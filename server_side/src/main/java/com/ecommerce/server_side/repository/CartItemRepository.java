package com.ecommerce.server_side.repository;

import com.ecommerce.server_side.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
