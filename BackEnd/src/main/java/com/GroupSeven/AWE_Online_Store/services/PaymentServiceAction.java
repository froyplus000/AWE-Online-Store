package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.PaymentRequest;
import com.GroupSeven.AWE_Online_Store.dto.PaymentResponse;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.Payment;
import com.GroupSeven.AWE_Online_Store.repository.OrderRepository;
import com.GroupSeven.AWE_Online_Store.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceAction implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final UniversalFactoryService factory;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Payment payment = factory.createPayment(request, order); // using UniversalFactory
        Payment saved = paymentRepository.save(payment);
        orderService.updateOrderStatus(request.getOrderId(), "PAID");
        return PaymentResponse.builder()
                .orderId(saved.getOrder().getId())
                .status(String.valueOf(saved.getStatus()))
                .paymentMethod(saved.getPaymentMethod())
                .paymentDate(saved.getPaymentDate())
                .amount(saved.getAmount())
                .build();
    }
}
