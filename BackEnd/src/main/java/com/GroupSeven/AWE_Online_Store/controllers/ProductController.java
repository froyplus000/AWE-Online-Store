package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.services.CatalogueManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {



    private final CatalogueManagerService catalogueManagerService;


    public ProductController(CatalogueManagerService catalogueManagerService) {
        this.catalogueManagerService = catalogueManagerService;
    }

    // Get all product
//    @GetMapping("/all")
//    public String getAllProducts() {
//        return "All products";
//    }

    // Get product by ID
//    @GetMapping("/{id}")
//    public String getProductById() {
//        return "Product with ID";
//    }
    // Post - Add new product
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNewProduct(@RequestBody ProductCreationRequest request) {
        Product created = catalogueManagerService.addProduct(request);
        return ResponseEntity.ok(created);
    }
    // Put - Update product
//    @PutMapping("/update/{id}")
//    public String updateProduct() {
//        return "Product updated";
//    }
    // Delete - Delete product
//    @DeleteMapping("/delete/{id}")
//    public String deleteProduct(@PathVariable("id") int id) {
//        return "Product with ID " + id + " deleted";
//    }

}
