package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.CartDTO;
import com.ecommerce.server_side.dto.CartItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CartServiceImplementation implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getCartByUserId(Long userId) {
        log.info("Fetching cart for user ID: {}", userId);
        
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("No cart found for user ID: {}, creating new cart", userId);
                    return createEmptyCart(userId);
                });
        
        log.info("Cart found with {} items", cart.getItems().size());
        CartDTO result = mapToDTO(cart);
        log.info("Mapped cart DTO with {} items", result.getItems().size());
        
        return result;
    }

    @Override
    public CartDTO addToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyCart(userId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .cart(cart)
                    .build();
            
            CartItem savedItem = cartItemRepository.save(newItem);
            cart.getItems().add(savedItem);
        }

        cart = cartRepository.save(cart);
        return mapToDTO(cart);
    }

    @Override
    public CartDTO updateItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                });

        cart = cartRepository.save(cart);
        return mapToDTO(cart);
    }

    @Override
    public CartDTO removeItem(Long userId, Long productId) {
        log.info("removeItem called: userId={}, productId={}", userId, productId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.warn("No cart for userId={}, creating empty cart for idempotent remove", userId);
                    return createEmptyCart(userId);
                });

        log.info("Cart has {} items before remove", cart.getItems().size());
        CartItem toRemove = cart.getItems().stream()
                .peek(ci -> log.info("Inspect item: productId={}, quantity={}", ci.getProduct().getId(), ci.getQuantity()))
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            cart.getItems().remove(toRemove);
            log.info("Removed item for productId={}", productId);
        } else {
            log.warn("No cart item found for productId={} — returning current cart (idempotent)", productId);
        }

        Cart saved = cartRepository.save(cart);
        log.info("Cart now has {} items after remove", saved.getItems().size());
        return mapToDTO(saved);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        // Clear items; orphanRemoval will delete children
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
        log.info("Mapping cart to DTO for user ID: {}", cart.getUser().getId());
        
        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> {
                    log.info("Mapping cart item: productId={}, quantity={}, productName={}", 
                            item.getProduct().getId(), item.getQuantity(), item.getProduct().getName());
                    
                    return CartItemDTO.builder()
                            .productId(item.getProduct().getId())
                            .name(item.getProduct().getName())
                            .description(item.getProduct().getDescription())
                            .price(item.getProduct().getPrice())
                            .imageUrl(item.getProduct().getImageUrl())
                            .quantity(item.getQuantity())
                            .build();
                })
                .toList();

        CartDTO result = CartDTO.builder()
                .userId(cart.getUser().getId())
                .items(items)
                .build();
        
        log.info("Mapped cart DTO with {} items for user ID: {}", items.size(), result.getUserId());
        return result;
    }
}
