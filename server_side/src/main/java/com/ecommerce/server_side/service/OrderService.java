package com.ecommerce.server_side.service;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.model.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDTO placeOrder(OrderDTO dto);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByUser(Long userId);
    OrderDTO getOrderById(Long id);
    OrderDTO checkoutCart(Long userId);
    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
}
