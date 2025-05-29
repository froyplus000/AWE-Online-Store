package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.OrderItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.OrderResponse;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> placeOrder(Authentication authentication) {
        String email = authentication.getName();
        Order order = orderService.placeOrder(email);

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream().map(item ->
                new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                )
        ).toList();

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                itemResponses
        );

        return ResponseEntity.ok(response);
    }

}
