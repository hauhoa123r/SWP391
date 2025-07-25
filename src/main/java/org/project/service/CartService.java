package org.project.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.springframework.stereotype.Service;

@Service
public interface CartService {

	List<CartItemEntity> getCart(Long userId);

	void removeItem(Long userId, Long productId);

	CartItemEntity getItemById(CartItemEntityId id);

	void updateItem(CartItemEntity item);

	BigDecimal calculateTotal(Long userId);

	void addItem(Long userId, Long productId, Integer quantity);
}