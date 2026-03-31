package com.shopsphere.order_service.repository;


import com.shopsphere.order_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository  extends JpaRepository<CartItem, Long> {

}
