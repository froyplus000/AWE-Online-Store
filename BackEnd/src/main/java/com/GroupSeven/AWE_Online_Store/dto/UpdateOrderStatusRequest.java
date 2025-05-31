package com.GroupSeven.AWE_Online_Store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private String status;
}
