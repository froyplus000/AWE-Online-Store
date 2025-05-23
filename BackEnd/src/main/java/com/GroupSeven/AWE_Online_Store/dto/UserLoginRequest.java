package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}
