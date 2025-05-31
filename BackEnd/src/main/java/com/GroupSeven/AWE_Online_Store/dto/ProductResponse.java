package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {
    private Integer id;
    private String name;
    private String category;
    private String description;
    private BigDecimal price;
    private String imageUrl;
}
