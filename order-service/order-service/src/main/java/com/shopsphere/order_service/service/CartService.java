package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.entity.*;
import com.shopsphere.order_service.exception.*;
import com.shopsphere.order_service.feign.CatalogClient;
import com.shopsphere.order_service.repository.*;
import com.shopsphere.order_service.dto.CartItemResponse;
import com.shopsphere.order_service.dto.CartResponse;
import com.shopsphere.order_service.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CatalogClient catalogClient;

    public CartResponse addItem(Long userId, CartItemRequest request) {

        ProductResponse product = catalogClient.getProductById(request.getProductId()).getData();

        if (product == null) throw new CartNotFoundException("Product not found");


        if (product.getStock() < request.getQuantity())
            throw new InvalidOrderStatusException("Insufficient stock. Available: " + product.getStock());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> {
                            int newQty = existing.getQuantity() + request.getQuantity();
                            if (newQty > product.getStock())
                                throw new InvalidOrderStatusException("Insufficient stock. Available: " + product.getStock());
                            existing.setQuantity(newQty);
                        },
                        () -> cart.getItems().add(CartItem.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .quantity(request.getQuantity())
                                .price(product.getPrice())
                                .productImageUrl(product.getImageUrl())
                                .cart(cart).build())
                );

        return toResponse(cartRepository.save(cart));
    }

    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        return toResponse(cart);
    }

    public CartResponse updateItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));
        if (quantity <= 0)
            cart.getItems().remove(item);
        else
            item.setQuantity(quantity);
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return toResponse(cartRepository.save(cart));
    }

    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream().map(i ->
                CartItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .imageUrl(i.getProductImageUrl())
                        .subtotal(i.getPrice() * i.getQuantity())
                        .build()
        ).collect(Collectors.toList());

        double total = items.stream().mapToDouble(CartItemResponse::getSubtotal).sum();
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .totalAmount(total)
                .build();
    }
}