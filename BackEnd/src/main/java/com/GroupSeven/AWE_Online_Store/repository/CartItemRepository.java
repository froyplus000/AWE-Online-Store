package com.GroupSeven.AWE_Online_Store.repository;

import com.GroupSeven.AWE_Online_Store.entity.CartItem;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    List<CartItem> findByUser(User user);

}
