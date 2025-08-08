package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.dto.OrderStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderDTO placeOrder(@RequestBody OrderDTO dto) {
        return orderService.placeOrder(dto);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/checkout/{userId}")
    public OrderDTO checkout(@PathVariable Long userId) {
        return orderService.checkoutCart(userId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')") // Optional: protect for admins only
    public OrderDTO updateStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }
}
