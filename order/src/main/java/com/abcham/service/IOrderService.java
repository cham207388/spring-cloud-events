package com.abcham.service;

import com.abcham.entity.Order;
import com.abcham.model.OrderInfo;
import com.abcham.model.OrderRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {

    Order createOrder(OrderRequest order);

    boolean updateOrderStatus(OrderInfo info);

    List<Order> getOrders();

}
