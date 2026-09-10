package com.abcham.service.impl;

import com.abcham.model.Order;
import com.abcham.model.OrderRequest;
import com.abcham.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final StreamBridge streamBridge;

    @Override
    public Order createOrder(OrderRequest orderRequest) {

        log.info("Received order created event for order: {}", orderRequest);
        Order order = toNewOrder(orderRequest);
        log.info("saving order: {}", order);
        publishOrder(orderRequest);
        return order;
    }

    private void publishOrder(OrderRequest orderRequest) {

        log.info("Publishing order: {}, for payment", orderRequest);
        streamBridge.send("createOrder-out-0", orderRequest);
    }

    @Override
    public boolean updateOrderStatus(Long orderId) {

        log.info("Received order status update event for orderId: {}", orderId);
        return true;
    }

    private Order toNewOrder(OrderRequest orderRequest) {

        return new Order(orderRequest.id(), orderRequest.customerName(), orderRequest.totalAmount(), "PENDING");
    }

}
