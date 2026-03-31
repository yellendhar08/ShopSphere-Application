package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.dto.ApiResponse;
import com.shopsphere.order_service.dto.CartItemRequest;
import com.shopsphere.order_service.dto.CartResponse;
import com.shopsphere.order_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@RequestHeader("X-User-Id") Long userId,
                                                             @Valid @RequestBody CartItemRequest request){
        return ResponseEntity.ok(ApiResponse.success(
                "Item added to cart", cartService.addItem(userId, request))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestHeader("X-User-Id") Long userId){
        return ResponseEntity.ok(ApiResponse.success(
                "Cart fetched successfully", cartService.getCart(userId)
        ));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@RequestHeader("X-User-Id") Long userId,
                                                                @PathVariable Long itemId,
                                                                @RequestBody Map<String, Integer> body){
        return ResponseEntity.ok(ApiResponse.success(
                "Cart item updated", cartService.updateItem(userId, itemId, body.get("quantity"))
        ));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cartService.removeItem(userId, itemId)));
    }

}
