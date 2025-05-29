package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.entity.*;
import com.GroupSeven.AWE_Online_Store.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceAction implements OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UniversalFactoryService universalFactoryService;

    @Autowired
    public OrderServiceAction(
            UserRepository userRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            UniversalFactoryService universalFactoryService
    ) {
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.universalFactoryService = universalFactoryService;
    }

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

        // 7. Create an initial payment entry
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStatus(Payment.PaymentStatus.UNPAID);
        payment.setPaymentMethod("NONE"); // Will be updated on payment
        paymentRepository.save(payment);

        // 8. Clear the user's cart
        cartItemRepository.deleteAll(cartItems);

        return order;
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
