package com.example.backend.service;

import com.example.backend.dto.request.order.PlaceOrderRequest;
import com.example.backend.dto.request.order.UpdateOrderStatusRequest;
import com.example.backend.dto.response.order.OrderItemResponse;
import com.example.backend.dto.response.order.OrderResponse;
import com.example.backend.dto.response.order.OrderSummaryResponse;
import com.example.backend.exception.*;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final UserService userService;
    private final CartService cartService;

    // POST /api/orders
    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request, String email) {
        User user = userService.getUserByEmail(email);
        Cart cart = cartService.getOrCreateCart(email);

        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException();
        }

        // validate stock and compute total
        Double totalAmount = 0.0;
        for (CartItem cartItem : cart.getCartItems()) {
            Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Inventory not found for product: " + cartItem.getProduct().getName()));

            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        cartItem.getProduct().getId(),
                        cartItem.getQuantity(),
                        inventory.getQuantity());
            }
            totalAmount = totalAmount + (cartItem.getProduct().getPrice() * cartItem.getQuantity());
        }

        // create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus("PENDING");
        order.setPlacedAt(LocalDateTime.now());
        orderRepository.save(order);

        // create order items and deduct inventory
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice()); // snapshot
            orderItems.add(orderItem);

            // deduct stock
            Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId()).get();
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventory.setUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);

        // clear cart after successful order
        cartService.clearCart(email);

        return mapToOrderResponse(order);
    }

    // GET /api/orders  – returns own orders for USER, all orders for ADMIN
    public List<OrderSummaryResponse> getOrders(String email) {
        User user = userService.getUserByEmail(email);

        List<Order> orders = user.getRole().equals("ADMIN")
                ? orderRepository.findAllByOrderByPlacedAtDesc()
                : orderRepository.findByUserIdOrderByPlacedAtDesc(user.getId());

        return orders.stream()
                .map(this::mapToOrderSummaryResponse)
                .collect(Collectors.toList());
    }

    // GET /api/orders/:id
    public OrderResponse getOrderById(Long id, String email) {
        User user = userService.getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        // users can only view their own orders; admins can view any
        if (!user.getRole().equals("ADMIN") &&
                !order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "Access denied: order " + id + " belongs to another user");
        }

        return mapToOrderResponse(order);
    }

    // PATCH /api/orders/:id/status  – admin
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        OrderStatus current = order.getStatus();
        OrderStatus next    = request.getStatus();
        boolean invalid =
                (current == OrderStatus.DELIVERED) ||
                        (current == OrderStatus.CANCELLED)  ||
                        (current == OrderStatus.DISPATCHED && next == OrderStatus.PENDING);
        if (invalid) {
            throw new InvalidOrderStatusException(current.name(), next.name());
        }
        order.setStatus(next);
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    // POST /api/orders/:id/cancel  – user
    @Transactional
    public OrderResponse cancelOrder(Long id, String email) {
        User user = userService.getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "Access denied: order " + id + " belongs to another user");
        }

        if (order.getStatus() == OrderStatus.DISPATCHED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException(id, order.getStatus().name());
        }

        // restore inventory on cancellation
        for (OrderItem item : order.getOrderItems()) {
            inventoryRepository.findByProductId(item.getProduct().getId()).ifPresent(inv -> {
                inv.setQuantity(inv.getQuantity() + item.getQuantity());
                inv.setUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(inv);
            });
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    // ── helpers ──────────────────────────────────────────────────
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getOrderItems()
                .stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .placedAt(order.getPlacedAt())
                .items(items)
                .build();
    }

    private OrderSummaryResponse mapToOrderSummaryResponse(Order order) {
        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getOrderItems().size())
                .placedAt(order.getPlacedAt())
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        Double subtotal = item.getUnitPrice() * item.getQuantity();

        return OrderItemResponse.builder()
                .orderItemId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }
}
