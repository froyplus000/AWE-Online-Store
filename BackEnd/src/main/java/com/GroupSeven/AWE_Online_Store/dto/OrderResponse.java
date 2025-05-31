package com.GroupSeven.AWE_Online_Store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private BigDecimal totalPrice;
    private String status;
    private List<OrderItemResponse> items;
}
