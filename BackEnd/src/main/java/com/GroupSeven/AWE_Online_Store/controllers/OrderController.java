package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.OrderItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.OrderResponse;
import com.GroupSeven.AWE_Online_Store.dto.UpdateOrderStatusRequest;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.repository.UserRepository;
import com.GroupSeven.AWE_Online_Store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user.getRole() == User.Role.ADMIN) {
            return ResponseEntity.ok(orderService.getAllOrders());
        } else {
            return ResponseEntity.ok(orderService.getOrdersForUser(email));
        }
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        String email = authentication.getName(); // logged-in user's email
        OrderResponse response = orderService.getOrderById(email, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody UpdateOrderStatusRequest request
    ) {
        OrderResponse updated = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }



}
