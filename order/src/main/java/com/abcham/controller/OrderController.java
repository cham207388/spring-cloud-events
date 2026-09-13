package com.abcham.controller;

import com.abcham.entity.Order;
import com.abcham.model.OrderRequest;
import com.abcham.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService iOrderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest) {

        return iOrderService.createOrder(orderRequest);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(iOrderService.getOrders());
    }

}
