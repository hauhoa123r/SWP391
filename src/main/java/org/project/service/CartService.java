package org.project.service;

import java.math.BigDecimal;
import java.util.List;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
	
	

	List<CartItemEntity> getCart(Long userId);

	void addItem(Long userId, Long productId, Integer quantity);

	void removeItem(Long userId, Long productId);

	void updateQuantity(Long userId, Long productId, Integer quantity);

	void applyCoupon(Long userId, String couponCode);

	BigDecimal calculateTotal(Long userId);

}