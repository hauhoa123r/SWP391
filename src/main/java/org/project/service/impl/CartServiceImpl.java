package org.project.service.impl;

import java.util.Optional;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.CouponEntity;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.project.repository.*;
import org.project.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartItemService {
	
	@Autowired
    private CartRepository cartRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CouponRepository couponRepo;

    @Override
    public CartItemEntity getCart(CartItemEntityId cartId) {
        return cartRepo.findById(cartId).orElseGet(() -> {
        	CartItemEntity cart = new CartItemEntity();
            return cartRepo.save(cart);
        });
    }

	@Override
	public CartItemEntity addItem(CartItemEntityId cartId, UserEntity userId, ProductEntity productId,
			Integer quantity) {
		CartItemEntity cart = getCart(cartId);
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        cart.addItem(product, quantity);
        return cartRepo.save(cart);
	}

	@Override
	public CartItemEntity removeItem(CartItemEntityId cartId, ProductEntity productId) {
		CartItemEntity cart = getCart(cartId);
        cart.removeItem(productId);
        return cartRepo.save(cart);
	}

    @Override
    public CartItemEntity updateQuantity(CartItemEntityId cartId, ProductEntity productId, Integer quantity) {
    	CartItemEntity cart = getCart(cartId);
        cart.updateItem(productId, quantity);
        return cartRepo.save(cart);
    }

    @Override
    public CartItemEntity applyCoupon(CartItemEntityId cartId, String couponCode) {
    	CartItemEntity cart = getCart(cartId);
        Optional<CouponEntity> optCoupon = couponRepo.findByCode(couponCode);
        if (!optCoupon.isPresent()) {
            throw new RuntimeException("Invalid coupon");
        }
        cart.setCoupon(optCoupon.get());
        return cartRepo.save(cart);
    }

    @Override
    public double calculateTotal(CartItemEntityId cartId) {
    	CartItemEntity cart = getCart(cartId);
        return cart.calculateTotal();
    }
}
