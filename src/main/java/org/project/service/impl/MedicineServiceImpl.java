package org.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.project.entity.CategoryEntity;
import org.project.entity.MedicineEntity;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.model.dto.MedicineDTO;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.MedicineRepository;
import org.project.repository.ProductRepository;
import org.project.service.BatchesService;
import org.project.service.MedicineService;
import org.project.enums.operation.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for {@link MedicineEntity}.
 */
@Slf4j
@Service
@Transactional
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    
    @Autowired
    private ProductRepository productRepository;

    //inject BatchService
    @Autowired
    private BatchesService  batchesService;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }
    
    @Override
    public MedicineEntity save(MedicineEntity medicine) {
        try {
            log.debug("Saving medicine for product ID: {}",
                    medicine.getProductEntity() != null ? medicine.getProductEntity().getId() : null);
            return medicineRepository.save(medicine);
        } catch (Exception e) {
            log.error("Error saving medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save medicine", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicineEntity findById(Long id) {
        try {
            log.debug("Finding medicine by ID: {}", id);
            return medicineRepository.findById(id).orElse(null);
        } catch (Exception e) {
            log.error("Error finding medicine: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicineEntity> findAll() {
        try {
            log.debug("Finding all medicines");
            return medicineRepository.findAll();
        } catch (Exception e) {
            log.error("Error finding all medicines: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            log.debug("Deleting medicine by ID: {}", id);
            medicineRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete medicine", e);
        }
    }
    
    @Override
    public Page<MedicineDTO> getAllMedicines(int page, int size, String name, SortDirection sortDirection, String sortField) {
        try {
            log.debug("Getting all medicines with pagination - page: {}, size: {}", page, size);
            
            // Create pageable
            PageRequest pageRequest;
            if (sortField != null && !sortField.isEmpty()) {
                Sort.Direction direction = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
                pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));
            } else {
                pageRequest = PageRequest.of(page, size);
            }
            
            // Get medicines
            List<MedicineEntity> medicines;
            if (name != null && !name.isEmpty()) {
                medicines = medicineRepository.findByProductEntityNameContaining(name);
            } else {
                medicines = medicineRepository.findAll();
            }
            
            // Apply pagination manually
            int start = (int) pageRequest.getOffset();
            int end = Math.min((start + pageRequest.getPageSize()), medicines.size());
            
            List<MedicineEntity> pageContent = start < end ? medicines.subList(start, end) : new ArrayList<>();
            
            // Convert to DTOs
            List<MedicineDTO> dtoList = pageContent.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return new PageImpl<>(dtoList, pageRequest, medicines.size());
        } catch (Exception e) {
            log.error("Error getting medicines with pagination: {}", e.getMessage(), e);
            return Page.empty();
        }
    }
    
    @Override
    public MedicineDTO createMedicine(MedicineDTO medicineDTO) {
        try {
            log.debug("Creating new medicine: {}", medicineDTO.getName());
            
            // Create product entity first
            ProductEntity product = new ProductEntity();
            product.setName(medicineDTO.getName());
            product.setDescription(medicineDTO.getDescription());
            product.setPrice(medicineDTO.getPrice());
            product.setUnit(medicineDTO.getUnit());
            product.setStockQuantities(medicineDTO.getStockQuantities() != null ? medicineDTO.getStockQuantities() : 0);
            product.setImageUrl(medicineDTO.getImageUrl());
            product.setProductType(ProductType.MEDICINE);
            product.setProductStatus(medicineDTO.getProductStatus());
            product.setLabel(medicineDTO.getLabel());
            
            ProductEntity savedProduct = productRepository.save(product);
            
            // Create medicine entity
            MedicineEntity medicine = new MedicineEntity();
            medicine.setProductEntity(savedProduct);
            
            MedicineEntity savedMedicine = medicineRepository.save(medicine);
            return convertToDTO(savedMedicine);
        } catch (Exception e) {
            log.error("Error creating medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create medicine", e);
        }
    }
    
    @Override
    public MedicineDTO updateMedicine(MedicineDTO medicineDTO) {
        try {
            log.debug("Updating medicine with ID: {}", medicineDTO.getId());
            
            MedicineEntity existingMedicine = medicineRepository.findById(medicineDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + medicineDTO.getId()));
            
            ProductEntity product = existingMedicine.getProductEntity();
            if (product != null) {
                product.setName(medicineDTO.getName());
                product.setDescription(medicineDTO.getDescription());
                product.setPrice(medicineDTO.getPrice());
                product.setUnit(medicineDTO.getUnit());
                product.setStockQuantities(medicineDTO.getStockQuantities());
                product.setImageUrl(medicineDTO.getImageUrl());
                product.setProductStatus(medicineDTO.getProductStatus());
                product.setLabel(medicineDTO.getLabel());
                productRepository.save(product);
            }
            
            MedicineEntity updatedMedicine = medicineRepository.save(existingMedicine);
            return convertToDTO(updatedMedicine);
        } catch (Exception e) {
            log.error("Error updating medicine: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update medicine", e);
        }
    }
    
    @Override
    public void processSupplierIn(SupplierInDTO supplierInDTO) {
        try {
            log.debug("Processing supplier in for medicines, ID: {}", supplierInDTO.getId());
            
            if (supplierInDTO.getItems() != null) {
                for (SupplierRequestItemDTO item : supplierInDTO.getItems()) {
                    ProductEntity product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + item.getProductId()));
                    
                    // Update stock quantity
                    int currentStock = product.getStockQuantities() != null ? product.getStockQuantities() : 0;
                    product.setStockQuantities(currentStock + item.getQuantity());
                    
                    productRepository.save(product);
                }
            }
        } catch (Exception e) {
            log.error("Error processing supplier in for medicines: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process supplier in for medicines", e);
        }
    }
    
    private MedicineDTO convertToDTO(MedicineEntity entity) {
        MedicineDTO dto = new MedicineDTO();
        
        if (entity.getProductEntity() != null) {
            ProductEntity product = entity.getProductEntity();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setUnit(product.getUnit());
            dto.setStockQuantities(product.getStockQuantities());
            dto.setImageUrl(product.getImageUrl());
            dto.setProductType(product.getProductType());
            dto.setProductStatus(product.getProductStatus());
            dto.setLabel(product.getLabel());
            dto.setAverageRating(product.getAverageRating());
            dto.setReviewCount(product.getReviewCount());
            //string of categories
            String categories = entity.getProductEntity().getCategoryEntities().stream()
                    .map(CategoryEntity::getName).collect(Collectors.joining(", "));
            dto.setCategory(categories);
            //set batches
            dto.setBatches(batchesService.findDTOByMedicine(entity));
        }
        
        return dto;
    }
}
