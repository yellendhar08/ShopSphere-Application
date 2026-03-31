package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.entity.*;
import com.shopsphere.order_service.exception.*;
import com.shopsphere.order_service.feign.CatalogClient;
import com.shopsphere.order_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItem_success_newCart() {
        Long userId = 1L;

        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(100.0);
        product.setStock(10);

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(product);

        when(catalogClient.getProductById(1L)).thenReturn(apiResponse);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartResponse response = cartService.addItem(userId, request);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
    }

    @Test
    void addItem_insufficientStock_shouldThrow() {
        Long userId = 1L;

        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(20);

        ProductResponse product = new ProductResponse();
        product.setId(1L);
        product.setStock(5);

        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setData(product);

        when(catalogClient.getProductById(1L)).thenReturn(apiResponse);

        assertThrows(InvalidOrderStatusException.class,
                () -> cartService.addItem(userId, request));
    }

    @Test
    void getCart_success_existing() {
        Long userId = 1L;

        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        CartResponse response = cartService.getCart(userId);

        assertNotNull(response);
    }

    @Test
    void getCart_createIfNotExists() {
        Long userId = 1L;

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartResponse response = cartService.getCart(userId);

        assertNotNull(response);
    }

    @Test
    void updateItem_success() {
        Long userId = 1L;

        CartItem item = CartItem.builder()
                .id(1L)
                .quantity(2)
                .price(100.0)
                .build();

        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartResponse response = cartService.updateItem(userId, 1L, 5);

        assertEquals(5, response.getItems().get(0).getQuantity());
    }

    @Test
    void updateItem_removeWhenZero() {
        Long userId = 1L;

        CartItem item = CartItem.builder()
                .id(1L)
                .quantity(2)
                .price(100.0)
                .build();

        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartResponse response = cartService.updateItem(userId, 1L, 0);

        assertEquals(0, response.getItems().size());
    }

    @Test
    void removeItem_success() {
        Long userId = 1L;

        CartItem item = CartItem.builder()
                .id(1L)
                .price(100.0)
                .build();

        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>(List.of(item)))
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartResponse response = cartService.removeItem(userId, 1L);

        assertEquals(0, response.getItems().size());
    }

    @Test
    void clearCart_success() {
        Long userId = 1L;

        Cart cart = Cart.builder()
                .userId(userId)
                .items(new ArrayList<>(List.of(new CartItem())))
                .build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.clearCart(userId);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }
}