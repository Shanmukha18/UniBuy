package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.CartDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.CartService;
import com.ecommerce.server_side.repository.CartRepository;
import com.ecommerce.server_side.repository.CartItemRepository;
import com.ecommerce.server_side.repository.UserRepository;
import com.ecommerce.server_side.repository.ProductRepository;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/{userId}")
    public CartDTO getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @GetMapping("/debug/{userId}")
    public Object debugCart(@PathVariable Long userId) {
        log.info("Debug endpoint called for user ID: {}", userId);
        
        var user = userRepository.findById(userId);
        var cart = cartRepository.findByUserId(userId);
        var allCarts = cartRepository.findAll();
        var allCartItems = cartItemRepository.findAll();
        var allProducts = productRepository.findAll();
        
        return Map.of(
            "user", user.orElse(null),
            "cart", cart.orElse(null),
            "allCarts", allCarts,
            "allCartItems", allCartItems,
            "allProducts", allProducts,
            "userExists", user.isPresent(),
            "cartExists", cart.isPresent()
        );
    }

    @PostMapping("/{userId}/add/{productId}")
    public CartDTO addToCart(@PathVariable Long userId,
                             @PathVariable Long productId,
                             @RequestParam int quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @PutMapping("/{userId}/update/{productId}")
    public CartDTO updateItem(@PathVariable Long userId,
                              @PathVariable Long productId,
                              @RequestParam int quantity) {
        return cartService.updateItem(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public CartDTO removeItem(@PathVariable Long userId,
                              @PathVariable Long productId) {
        return cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
    }
}
