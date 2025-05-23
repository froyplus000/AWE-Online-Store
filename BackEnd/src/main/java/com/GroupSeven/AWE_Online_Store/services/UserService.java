package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.UserLoginRequest;
import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.User;

import java.util.Map;

public interface UserService {
    User registerUser(UserRegisterRequest request);
    Map<String, Object> login(UserLoginRequest request);
}
