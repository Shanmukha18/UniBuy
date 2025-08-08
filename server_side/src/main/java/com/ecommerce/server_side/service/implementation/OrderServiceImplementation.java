package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.server_side.repository.CartRepository;
import com.ecommerce.server_side.repository.CartItemRepository;
import com.ecommerce.server_side.repository.OrderRepository;
import com.ecommerce.server_side.repository.ProductRepository;
import com.ecommerce.server_side.repository.UserRepository;
import com.ecommerce.server_side.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImplementation implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public OrderDTO placeOrder(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Product> products = productRepository.findAllById(dto.getProductIds());

        Order order = Order.builder()
                .user(user)
                .products(products)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);

        return mapToDTO(saved);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    @Transactional
    public OrderDTO checkoutCart(Long userId) {
        log.info("Starting checkout process for user ID: {}", userId);
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for ID: {}", userId);
                    return new RuntimeException("User not found");
                });

        // Find cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user ID: {}", userId);
                    return new RuntimeException("Cart not found");
                });

        if (cart.getItems().isEmpty()) {
            log.error("Cart is empty for user ID: {}", userId);
            throw new RuntimeException("Cart is empty");
        }

        log.info("Cart found with {} items for user ID: {}", cart.getItems().size(), userId);

        // Extract products and calculate total
        List<Product> products = cart.getItems().stream()
                .map(CartItem::getProduct)
                .toList();

        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        log.info("Total amount calculated: {} for user ID: {}", totalAmount, userId);

        // Create order
        Order order = Order.builder()
                .user(user)
                .products(products)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .paymentStatus("PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} for user ID: {}", savedOrder.getId(), userId);

        // Clear the cart properly
        try {
            cart.getItems().clear();
            cartRepository.save(cart);
            log.info("Cart cleared successfully for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing cart for user ID: {}", userId, e);
            // Don't throw exception here as order is already created
        }

        return mapToDTO(savedOrder);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        return mapToDTO(updated);
    }

    @Override
    public OrderDTO updatePaymentStatus(Long orderId, String paymentId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setRazorpayPaymentId(paymentId);
        order.setPaymentStatus(paymentStatus);
        
        // Update order status based on payment status
        if ("COMPLETED".equals(paymentStatus)) {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        
        Order updated = orderRepository.save(order);
        return mapToDTO(updated);
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .productIds(order.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .razorpayOrderId(order.getRazorpayOrderId())
                .razorpayPaymentId(order.getRazorpayPaymentId())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }
}
