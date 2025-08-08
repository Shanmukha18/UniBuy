package com.ecommerce.server_side.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private int quantity;
}
