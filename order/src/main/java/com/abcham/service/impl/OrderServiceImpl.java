package com.abcham.service.impl;

import com.abcham.model.OrderInfo;
import com.abcham.repository.OrderRepository;
import com.abcham.entity.Order;
import com.abcham.model.OrderRequest;
import com.abcham.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final StreamBridge streamBridge;
    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(OrderRequest orderRequest) {

        log.info("Received order created event for order: {}", orderRequest);
        Order order = toNewOrder(orderRequest);
        orderRepository.save(order);
        publishOrder(orderRequest);
        return order;
    }

    private void publishOrder(OrderRequest orderRequest) {

        log.info("Publishing order: {}, for payment", orderRequest);
        streamBridge.send("createOrder-out-0", orderRequest);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(OrderInfo info) {

        Order savedOrder = orderRepository.findById(info.id())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        savedOrder.setStatus(info.status());
//        orderRepository.save(savedOrder);

        log.info("Received order status update event for orderId: {}", info.id());
        return true;
    }

    private Order toNewOrder(OrderRequest orderRequest) {

        return new Order(orderRequest.id(), orderRequest.customerName(), orderRequest.totalAmount(), "PENDING");
    }

}
