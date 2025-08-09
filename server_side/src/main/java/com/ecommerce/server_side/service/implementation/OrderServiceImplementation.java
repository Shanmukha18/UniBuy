package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.dto.OrderItemDTO;
import com.ecommerce.server_side.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.server_side.repository.CartRepository;
import com.ecommerce.server_side.repository.CartItemRepository;
import com.ecommerce.server_side.repository.OrderRepository;
import com.ecommerce.server_side.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
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

        // Calculate total
        double totalAmount = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        log.info("Total amount calculated: {} for user ID: {}", totalAmount, userId);

        // Create order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .paymentStatus("PENDING")
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {} for user ID: {}", savedOrder.getId(), userId);

        // Create order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(savedOrder)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .build())
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);
        log.info("Order items created for order ID: {}", savedOrder.getId());

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
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);

        // If order is being confirmed, reduce stock
        if (oldStatus != OrderStatus.CONFIRMED && newStatus == OrderStatus.CONFIRMED) {
            reduceStockForOrder(orderId);
        }

        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public OrderDTO updatePaymentStatus(Long orderId, String paymentId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setRazorpayPaymentId(paymentId);
        order.setPaymentStatus(paymentStatus);
        
        // Update order status based on payment status
        if ("COMPLETED".equals(paymentStatus)) {
            order.setStatus(OrderStatus.CONFIRMED);
            // Reduce stock when payment is completed
            reduceStockForOrder(orderId);
        }
        
        Order updated = orderRepository.save(order);
        return mapToDTO(updated);
    }

    @Transactional
    private void reduceStockForOrder(Long orderId) {
        log.info("Reducing stock for order ID: {}", orderId);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int currentStock = product.getStock();
            int orderedQuantity = orderItem.getQuantity();
            
            if (currentStock < orderedQuantity) {
                log.error("Insufficient stock for product ID: {}. Available: {}, Requested: {}", 
                    product.getId(), currentStock, orderedQuantity);
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            product.setStock(currentStock - orderedQuantity);
            productRepository.save(product);
            log.info("Reduced stock for product ID: {} by {}. New stock: {}", 
                product.getId(), orderedQuantity, product.getStock());
        }
    }

    private OrderDTO mapToDTO(Order order) {
        List<Long> productIds = order.getOrderItems() != null ? 
            order.getOrderItems().stream()
                .map(orderItem -> orderItem.getProduct().getId())
                .collect(Collectors.toList()) : 
            List.of();

        List<OrderItemDTO> orderItemDTOs = order.getOrderItems() != null ?
            order.getOrderItems().stream()
                .map(orderItem -> OrderItemDTO.builder()
                    .id(orderItem.getId())
                    .productId(orderItem.getProduct().getId())
                    .productName(orderItem.getProduct().getName())
                    .productImageUrl(orderItem.getProduct().getImageUrl())
                    .productPrice(orderItem.getProduct().getPrice())
                    .quantity(orderItem.getQuantity())
                    .build())
                .collect(Collectors.toList()) :
            List.of();

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .productIds(productIds)
                .orderItems(orderItemDTOs)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .razorpayOrderId(order.getRazorpayOrderId())
                .razorpayPaymentId(order.getRazorpayPaymentId())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }
}
