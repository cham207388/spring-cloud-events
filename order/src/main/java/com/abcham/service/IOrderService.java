package com.abcham.service;

import com.abcham.entity.Order;
import com.abcham.model.OrderInfo;
import com.abcham.model.OrderRequest;

public interface IOrderService {

    Order createOrder(OrderRequest order);

    boolean updateOrderStatus(OrderInfo info);

}
