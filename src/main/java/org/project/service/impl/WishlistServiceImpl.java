package org.project.service.impl;

import lombok.RequiredArgsConstructor;
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
                .map(WishlistProductEntity::getProductEntity)
                .map(converter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addProduct(Long userId, Long productId) {
        WishlistProductEntityId id = new WishlistProductEntityId(userId, productId);
        if (wishlistRepo.existsById(id)) return;
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
}
