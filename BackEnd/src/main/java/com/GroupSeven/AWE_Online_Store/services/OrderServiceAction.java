package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.OrderItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.OrderResponse;
import com.GroupSeven.AWE_Online_Store.entity.*;
import com.GroupSeven.AWE_Online_Store.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OrderServiceAction implements OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UniversalFactoryService universalFactoryService;

    @Override
    public Order placeOrder(String userEmail) {
        // 1. Get the user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get user's cart
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 3. Calculate total price
        BigDecimal totalPrice = calculateTotal(cartItems);

        // 4. Create an Order using a factory
        Order order = universalFactoryService.createOrder(user, totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        // 5. Save the order first (to generate ID)
        order = orderRepository.save(order);

        // 6. Create and save OrderItems
        List<OrderItem> orderItems = createOrderItems(order, cartItems);
        order.getOrderItems().addAll(orderItems);

        // 7. Clear the user's cart
        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    @Override
    public List<OrderResponse> getOrdersForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                items
        );
    }

    @Override
    public OrderResponse getOrderById(String email, Integer orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String userRole = String.valueOf(user.getRole());
        User orderOwner = order.getUser();
        if (Objects.equals(userRole, "CUSTOMER")) {
            if (!Objects.equals(user.getId(), orderOwner.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view this order");
            }
        }
        return mapToOrderResponse(order); // reuse from earlier
    }

    @Override
    public OrderResponse updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus statusEnum;
        try {
            statusEnum = Order.OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
        }

        order.setStatus(statusEnum);
        orderRepository.save(order);

        return mapToOrderResponse(order); // reuse existing mapping method
    }



    private BigDecimal calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItem> createOrderItems(Order order, List<CartItem> cartItems) {
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            return orderItem;
        }).toList();

        return orderItemRepository.saveAll(orderItems);
    }
}
