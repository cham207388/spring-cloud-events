package com.abcham.notification.functions;

import com.abcham.notification.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class NotificationFunction {

    @Bean
    public Consumer<OrderInfo> updateOrder() {

        return orderStatus -> {
            // Implement your logic here
            log.info("Payment for order_id: {} is {}", orderStatus.id(), orderStatus.status());

        };
    }

}
