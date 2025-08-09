package com.ecommerce.server_side.controller;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.dto.OrderStatusUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.server_side.service.OrderService;

import java.util.List;

@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://unibuy-shanmukha-thadavarthis-projects.vercel.app",
    "https://unibuy-git-main-shanmukha-thadavarthis-projects.vercel.app",
    "https://unibuy-six.vercel.app"
})
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderDTO placeOrder(@RequestBody OrderDTO dto) {
        log.info("Placing order for user ID: {}", dto.getUserId());
        return orderService.placeOrder(dto);
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable Long userId) {
        log.info("Getting orders for user ID: {}", userId);
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/{id}")
    public OrderDTO getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/checkout/{userId}")
    public OrderDTO checkout(@PathVariable Long userId) {
        log.info("Checkout initiated for user ID: {}", userId);
        try {
            OrderDTO result = orderService.checkoutCart(userId);
            log.info("Checkout successful for user ID: {}, order ID: {}", userId, result.getId());
            return result;
        } catch (Exception e) {
            log.error("Checkout failed for user ID: {}", userId, e);
            throw e;
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')") // Optional: protect for admins only
    public OrderDTO updateStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(id, request.getStatus());
    }

    @PutMapping("/{id}/payment")
    public OrderDTO updatePaymentStatus(@PathVariable Long id, @RequestParam String paymentId, @RequestParam String paymentStatus) {
        return orderService.updatePaymentStatus(id, paymentId, paymentStatus);
    }
}
