package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.UserLoginRequest;
import com.GroupSeven.AWE_Online_Store.dto.UserRegisterRequest;
import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.services.UserService;
import com.GroupSeven.AWE_Online_Store.util.JwtUtil; // New
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            // Use the existing login method that already generates token
            Map<String, Object> loginResponse = userService.login(request);
            
            // Restructure response to match frontend expectations
            return ResponseEntity.ok(Map.of(
                    "user", Map.of(
                            "id", loginResponse.get("id"),
                            "email", loginResponse.get("email"),
                            "role", loginResponse.get("role").toString(),
                            "fullName", "" // Add if available
                    ),
                    "token", loginResponse.get("token"),
                    "tokenType", "Bearer"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            Map<String, Object> userInfo = userService.getCurrentUser(request);
            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
