package com.GroupSeven.AWE_Online_Store.repository;

import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);
}
