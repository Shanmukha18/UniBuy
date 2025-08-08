package com.ecommerce.server_side.dto;

import com.ecommerce.server_side.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private LocalDateTime orderDate;
    private OrderStatus status;
}
