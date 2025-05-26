package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.dto.ProductResponse;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.services.CatalogueManagerService;
import com.GroupSeven.AWE_Online_Store.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {



    private final CatalogueManagerService catalogueManagerService;
    private final ProductService productService;


    public ProductController(CatalogueManagerService catalogueManagerService, ProductService productService) {
        this.productService = productService;
        this.catalogueManagerService = catalogueManagerService;
    }

    // Get all product
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }


    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // Post - Add new product
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addNewProduct(@RequestBody ProductCreationRequest request) {
        Product created = catalogueManagerService.addProduct(request);
        return ResponseEntity.ok(created);
    }
    // Put - Update product
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductCreationRequest request) {
        Product updated = catalogueManagerService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }
    // Delete - Delete product
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        catalogueManagerService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

}
