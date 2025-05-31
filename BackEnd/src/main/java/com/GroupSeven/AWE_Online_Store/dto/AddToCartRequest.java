package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Integer productId;
    private Integer quantity;
}

