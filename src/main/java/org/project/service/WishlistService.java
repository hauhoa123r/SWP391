package org.project.service;

import org.project.model.response.PharmacyResponse;

import java.util.List;

public interface WishlistService {
    List<PharmacyResponse> getWishlistItems(Long userId);
    
    List<PharmacyResponse> getWishlistItems(Long userId, String searchQuery, String sortBy, String filterStock);

    void addProduct(Long userId, Long productId);
    
    void addProduct(Long userId, Long productId, Integer quantity);

    boolean removeProduct(Long userId, Long productId);
    
    void updateQuantity(Long userId, Long productId, Integer quantity);
}
