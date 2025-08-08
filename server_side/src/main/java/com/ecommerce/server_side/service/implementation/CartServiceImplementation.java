package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.CartDTO;
import com.ecommerce.server_side.dto.CartItemDTO;
import lombok.RequiredArgsConstructor;
import com.ecommerce.server_side.model.Cart;
import com.ecommerce.server_side.model.CartItem;
import com.ecommerce.server_side.model.Product;
import com.ecommerce.server_side.model.User;
import org.springframework.stereotype.Service;
import com.ecommerce.server_side.repository.CartItemRepository;
import com.ecommerce.server_side.repository.CartRepository;
import com.ecommerce.server_side.repository.ProductRepository;
import com.ecommerce.server_side.repository.UserRepository;
import com.ecommerce.server_side.service.CartService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImplementation implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));
        return mapToDTO(cart);
    }

    @Override
    public CartDTO addToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .cart(cart)
                    .build();
            cart.getItems().add(newItem);
        }

        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        return mapToDTO(cartRepository.save(cart));
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createEmptyCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }

    private CartDTO mapToDTO(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> new CartItemDTO(item.getProduct().getId(), item.getQuantity()))
                .toList();

        return CartDTO.builder()
                .userId(cart.getUser().getId())
                .items(items)
                .build();
    }
}
