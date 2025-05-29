package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.OrderResponse;
import com.GroupSeven.AWE_Online_Store.entity.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(String userEmail);
    List<OrderResponse> getOrdersForUser(String userEmail);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String userEmail, Integer orderId);
}
