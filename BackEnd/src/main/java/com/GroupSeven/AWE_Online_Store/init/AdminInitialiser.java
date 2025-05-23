package com.GroupSeven.AWE_Online_Store.init;

import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitialiser {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @PostConstruct
    public void createAdminAccount() {
        String adminEmail = "admin@awe.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFullName("Default Admin");
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // default password
            admin.setRole(User.Role.ADMIN);
            admin.setAddress("System");
            admin.setPhone("0000000000");

            userRepository.save(admin);
            System.out.println("✅ Default admin account created: " + adminEmail);
        } else {
            System.out.println("ℹ️ Admin account already exists: " + adminEmail);
        }
    }
}
