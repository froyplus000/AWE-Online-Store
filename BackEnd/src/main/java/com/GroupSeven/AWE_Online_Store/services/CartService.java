package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.AddToCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.CartItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.RemoveFromCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.UpdateCartRequest;

import java.util.List;

public interface CartService {
    List<CartItemResponse> getCartItems(String email);
    void addToCart(String userEmail, AddToCartRequest request);
    void updateCartItem(String email, UpdateCartRequest request);
    void removeFromCart(String email, RemoveFromCartRequest request);

}
