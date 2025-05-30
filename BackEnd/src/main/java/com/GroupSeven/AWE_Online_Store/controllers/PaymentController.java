package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.PaymentRequest;
import com.GroupSeven.AWE_Online_Store.dto.PaymentResponse;
import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.Payment;
import com.GroupSeven.AWE_Online_Store.repository.OrderRepository;
import com.GroupSeven.AWE_Online_Store.repository.PaymentRepository;
import com.GroupSeven.AWE_Online_Store.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            // Get the order
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            
            // Create payment
            Payment payment = Payment.builder()
                    .order(order)
                    .amount(request.getAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .status(request.getStatus())
                    .paymentDate(LocalDateTime.now())
                    .build();
            
            // Save payment
            Payment savedPayment = paymentRepository.save(payment);
            
            return ResponseEntity.ok(Map.of(
                "id", savedPayment.getId(),
                "status", savedPayment.getStatus(),
                "amount", savedPayment.getAmount(),
                "message", "Payment processed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
    
    // DTO for payment creation
    public static class CreatePaymentRequest {
        private Integer orderId;
        private BigDecimal amount;
        private String paymentMethod;
        private String status;
        
        // Getters and setters
        public Integer getOrderId() { return orderId; }
        public void setOrderId(Integer orderId) { this.orderId = orderId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
