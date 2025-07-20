package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.WishlistProductEntity;
import org.project.entity.WishlistProductEntityId;
import org.project.model.response.PharmacyResponse;
import org.project.repository.ProductRepository;
import org.project.repository.UserRepository;
import org.project.repository.WishlistProductRepository;
import org.project.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistProductRepository wishlistRepo;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ConverterPharmacyProduct converter;

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyResponse> getWishlistItems(Long userId) {
        List<WishlistProductEntity> entities = wishlistRepo.findByIdUserId(userId);
        return entities.stream()
                .map(entity -> {
                    // Lấy stockQuantities trực tiếp từ ProductEntity
                    Integer stockQuantity = entity.getProductEntity().getStockQuantities();
                    log.info("Product ID: {}, Name: {}, Stock Quantity: {}", 
                             entity.getProductEntity().getId(), 
                             entity.getProductEntity().getName(),
                             stockQuantity);
                    
                    PharmacyResponse response = converter.toDto(entity.getProductEntity());
                    if (response.getStockQuantity() == null) {
                        log.warn("Stock quantity in PharmacyResponse is null, using direct value");
                        response.setStockQuantity(stockQuantity);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addProduct(Long userId, Long productId) {
        addProduct(userId, productId, 1);
    }
    
    @Override
    @Transactional
    public void addProduct(Long userId, Long productId, Integer quantity) {
        WishlistProductEntityId id = new WishlistProductEntityId(userId, productId);
        if (wishlistRepo.existsById(id)) {
            return;
        }
        WishlistProductEntity entity = new WishlistProductEntity();
        entity.setId(id);
        productRepository.findById(productId).ifPresent(entity::setProductEntity);
        userRepository.findById(userId).ifPresent(entity::setUserEntity);
        wishlistRepo.save(entity);
    }

    @Override
    @Transactional
    public void removeProduct(Long userId, Long productId) {
        wishlistRepo.deleteByIdUserIdAndIdProductId(userId, productId);
    }
    
    @Override
    @Transactional
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        // Không cần lưu quantity vì chúng ta đang sử dụng stockQuantity từ product
        // Phương thức này có thể trống hoặc được sử dụng cho việc khác sau này
    }
}
