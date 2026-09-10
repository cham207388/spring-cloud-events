package com.abcham.controller;

import com.abcham.model.Order;
import com.abcham.model.OrderRequest;
import com.abcham.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService iOrderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest) {

        return iOrderService.createOrder(orderRequest);
    }

}
