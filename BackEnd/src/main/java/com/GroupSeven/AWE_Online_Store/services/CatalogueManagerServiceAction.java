package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogueManagerServiceAction implements CatalogueManagerService{
    private final UniversalFactoryService universalFactoryService;

    @Autowired
    private ProductRepository productRepository;


    public CatalogueManagerServiceAction(UniversalFactoryService universalFactoryService, ProductRepository productRepository) {
        this.universalFactoryService = universalFactoryService;
        this.productRepository = productRepository;
    }
    // Add a new product with Universal Factory Service
    @Override
    public Product addProduct(ProductCreationRequest request) {
        return universalFactoryService.createProduct(request);
    }
    // Update product
    @Override
    public Product updateProduct(Integer id, ProductCreationRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(request.getName());
        existing.setCategory(request.getCategory());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setImageUrl(request.getImageUrl());

        return productRepository.save(existing);
    }
    // Delete product by ID
    @Override
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
}
