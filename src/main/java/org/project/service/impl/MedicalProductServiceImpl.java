package org.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.project.entity.MedicalProductEntity;
import org.project.entity.ProductEntity;
import org.project.repository.MedicalProductRepository;
import org.project.repository.ProductRepository;
import org.project.service.MedicalProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for {@link MedicalProductEntity} related operations.
 */
@Slf4j
@Service
@Transactional
public class MedicalProductServiceImpl implements MedicalProductService {

    @Autowired
    private  MedicalProductRepository medicalProductRepository;

    @Autowired
    private  ProductRepository productRepository;

    public MedicalProductServiceImpl(MedicalProductRepository medicalProductRepository,
                                     ProductRepository productRepository) {
        this.medicalProductRepository = medicalProductRepository;
        this.productRepository = productRepository;
    }

    // ==================== CRUD operations ====================

    @Override
    public MedicalProductEntity save(MedicalProductEntity medicalProduct) {
        try {
            log.debug("Saving medical product wrapper for product ID: {}",
                    medicalProduct.getProductEntity() != null ? medicalProduct.getProductEntity().getId() : null);
            return medicalProductRepository.save(medicalProduct);
        } catch (Exception e) {
            log.error("Error saving medical product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save medical product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalProductEntity findById(Long id) {
        try {
            log.debug("Finding medical product by ID: {}", id);
            return medicalProductRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error finding medical product: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalProductEntity> findAll() {
        try {
            log.debug("Finding all medical products");
            return medicalProductRepository.findAll();
        } catch (Exception e) {
            log.error("Error finding all medical products: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            log.debug("Deleting medical product by ID: {}", id);
            medicalProductRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting medical product: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete medical product", e);
        }
    }

    // ==================== Stock management ====================

    @Override
    public void updateStock(Long id, Integer quantity) {
        try {
            log.debug("Updating stock for medical product wrapper ID: {} to quantity: {}", id, quantity);
            MedicalProductEntity wrapper = medicalProductRepository.findById(id).orElse(null);
            if (wrapper != null && wrapper.getProductEntity() != null) {
                ProductEntity product = wrapper.getProductEntity();
                product.setStockQuantities(quantity);
                productRepository.save(product);
            } else {
                log.warn("Cannot update stock: medical product with ID {} not found", id);
            }
        } catch (Exception e) {
            log.error("Error updating medical product stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock", e);
        }
    }
}
