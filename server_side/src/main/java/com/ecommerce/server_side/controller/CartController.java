package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.CartDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.CartService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public CartDTO getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
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
