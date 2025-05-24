package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class CatalogueManagerServiceAction implements CatalogueManagerService{
    private final UniversalFactoryService universalFactoryService;

    public CatalogueManagerServiceAction(UniversalFactoryService universalFactoryService) {
        this.universalFactoryService = universalFactoryService;
    }

    @Override
    public Product addProduct(ProductCreationRequest request) {
        return universalFactoryService.createProduct(request);
    }
}
