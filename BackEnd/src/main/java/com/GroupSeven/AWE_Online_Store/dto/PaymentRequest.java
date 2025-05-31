package com.GroupSeven.AWE_Online_Store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PaymentRequest {
    private Integer orderId;
    private String paymentMethod;
    private BigDecimal amount;
}
