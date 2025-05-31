package com.GroupSeven.AWE_Online_Store.repository;

import com.GroupSeven.AWE_Online_Store.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
}
