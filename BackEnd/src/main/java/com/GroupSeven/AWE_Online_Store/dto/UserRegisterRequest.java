package com.GroupSeven.AWE_Online_Store.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private String phone;
}
