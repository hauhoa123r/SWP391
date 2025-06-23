package org.project.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {
	@Autowired
	private final CartRepository cartRepo;
	@Autowired
	private final ProductRepository productRepo;
	@Autowired
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
	
	public CartItemEntity getItemById(CartItemEntityId id) {
		CartItemEntity item = cartRepo.findById(id).orElse(null);
	    return item;
	}
    @Override
    public void updateItem(CartItemEntity item) {
        cartRepo.save(item); // save() updates if ID is present
    }


	@Override
	public BigDecimal calculateTotal(Long userId) {
		List<CartItemEntity> items = cartRepo.findByUserEntityId(userId);
		return items.stream().map(item -> {
			BigDecimal price = item.getProductEntity().getPrice();
			Integer quantity = item.getQuantity();
			if (price == null) {
				price = BigDecimal.ZERO;
			}
			return price.multiply(BigDecimal.valueOf(quantity));
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	@Override
	    public void syncCart(Long userId, List<CartItemEntity> sessionCart) {
		//load list from db
	        List<CartItemEntity> dbCartItems = cartRepo.findByUserEntityId(userId);
	        // using session to store item ids
	        List<CartItemEntityId> sessionItemIds = sessionCart.stream()
	                .map(CartItemEntity::getId)
	                .collect(Collectors.toList());
	        // find item exist in db
	        List<CartItemEntity> toDelete = dbCartItems.stream()
	                .filter(item -> !sessionItemIds.contains(item.getId()))
	                .collect(Collectors.toList());
	        // delete from db
	        cartRepo.deleteAll(toDelete); // can be soft delete if needed
	    }
	
}
