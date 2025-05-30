package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.CreateOrderRequest;
import com.GroupSeven.AWE_Online_Store.dto.OrderResponse;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.User;

import java.util.List;

public interface OrderService {
    Order placeOrder(String userEmail);
    List<OrderResponse> getOrdersForUser(String userEmail);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(String userEmail, Integer orderId);
    OrderResponse updateOrderStatus(Integer orderId, String newStatus);
    
    
    Order createOrder(User user, CreateOrderRequest request);
}
