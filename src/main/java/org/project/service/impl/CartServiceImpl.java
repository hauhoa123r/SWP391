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
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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
	@Autowired
	private final UserRepository userRepo;

	
	public CartServiceImpl(CartRepository cartRepo, ProductRepository productRepo, CouponRepository couponRepo, UserRepository userRepo) {
		this.cartRepo = cartRepo;
		this.productRepo = productRepo;
		this.couponRepo = couponRepo;
		this.userRepo = userRepo;
	}

	@Override
	public List<CartItemEntity> getCart(Long userId) {
		return cartRepo.findByUserEntityId(userId);
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

    //calculate total amount of money in cart (no coupon applied)
	@Override
	public BigDecimal calculateTotal(Long userId) {
		//get items from user
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
	// add item to cart
	@Override
	public void addItem(Long userId, Long productId, Integer quantity) {
	        // Load the user and product entities
	        UserEntity user = userRepo.findById(userId)
	                .orElseThrow(() -> new EntityNotFoundException("User not found"));
	        ProductEntity product = productRepo.findById(productId)
	                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

	        // Create composite key
	        CartItemEntityId cartItemId = new CartItemEntityId();
	        cartItemId.setUserId(userId);
	        cartItemId.setCartItemId(productId); // Link cartItemId to productId

	        // Try to find existing cart item
	        Optional<CartItemEntity> optionalCartItem = cartRepo.findById(cartItemId);

	        if (optionalCartItem.isPresent()) {
	            // get quantity
	            CartItemEntity existingItem = optionalCartItem.get();
	            existingItem.setQuantity(existingItem.getQuantity() + quantity);
	        } else {
	            // Create new cart item
	            CartItemEntity newItem = new CartItemEntity();
	            newItem.setId(cartItemId);
	            newItem.setUserEntity(user);
	            newItem.setProductEntity(product);
	            newItem.setQuantity(quantity);
	            cartRepo.save(newItem);
	        }
	    
	}
	
}
