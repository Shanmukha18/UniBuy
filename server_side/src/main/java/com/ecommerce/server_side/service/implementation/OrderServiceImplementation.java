package com.ecommerce.server_side.service.implementation;

import com.ecommerce.server_side.dto.OrderDTO;
import com.ecommerce.server_side.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ecommerce.server_side.repository.CartRepository;
import com.ecommerce.server_side.repository.OrderRepository;
import com.ecommerce.server_side.repository.ProductRepository;
import com.ecommerce.server_side.repository.UserRepository;
import com.ecommerce.server_side.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

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
    public OrderDTO checkoutCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = cart.getUser();
        List<Product> products = cart.getItems().stream()
                .map(CartItem::getProduct)
                .toList();

        Order order = Order.builder()
                .user(user)
                .products(products)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // Clear the cart
        cart.getItems().clear();
        cartRepository.save(cart);

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

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .productIds(order.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .build();
    }
}
