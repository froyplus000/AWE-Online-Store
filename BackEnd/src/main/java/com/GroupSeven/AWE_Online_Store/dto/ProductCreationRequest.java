package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreationRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;
}
