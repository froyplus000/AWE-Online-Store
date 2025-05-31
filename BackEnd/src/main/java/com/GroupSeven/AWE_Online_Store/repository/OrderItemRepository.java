package com.GroupSeven.AWE_Online_Store.repository;

import com.GroupSeven.AWE_Online_Store.entity.Order;
import com.GroupSeven.AWE_Online_Store.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findByOrder(Order order);
}
