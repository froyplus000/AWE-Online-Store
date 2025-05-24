package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.entity.User;

public interface UniversalFactoryService {
    User registerNewCustomer(UserRegisterRequest request);

}
