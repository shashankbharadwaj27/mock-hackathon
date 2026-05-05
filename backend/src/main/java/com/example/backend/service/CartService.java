package com.example.backend.service;

import com.example.backend.dto.request.cart.AddToCartRequest;
import com.example.backend.dto.request.cart.UpdateCartItemRequest;
import com.example.backend.dto.response.cart.CartItemResponse;
import com.example.backend.dto.response.cart.CartResponse;
import com.example.backend.exception.CartOwnershipException;
import com.example.backend.exception.InsufficientStockException;
import com.example.backend.exception.ProductInactiveException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.entity.Cart;
import com.example.backend.entity.CartItem;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.repository.CartItemRepository;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final InventoryService inventoryService;
    private final ProductService productService;

    // GET /api/cart
    public CartResponse getCart(String email) {
        Cart cart = getOrCreateCart(email);
        return mapToCartResponse(cart);
    }

    // POST /api/cart/items
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, String email) {
        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        if (!product.getIsActive()) {
            throw new ProductInactiveException(product.getName());
        }

        int availableStock = inventoryService.getAvailableStock(product.getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                    product.getName(), request.getQuantity(), availableStock);
        }

        // update quantity if item already in cart, else add new
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + request.getQuantity();
            if (availableStock < newQty) {
                throw new InsufficientStockException(
                        product.getName(), newQty, availableStock);
            }
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cart.getCartItems().add(cartItem);
        }

        cartItemRepository.save(cartItem);
        return mapToCartResponse(cart);
    }

    // PATCH /api/cart/items/:itemId
    @Transactional
    public CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request, String email) {
        Cart cart = getOrCreateCart(email);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartOwnershipException(itemId);
        }

        int availableStock = inventoryService.getAvailableStock(cartItem.getProduct().getId());
        if (availableStock < request.getQuantity()) {
            throw new InsufficientStockException(
                    cartItem.getProduct().getName(), request.getQuantity(), availableStock);
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return mapToCartResponse(cart);
    }

    // DELETE /api/cart/items/:itemId
    @Transactional
    public CartResponse removeCartItem(Long itemId, String email) {
        Cart cart = getOrCreateCart(email);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartOwnershipException(itemId);
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        return mapToCartResponse(cart);
    }

    // DELETE /api/cart
    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    // used by OrderService when placing an order
    public Cart getOrCreateCart(String email) {
        User user = userService.getUserByEmail(email);
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    // ── helpers ──────────────────────────────────────────────────
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems()
                .stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        Double totalAmount = items.stream()
                .mapToDouble(item -> item.getSubtotal() != null ? item.getSubtotal() : 0.0)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalItems(items.size())
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        Double subtotal = item.getProduct().getPrice() * item.getQuantity();

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .product(productService.mapToProductResponse(item.getProduct()))
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
