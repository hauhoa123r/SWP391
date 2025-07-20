package org.project.service;

import org.project.model.response.PharmacyResponse;

import java.util.List;

public interface WishlistService {
    List<PharmacyResponse> getWishlistItems(Long userId);

    void addProduct(Long userId, Long productId);

    void removeProduct(Long userId, Long productId);
}
