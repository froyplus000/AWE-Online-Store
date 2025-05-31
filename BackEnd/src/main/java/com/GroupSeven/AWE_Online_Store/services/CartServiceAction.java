package com.GroupSeven.AWE_Online_Store.services;

import com.GroupSeven.AWE_Online_Store.dto.AddToCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.CartItemResponse;
import com.GroupSeven.AWE_Online_Store.dto.RemoveFromCartRequest;
import com.GroupSeven.AWE_Online_Store.dto.UpdateCartRequest;
import com.GroupSeven.AWE_Online_Store.entity.CartItem;
import com.GroupSeven.AWE_Online_Store.entity.Product;
import com.GroupSeven.AWE_Online_Store.entity.User;
import com.GroupSeven.AWE_Online_Store.repository.CartItemRepository;
import com.GroupSeven.AWE_Online_Store.repository.ProductRepository;
import com.GroupSeven.AWE_Online_Store.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceAction implements CartService{

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceAction(UserRepository userRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public List<CartItemResponse> getCartItems(String email) {
        // 1. Find a user by email (from token)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Get all cart items for this user
        List<CartItem> items = cartItemRepository.findByUser(user);

        // 3. Map to response DTO
        return items.stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice().doubleValue(), // price
                        item.getProduct()
                                .getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                                .doubleValue() // total
                ))
                .toList();
    }

    @Override
    public void addToCart(String userEmail, AddToCartRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public void updateCartItem(String email, UpdateCartRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(String email, RemoveFromCartRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        cartItemRepository.deleteAll(cartItems);
    }
}
