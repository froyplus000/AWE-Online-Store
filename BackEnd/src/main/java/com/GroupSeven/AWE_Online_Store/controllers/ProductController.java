package com.GroupSeven.AWE_Online_Store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Get all product
    @GetMapping("/all")
    public String getAllProducts() {
        return "All products";
    }

    // Get product by ID
    @GetMapping("/{id}")
    public String getProductById() {
        return "Product with ID";
    }
    // Post - Add new product
    @PostMapping("/add")
    public String addNewProduct() {
        return "New product added";
    }
    // Put - Update product
    @PutMapping("/update")
    public String updateProduct() {
        return "Product updated";
    }
    // Delete - Delete product
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        return "Product with ID " + id + " deleted";
    }

}
