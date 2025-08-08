package com.ecommerce.server_side.service;

import com.ecommerce.server_side.dto.CartDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addToCart(Long userId, Long productId, int quantity);
    CartDTO updateItem(Long userId, Long productId, int quantity);
    CartDTO removeItem(Long userId, Long productId);
    void clearCart(Long userId);
}
