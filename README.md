# Spring Cloud Events

Event-driven microservices architecture using **Spring Cloud Stream** and **Spring Cloud Function** backed by **Apache Kafka** and **PostgreSQL**.

---

## Architecture & Event Flow

```text
[Client] 
   │  POST /api/orders
   ▼
[Order Service] ──── StreamBridge ────► [Kafka: order-created]
   ▲                                            │
   │                                            ▼
   │                                   [Payment Service] (Function)
   │                                            │
   │                                            ▼
   └─────────────────────────────────── [Kafka: payment-processed]
                                                │
                                                ▼
                                      [Notification Service] (Consumer)
```

1. **Order Creation**: Client calls `POST /api/orders` with `customerName` and `totalAmount`. `Order Service` persists the order (status `PENDING`) with an auto-generated ID and publishes an `OrderInfo` event (`id`, `PENDING`) to topic `order-created` via `StreamBridge`.
2. **Payment Processing**: `Payment Service` consumes `OrderInfo` from `order-created`, updates status to `PAID`, and publishes the updated `OrderInfo` to topic `payment-processed`.
3. **Order Update**: `Order Service` consumes `OrderInfo` from `payment-processed` and updates the order status to `PAID` in PostgreSQL.
4. **Notification**: `Notification Service` consumes `OrderInfo` from `payment-processed` and triggers customer email and delivery notifications.

---

## Spring Cloud Function Core Concepts

Spring Cloud Stream uses **Spring Cloud Function** to bind standard Java functional interfaces (`java.util.function`) to message brokers:

| Interface        | Stream Role            | Behavior                                            | Project Example                                              | Binding Convention                  |
|:-----------------|:-----------------------|:----------------------------------------------------|:-------------------------------------------------------------|:------------------------------------|
| `Supplier<O>`    | **Producer**           | Generates events periodically or on poll            | —                                                            | `<fnName>-out-0`                    |
| `Function<I, O>` | **Processor**          | Consumes input event, outputs new event             | `processOrder` (`payment`)                                   | `<fnName>-in-0`<br>`<fnName>-out-0` |
| `Consumer<I>`    | **Sink**               | Consumes input event, produces no output            | `updateOrder` (`order`), `sendNotification` (`notification`) | `<fnName>-in-0`                     |
| `StreamBridge`   | **On-Demand Producer** | Imperatively sends messages outside functional flow | `publishOrder` (`order`)                                     | `<bindingName>-out-0`               |

### Binding Conventions
- **Inputs**: `<functionName>-in-<index>` (maps to consumer destination/topic)
- **Outputs**: `<functionName>-out-<index>` (maps to producer destination/topic)
- **Function Activation**: `spring.cloud.function.definition` specifies active functional bean names (multiple can be composed using `|`).

---

## Services

### 1. Order Service (`order`)
- **Role**: REST Entry Point + Event Producer (`StreamBridge`) + Event Consumer (`Consumer<OrderInfo>`)
- **Database**: PostgreSQL with Flyway migrations (`orders_space` schema, `orders` table)
- **Function Definition**: `updateOrder`
- **Bindings**:
  - `createOrder-out-0` ➔ `order-created` (via `StreamBridge`)
  - `updateOrder-in-0` ➔ `payment-processed` (group: `order`)
- **Key Files**:
  - `OrderFunction.java`: `Consumer<OrderInfo> updateOrder(IOrderService)`
  - `OrderServiceImpl.java`: `streamBridge.send("createOrder-out-0", orderInfo)`
  - `OrderController.java`: `POST /api/orders`
  - `application.yaml`: Binding and function configurations

### 2. Payment Service (`payment`)
- **Role**: Event Processor (`Function<OrderInfo, OrderInfo>`)
- **Function Definition**: `processOrder`
- **Bindings**:
  - `processOrder-in-0` ➔ `order-created` (group: `payment`)
  - `processOrder-out-0` ➔ `payment-processed`
- **Key Files**:
  - `PaymentFunction.java`: `Function<OrderInfo, OrderInfo> processOrder()`
  - `OrderInfo.java`: Event payload model (`id`, `status`)
  - `Configs.java`: `ObjectMapper` bean
  - `application.yaml`: Input and output binding declarations

### 3. Notification Service (`notification`)
- **Role**: Event Sink (`Consumer<OrderInfo>`)
- **Function Definition**: `sendNotification`
- **Bindings**:
  - `sendNotification-in-0` ➔ `payment-processed` (group: `notification`)
- **Key Files**:
  - `NotificationFunction.java`: `Consumer<OrderInfo> sendNotification()`
  - `Configs.java`: `ObjectMapper` bean
  - `application.yml`: Input binding configuration

---

## Quick Start

### 1. Start Infrastructure
```bash
make compose-up
# Starts Kafka on :9092 and PostgreSQL on :5423
```

### 2. Run Services
Run each service in a separate terminal:
```bash
# 1. Order Service (default port 8080)
cd order && ./gradlew bootRun

# 2. Payment Service
cd payment && ./gradlew bootRun

# 3. Notification Service
cd notification && ./gradlew bootRun
```

### 3. Test the Flow
Submit a test order:
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "totalAmount": 99.99
  }'
```