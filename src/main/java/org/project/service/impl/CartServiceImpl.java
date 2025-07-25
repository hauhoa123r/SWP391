//package org.project.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.project.entity.CartItemEntity;
//import org.project.entity.CartItemEntityId;
//import org.project.entity.ProductEntity;
//import org.project.entity.UserEntity;
//import org.project.model.response.CartItemResponse;
//import org.project.repository.CartItemRepository;
//import org.project.repository.ProductRepository;
//import org.project.repository.UserRepository;
//import org.project.service.CartService;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service("cartService")
//@RequiredArgsConstructor
//public class CartServiceImpl implements CartService {
//
//    private final CartItemRepository cartItemRepository;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//
//    @Override
//    @Transactional
//    public void addToCart(Long userId, Long productId, Integer quantity) {
//        log.info("Adding product {} to cart for user {} with quantity {}", productId, userId, quantity);
//
//        // Find existing cart item
//        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserEntity_IdAndProductEntity_Id(userId, productId);
//
//        if (existingItem.isPresent()) {
//            // Update quantity if item already exists
//            CartItemEntity item = existingItem.get();
//            item.setQuantity(item.getQuantity() + quantity);
//            cartItemRepository.save(item);
//            log.info("Updated existing cart item, new quantity: {}", item.getQuantity());
//        } else {
//            // Create new cart item
//            CartItemEntity newItem = new CartItemEntity();
//
//            // Get next cart item ID
//            Long nextCartItemId = getNextCartItemId(userId);
//
//            // Create and set the composite ID
//            CartItemEntityId cartItemId = new CartItemEntityId(nextCartItemId, userId);
//            newItem.setId(cartItemId);
//
//            // Set product
//            ProductEntity product = productRepository.findById(productId).orElse(null);
//            if (product == null) {
//                log.error("Product with ID {} not found", productId);
//                return;
//            }
//            newItem.setProductEntity(product);
//
//            // Set user
//            UserEntity user = userRepository.findById(userId).orElse(null);
//            if (user == null) {
//                log.error("User with ID {} not found", userId);
//                return;
//            }
//            newItem.setUserEntity(user);
//
//            // Set quantity
//            newItem.setQuantity(quantity);
//
//            cartItemRepository.save(newItem);
//            log.info("Added new item to cart with ID {}", nextCartItemId);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void removeFromCart(Long userId, Long productId) {
//        log.info("Removing product {} from cart for user {}", productId, userId);
//        cartItemRepository.deleteByUserEntity_IdAndProductEntity_Id(userId, productId);
//    }
//
//    @Override
//    @Transactional
//    public void updateQuantity(Long userId, Long productId, Integer quantity) {
//        log.info("Updating quantity for product {} in cart for user {} to {}", productId, userId, quantity);
//
//        if (quantity <= 0) {
//            // Remove item if quantity is 0 or negative
//            removeFromCart(userId, productId);
//            return;
//        }
//
//        // Find and update cart item
//        Optional<CartItemEntity> existingItem = cartItemRepository.findByUserEntity_IdAndProductEntity_Id(userId, productId);
//
//        if (existingItem.isPresent()) {
//            CartItemEntity item = existingItem.get();
//            item.setQuantity(quantity);
//            cartItemRepository.save(item);
//            log.info("Updated cart item quantity to: {}", quantity);
//        } else {
//            log.warn("Cart item not found for user {} and product {}", userId, productId);
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<CartItemResponse> getCartItems(Long userId) {
//        log.info("Getting cart items for user {}", userId);
//
//        List<CartItemEntity> cartItems = cartItemRepository.findByUserEntity_Id(userId);
//
//        return cartItems.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public int getCartItemCount(Long userId) {
//        log.info("Getting cart item count for user {}", userId);
//        return cartItemRepository.countByUserEntity_Id(userId);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public double getCartTotal(Long userId) {
//        log.info("Calculating cart total for user {}", userId);
//
//        List<CartItemEntity> cartItems = cartItemRepository.findByUserEntity_Id(userId);
//
//        return cartItems.stream()
//                .mapToDouble(item -> item.getProductEntity().getPrice().doubleValue() * item.getQuantity())
//                .sum();
//    }
//
//    @Override
//    @Transactional
//    public void clearCart(Long userId) {
//        log.info("Clearing cart for user {}", userId);
//        cartItemRepository.deleteByUserEntity_Id(userId);
//    }
//
//    private CartItemResponse convertToResponse(CartItemEntity entity) {
//        ProductEntity product = entity.getProductEntity();
//        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
//
//        return CartItemResponse.builder()
//                .id(entity.getId().getCartItemId())
//                .productId(product.getId())
//                .productName(product.getName())
//                .productImage(product.getImageUrl())
//                .price(product.getPrice())
//                .quantity(entity.getQuantity())
//                .subtotal(subtotal)
//                .build();
//    }
//
//    private Long getNextCartItemId(Long userId) {
//        // Find the maximum cart item ID for this user and increment by 1
//        return cartItemRepository.findMaxCartItemIdByUserId(userId)
//                .map(id -> id + 1L)
//                .orElse(1L); // Start with 1 if no existing items
//    }
//}