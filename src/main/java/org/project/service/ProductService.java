package org.project.service;

import org.project.model.response.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponse> getAllServicesByPage(int index, int size);

    boolean isServiceExist(Long productId);

    ProductResponse getServiceByProductId(Long productId);
}
