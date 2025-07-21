package org.project.service.impl;

import org.project.entity.InventoryManagerEntity;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.entity.SupplierTransactionItemEntity;
import org.project.entity.SupplierTransactionItemEntityId;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.InventoryManagerRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.SupplierInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierInServiceImpl implements SupplierInService {

    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;

    @Autowired
    private SupplierEntityRepository supplierEntityRepository;

    @Autowired
    private InventoryManagerRepository inventoryManagerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierTransactionItemRepository supplierTransactionItemRepository;

    @Override
    public List<SupplierInDTO> getAllSupplierIns() {
        List<SupplierTransactionsEntity> transactions = supplierTransactionRepository.findByTransactionType(SupplierTransactionType.STOCK_IN);
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierInDTO> getAllSupplierIns(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size);

        // Thực hiện tìm kiếm dựa trên keyword và status
        Page<SupplierTransactionsEntity> transactionsPage;

        if (keyword != null && !keyword.isEmpty() && status != null && !status.isEmpty()) {
            // Tìm kiếm theo cả keyword và status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatusAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_IN, statusEnum, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Chỉ tìm kiếm theo keyword
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndInvoiceNumberContaining(
                    SupplierTransactionType.STOCK_IN, keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            // Chỉ tìm kiếm theo status
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            transactionsPage = supplierTransactionRepository.findByTransactionTypeAndStatus(
                    SupplierTransactionType.STOCK_IN, statusEnum, pageable);
        } else {
            // Không có điều kiện tìm kiếm
            transactionsPage = supplierTransactionRepository.findByTransactionType(
                    SupplierTransactionType.STOCK_IN, pageable);
        }

        List<SupplierInDTO> dtoList = transactionsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, transactionsPage.getTotalElements());
    }

    @Override
    public SupplierInDTO getSupplierInById(Long id) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        return transactionOpt.filter(t -> t.getTransactionType() == SupplierTransactionType.STOCK_IN)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public SupplierInDTO createSupplierIn(SupplierInDTO supplierInDTO) {
        SupplierTransactionsEntity transaction = new SupplierTransactionsEntity();

        // Set basic information
        transaction.setTransactionType(SupplierTransactionType.STOCK_IN);
        transaction.setStatus(supplierInDTO.getStatus() != null ? supplierInDTO.getStatus() : SupplierTransactionStatus.PENDING);
        transaction.setTransactionDate(supplierInDTO.getTransactionDate() != null ? supplierInDTO.getTransactionDate() : Timestamp.from(Instant.now()));
        transaction.setTotalAmount(supplierInDTO.getTotalAmount());
        transaction.setNotes(supplierInDTO.getNotes());
        transaction.setExpectedDeliveryDate(supplierInDTO.getExpectedDeliveryDate());
        transaction.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        transaction.setTaxAmount(supplierInDTO.getTaxAmount());
        transaction.setShippingCost(supplierInDTO.getShippingCost());
        transaction.setPaymentMethod(supplierInDTO.getPaymentMethod());
        transaction.setDueDate(supplierInDTO.getDueDate());
        transaction.setPaymentDate(supplierInDTO.getPaymentDate());

        // Set supplier
        SupplierEntity supplier = supplierEntityRepository.findById(supplierInDTO.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierInDTO.getSupplierId()));
        transaction.setSupplierEntity(supplier);

        // Set inventory manager
        InventoryManagerEntity manager = inventoryManagerRepository.findById(supplierInDTO.getInventoryManagerId())
                .orElseThrow(() -> new RuntimeException("Inventory Manager not found with id: " + supplierInDTO.getInventoryManagerId()));
        transaction.setInventoryManagerEntity(manager);

        // Save transaction first to get ID
        SupplierTransactionsEntity savedTransaction = supplierTransactionRepository.save(transaction);

        // Process items if available
        if (supplierInDTO.getItems() != null && !supplierInDTO.getItems().isEmpty()) {
            List<SupplierTransactionItemEntity> itemEntities = new ArrayList<>();

            for (SupplierRequestItemDTO itemDTO : supplierInDTO.getItems()) {
                SupplierTransactionItemEntity itemEntity = new SupplierTransactionItemEntity();

                // Create composite key
                SupplierTransactionItemEntityId id = new SupplierTransactionItemEntityId();
                id.setSupplierTransactionId(savedTransaction.getId());
                id.setProductId(itemDTO.getProductId());
                itemEntity.setId(id);

                // Set transaction reference
                itemEntity.setSupplierTransactionEntity(savedTransaction);

                // Set product reference
                ProductEntity product = productRepository.findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDTO.getProductId()));
                itemEntity.setProductEntity(product);

                // Set other fields
                itemEntity.setQuantity(itemDTO.getQuantity());
                itemEntity.setUnitPrice(itemDTO.getUnitPrice());
                itemEntity.setManufactureDate(itemDTO.getManufactureDate());
                itemEntity.setExpirationDate(itemDTO.getExpirationDate());
                itemEntity.setBatchNumber(itemDTO.getBatchNumber());
                itemEntity.setStorageLocation(itemDTO.getStorageLocation());
                itemEntity.setNotes(itemDTO.getNotes());

                itemEntities.add(itemEntity);
            }

            // Save all items
            supplierTransactionItemRepository.saveAll(itemEntities);
        }

        // Return the DTO with updated information
        return getSupplierInById(savedTransaction.getId());
    }

    @Override
    @Transactional
    public SupplierInDTO updateSupplierIn(Long id, SupplierInDTO supplierInDTO) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        if (transactionOpt.isEmpty() || transactionOpt.get().getTransactionType() != SupplierTransactionType.STOCK_IN) {
            return null;
        }

        SupplierTransactionsEntity transaction = transactionOpt.get();

        // Update basic information
        transaction.setStatus(supplierInDTO.getStatus());
        transaction.setTotalAmount(supplierInDTO.getTotalAmount());
        transaction.setNotes(supplierInDTO.getNotes());
        transaction.setExpectedDeliveryDate(supplierInDTO.getExpectedDeliveryDate());
        transaction.setInvoiceNumber(supplierInDTO.getInvoiceNumber());
        transaction.setTaxAmount(supplierInDTO.getTaxAmount());
        transaction.setShippingCost(supplierInDTO.getShippingCost());
        transaction.setPaymentMethod(supplierInDTO.getPaymentMethod());
        transaction.setDueDate(supplierInDTO.getDueDate());
        transaction.setPaymentDate(supplierInDTO.getPaymentDate());

        // Update supplier if provided
        if (supplierInDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierEntityRepository.findById(supplierInDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierInDTO.getSupplierId()));
            transaction.setSupplierEntity(supplier);
        }

        // Save updated transaction
        supplierTransactionRepository.save(transaction);

        // Return the updated DTO
        return getSupplierInById(id);
    }

    @Override
    @Transactional
    public void updateSupplierInStatus(Long id, String status) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        if (transactionOpt.isPresent() && transactionOpt.get().getTransactionType() == SupplierTransactionType.STOCK_IN) {
            SupplierTransactionsEntity transaction = transactionOpt.get();
            transaction.setStatus(SupplierTransactionStatus.valueOf(status));

            // If status is APPROVED, set approvedDate
            if (SupplierTransactionStatus.APPROVED.name().equals(status)) {
                transaction.setApprovedDate(Timestamp.from(Instant.now()));
            }

            supplierTransactionRepository.save(transaction);
        }
    }

    @Override
    @Transactional
    public void deleteSupplierIn(Long id) {
        Optional<SupplierTransactionsEntity> transactionOpt = supplierTransactionRepository.findById(id);
        if (transactionOpt.isPresent() && transactionOpt.get().getTransactionType() == SupplierTransactionType.STOCK_IN) {
            supplierTransactionRepository.deleteById(id);
        }
    }

    private SupplierInDTO convertToDTO(SupplierTransactionsEntity entity) {
        SupplierInDTO dto = new SupplierInDTO();

        dto.setId(entity.getId());
        dto.setSupplierId(entity.getSupplierEntity().getId());
        dto.setSupplierName(entity.getSupplierEntity().getName());
        dto.setSupplierContact(entity.getSupplierEntity().getPhoneNumber());
        dto.setInventoryManagerId(entity.getInventoryManagerEntity().getId());

        // Get staff name from inventory manager's staff entity
        if (entity.getInventoryManagerEntity().getStaffEntity() != null) {
            dto.setInventoryManagerName(entity.getInventoryManagerEntity().getStaffEntity().getFullName());
        }

        dto.setTotalAmount(entity.getTotalAmount());
        dto.setTransactionDate(entity.getTransactionDate());
        dto.setStatus(entity.getStatus());
        dto.setApprovedDate(entity.getApprovedDate());
        dto.setNotes(entity.getNotes());
        dto.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());

//        if (entity.getApprovedBy() != null) {
//            dto.setApprovedById(entity.getApprovedBy().getId());
//            if (entity.getApprovedBy().getStaffEntity() != null) {
//                dto.setApprovedByName(entity.getApprovedBy().getStaffEntity().getFullName());
//            }
//        }

        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setShippingCost(entity.getShippingCost());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setDueDate(entity.getDueDate());
        dto.setPaymentDate(entity.getPaymentDate());

        // Convert items
        if (entity.getSupplierTransactionItemEntities() != null) {
            List<SupplierRequestItemDTO> itemDTOs = entity.getSupplierTransactionItemEntities().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    private SupplierRequestItemDTO convertItemToDTO(SupplierTransactionItemEntity entity) {
        SupplierRequestItemDTO dto = new SupplierRequestItemDTO();

        dto.setProductId(entity.getProductEntity().getId());
        dto.setProductName(entity.getProductEntity().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setManufactureDate(entity.getManufactureDate());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setBatchNumber(entity.getBatchNumber());
        dto.setStorageLocation(entity.getStorageLocation());
        dto.setNotes(entity.getNotes());

        return dto;
    }
}