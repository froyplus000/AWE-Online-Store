package com.GroupSeven.AWE_Online_Store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Integer orderId;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
}
