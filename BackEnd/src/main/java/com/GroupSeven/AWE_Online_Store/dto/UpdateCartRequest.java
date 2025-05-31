package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Integer productId;
    private Integer quantity;
}

