package com.GroupSeven.AWE_Online_Store.repository;

import com.GroupSeven.AWE_Online_Store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
