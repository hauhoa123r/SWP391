package org.project.repository.impl.custom;

import org.project.model.dto.ProductViewDTO;

import java.util.List;

public interface PharmacyRepositoryCustom {
    /**
     * Custom method to fetch paginated products.
     *
     * @param limit  the maximum number of products to return
     * @param offset the starting point for pagination
     * @return a list of products as Object arrays, where each array contains product details
     */
    List<ProductViewDTO> getPagedProducts(int limit, int offset);
}
