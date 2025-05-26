package com.GroupSeven.AWE_Online_Store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemResponse
{
    private String productName;
    private int quantity;
    private double price;
    private double total;  // quantity * price
}
