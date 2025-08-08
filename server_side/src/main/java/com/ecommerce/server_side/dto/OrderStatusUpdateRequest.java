package com.ecommerce.server_side.dto;

import com.ecommerce.server_side.model.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
