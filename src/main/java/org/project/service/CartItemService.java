package org.project.service;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;

public interface CartItemService {
	CartItemEntity addItem(CartItemEntityId cartId, UserEntity userId, ProductEntity productId, Integer quantity);

	CartItemEntity removeItem(CartItemEntityId cartId, ProductEntity productId);

	CartItemEntity updateQuantity(CartItemEntityId cartId, ProductEntity productId, Integer quantity);

	CartItemEntity applyCoupon(CartItemEntityId cartId, String couponCode);

	double calculateTotal(CartItemEntityId cartId);

	CartItemEntity getCart(CartItemEntityId cartId);
}