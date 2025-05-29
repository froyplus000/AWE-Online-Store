package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.entity.User;

import java.math.BigDecimal;


// ONLY HANDLE CREATION LOGIC -> FACTORY PATTERN
public interface UniversalFactoryService {
    User registerNewCustomer(UserRegisterRequest request);
    Product createProduct(ProductCreationRequest request);
    Order createOrder(User user, BigDecimal totalPrice);
}
