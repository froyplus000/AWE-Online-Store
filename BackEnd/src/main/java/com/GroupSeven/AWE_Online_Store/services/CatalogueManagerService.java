package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.ProductCreationRequest;
import com.GroupSeven.AWE_Online_Store.entity.Product;

public interface CatalogueManagerService {
    Product addProduct(ProductCreationRequest request);
    Product updateProduct (Integer id, ProductCreationRequest request);
    void deleteProduct(Integer id);
}
