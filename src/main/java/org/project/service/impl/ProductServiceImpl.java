package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.ProductConverter;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.response.ProductResponse;
import org.project.repository.ProductRepository;
import org.project.repository.impl.custom.ProductRepositoryCustom;
import org.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductType SERVICE_TYPE = ProductType.SERVICE;

    private ProductRepository productRepository;
    private ProductRepositoryCustom productRepositoryCustom;
    private ProductConverter productConverter;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Autowired
    public void setProductRepositoryCustom(ProductRepositoryCustom productRepositoryCustom) {
        this.productRepositoryCustom = productRepositoryCustom;
    }

    @Autowired
    public void setProductConverter(ProductConverter productConverter) {
        this.productConverter = productConverter;
    }

    @Override
    public Page<ProductResponse> getAllServicesByPage(int index, int size) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }
        Pageable pageable = PageRequest.of(index, size);
        Page<ProductEntity> productEntityPage = productRepositoryCustom.findAllByProductType(SERVICE_TYPE, pageable);
        if (productEntityPage == null || productEntityPage.isEmpty()) {
            throw new PageNotFoundException(ProductEntity.class, index, size);
        }
        return productEntityPage.map(productConverter::toResponse);
    }

    @Override
    public boolean isServiceExist(Long productId) {
        return productRepository.existsByProductTypeAndId(SERVICE_TYPE, productId);
    }

    @Override
    public ProductResponse getServiceByProductId(Long productId) {
        if (!isServiceExist(productId)) {
            throw new EntityNotFoundException(ProductEntity.class, productId);
        }
        return productConverter.toResponse(productRepository.findByProductTypeAndId(SERVICE_TYPE, productId));
    }
}
