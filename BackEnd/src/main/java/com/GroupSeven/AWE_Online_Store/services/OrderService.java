package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.entity.Order;

public interface OrderService {
    Order placeOrder(String userEmail);
}
