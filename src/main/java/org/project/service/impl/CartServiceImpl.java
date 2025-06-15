package org.project.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.CouponEntity;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.project.repository.*;
import org.project.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepo;
	private final ProductRepository productRepo;
	private final CouponRepository couponRepo;

	public CartServiceImpl(CartRepository cartRepo, ProductRepository productRepo, CouponRepository couponRepo) {
		this.cartRepo = cartRepo;
		this.productRepo = productRepo;
		this.couponRepo = couponRepo;
	}

	@Override
	public List<CartItemEntity> getCart(Long userId) {
		return cartRepo.findByUserEntityId(userId);
	}

	@Override
	public void addItem(Long userId, Long productId, Integer quantity) {
		ProductEntity product = productRepo.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		CartItemEntity item = new CartItemEntity();
		item.setId(new CartItemEntityId(productId + userId, userId));
		UserEntity user = new UserEntity();
		user.setId(userId);
		item.setUserEntity(user);
		item.setProductEntity(product);
		item.setQuantity(quantity);
		cartRepo.save(item);
	}

	@Override
	public void removeItem(Long userId, Long productId) {
		cartRepo.deleteByUserEntityIdAndProductEntityId(userId, productId);
	}

	@Override
	public void updateQuantity(Long userId, Long productId, Integer quantity) {
		List<CartItemEntity> items = cartRepo.findByUserEntityId(userId);
		for (CartItemEntity item : items) {
			if (item.getProductEntity().getId().equals(productId)) {
				item.setQuantity(quantity);
				cartRepo.save(item);
				return;
			}
		}
		throw new RuntimeException("Item not found in cart");
	}

	@Override
	public void applyCoupon(Long userId, String couponCode) {
		// Store coupon in session or apply logic in checkout stage
		couponRepo.findByCode(couponCode).orElseThrow(() -> new RuntimeException("Invalid coupon code"));
	}

	@Override
	public BigDecimal calculateTotal(Long userId) {
		List<CartItemEntity> items = cartRepo.findByUserEntityId(userId);
		return items.stream().map(item -> {
			BigDecimal price = item.getProductEntity().getPrice();
			if (price == null)
				price = BigDecimal.ZERO;
			return price.multiply(BigDecimal.valueOf(item.getQuantity()));
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
