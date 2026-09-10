package com.abcham.payment.function;

import com.abcham.payment.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Slf4j
@Configuration
public class PaymentFunction {

    @Bean
    public Function<OrderRequest, String> processOrder() {
        return orderRequest -> {
            // Implement your logic here
            log.info("Processing order: {}", orderRequest);
            // For example, you can return "PROCESSED" if the order is processed successfully
            return "PROCESSED";
        };
    }

}
