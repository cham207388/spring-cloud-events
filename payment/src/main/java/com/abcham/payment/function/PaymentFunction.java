package com.abcham.payment.function;

import com.abcham.payment.model.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Slf4j
@Configuration
public class PaymentFunction {

    @Bean
    public Function<OrderInfo, OrderInfo> processOrder() {
        return orderInfo -> {
            // Implement your logic here
            log.info("Processing order: {}", orderInfo);
            // Check payment status and update order status accordingly
            orderInfo.setStatus("PAID");
            log.info("Processing order: {}", orderInfo);
            return orderInfo;
        };
    }

}
