package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.UserLoginRequest;
import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.repository.UserRepository;
import com.GroupSeven.AWE_Online_Store.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceAction implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceAction(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UniversalFactoryService universalFactoryService;

    @Override
    public User registerUser(UserRegisterRequest request) {
        return universalFactoryService.registerNewCustomer(request);
    }

    @Override
    public Map<String, Object> login(UserLoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("id", user.getId()); // included for API compatibility

        return response;
    }

    @Override
    public Map<String, Object> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        String email = jwtUtil.getEmailFromToken(token);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("fullName", user.getFullName());
        response.put("address", user.getAddress());
        response.put("phone", user.getPhone());

        return response;
    }

    @Override
    public User loginAndReturnUser(UserLoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }
}
