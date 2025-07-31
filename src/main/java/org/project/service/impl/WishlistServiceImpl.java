package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.project.entity.WishlistProductEntity;
import org.project.entity.WishlistProductEntityId;
import org.project.model.response.PharmacyResponse;
import org.project.repository.ProductRepository;
import org.project.repository.UserRepository;
import org.project.repository.WishlistProductRepository;
import org.project.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistProductRepository wishlistRepo;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> getWishlistItems(Long userId) {
        return wishlistRepo.findByIdUserId(userId).stream()
                .map(wishlist -> {
                    PharmacyResponse response = new PharmacyResponse();
                    response.setId(wishlist.getProductEntity().getId());
                    response.setName(wishlist.getProductEntity().getName());
                    response.setPrice(wishlist.getProductEntity().getPrice());
                    response.setStockQuantity(wishlist.getProductEntity().getStockQuantities());
                    response.setImageUrl(wishlist.getProductEntity().getImageUrl());
                    response.setDescription(wishlist.getProductEntity().getDescription());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> getWishlistItems(Long userId, String searchQuery, String sortBy, String filterStock) {
        List<PharmacyResponse> allItems = getWishlistItems(userId);
        
        // Lọc theo tìm kiếm nếu có
        List<PharmacyResponse> filteredItems = allItems;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String searchLower = searchQuery.toLowerCase();
            filteredItems = allItems.stream()
                    .filter(item -> item.getName().toLowerCase().contains(searchLower) || 
                                    (item.getDescription() != null && 
                                     item.getDescription().toLowerCase().contains(searchLower)))
                    .collect(Collectors.toList());
        }
        
        // Lọc theo tồn kho
        if (filterStock != null && filterStock.equals("in_stock")) {
            filteredItems = filteredItems.stream()
                    .filter(item -> item.getStockQuantity() > 0)
                    .collect(Collectors.toList());
        }
        
        // Sắp xếp dữ liệu
        if (sortBy != null) {
            switch (sortBy) {
                case "name_asc":
                    filteredItems.sort(Comparator.comparing(PharmacyResponse::getName));
                    break;
                case "name_desc":
                    filteredItems.sort(Comparator.comparing(PharmacyResponse::getName).reversed());
                    break;
                case "price_asc":
                    filteredItems.sort(Comparator.comparing(PharmacyResponse::getPrice));
                    break;
                case "price_desc":
                    filteredItems.sort(Comparator.comparing(PharmacyResponse::getPrice).reversed());
                    break;
                default:
                    // Mặc định sắp xếp theo tên tăng dần
                    filteredItems.sort(Comparator.comparing(PharmacyResponse::getName));
                    break;
            }
        }
        
        return filteredItems;
    }

    @Override
    @Transactional
    public void addProduct(Long userId, Long productId) {
        addProduct(userId, productId, 1);
    }
    
    @Override
    @Transactional
    public void addProduct(Long userId, Long productId, Integer quantity) {
        // Check if already exists in wishlist
        WishlistProductEntityId id = new WishlistProductEntityId(userId, productId);
        if (wishlistRepo.existsById(id)) {
            log.debug("Product {} already in wishlist for user {}", productId, userId);
            return;
        }
        
        // Fetch entities and validate they exist
        Optional<ProductEntity> productOpt = productRepository.findById(productId);
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        
        if (productOpt.isEmpty()) {
            log.error("Cannot add product to wishlist: Product with ID {} not found", productId);
            return;
        }
        
        if (userOpt.isEmpty()) {
            log.error("Cannot add product to wishlist: User with ID {} not found", userId);
            return;
        }
        
        // Create and save the wishlist entry
        WishlistProductEntity entity = new WishlistProductEntity();
        entity.setId(id);
        entity.setProductEntity(productOpt.get());
        entity.setUserEntity(userOpt.get());
        
        wishlistRepo.save(entity);
        log.info("Added product {} to wishlist for user {}", productId, userId);
    }

    @Override
    @Transactional
    public boolean removeProduct(Long userId, Long productId) {
        log.info("Attempting to remove product {} from wishlist for user {}", productId, userId);
        WishlistProductEntityId id = new WishlistProductEntityId(userId, productId);
        if (wishlistRepo.existsById(id)) {
            log.info("Product {} found in wishlist for user {}, proceeding with deletion", productId, userId);
            wishlistRepo.deleteByIdUserIdAndIdProductId(userId, productId);
            boolean isRemoved = !wishlistRepo.existsById(id);
            if (isRemoved) {
                log.info("Successfully removed product {} from wishlist for user {}", productId, userId);
            } else {
                log.error("Failed to remove product {} from wishlist for user {}, still exists after deletion attempt", productId, userId);
            }
            return isRemoved;
        } else {
            log.warn("Product {} not found in wishlist for user {}", productId, userId);
        }
        return false;
    }
    
    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        // Không cần lưu quantity vì chúng ta đang sử dụng stockQuantity từ product
        // Phương thức này có thể trống hoặc được sử dụng cho việc khác sau này
    }
}
