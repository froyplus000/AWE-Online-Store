package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.repository.ProductRepository;
import com.GroupSeven.AWE_Online_Store.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UniversalFactoryServiceAction implements UniversalFactoryService {
    // All Repository - interacting with database
    private final UserRepository userRepository;
    // For password Hashing
    private final BCryptPasswordEncoder passwordEncoder;


    public UniversalFactoryServiceAction(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewCustomer(UserRegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.CUSTOMER);
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());

        return userRepository.save(user);
    }

    
}
