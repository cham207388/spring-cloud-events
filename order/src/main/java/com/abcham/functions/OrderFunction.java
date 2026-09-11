package com.abcham.functions;

import com.abcham.model.OrderInfo;
import com.abcham.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class OrderFunction {

    @Bean
    public Consumer<OrderInfo> updateOrder(IOrderService orderService) {
        return orderStatus -> {
            // Implement your logic here
            log.info("Processing order: {}", orderStatus);
            boolean result = orderService.updateOrderStatus(orderStatus);
            if (result) {
                log.info("Order status updated successfully");
            } else {
                log.error("Failed to update order status");
            }
        };
    }

}
