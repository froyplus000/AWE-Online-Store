package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.PaymentRequest;
import com.GroupSeven.AWE_Online_Store.dto.PaymentResponse;
import com.GroupSeven.AWE_Online_Store.services.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
}
