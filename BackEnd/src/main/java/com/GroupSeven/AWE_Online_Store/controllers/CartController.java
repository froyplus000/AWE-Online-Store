package com.GroupSeven.AWE_Online_Store.controllers;

import com.GroupSeven.AWE_Online_Store.dto.AddToCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.CartItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.RemoveFromCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.UpdateCartRequest;
import com.GroupSeven.AWE_Online_Store.services.CartService;
import com.GroupSeven.AWE_Online_Store.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartItemResponse>> getCartItems() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName(); // ← email from JWT

        List<CartItemResponse> cartItems = cartService.getCartItems(email);

        return ResponseEntity.ok(cartItems);
    }


    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addToCart(
            @RequestBody AddToCartRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();  // ← this now holds the user's email

        cartService.addToCart(email, request);

        return ResponseEntity.ok("Item added to cart");
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateCart(
            @RequestBody UpdateCartRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        cartService.updateCartItem(email, request);

        return ResponseEntity.ok("Cart item updated");
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> removeFromCart(
            @RequestBody RemoveFromCartRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        cartService.removeFromCart(email, request);

        return ResponseEntity.ok("Item removed from cart");
    }



}
