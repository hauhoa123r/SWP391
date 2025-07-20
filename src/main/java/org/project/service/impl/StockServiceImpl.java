package org.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.project.entity.*;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.model.request.StockInvoiceDTO;
import org.project.model.request.StockRequestDTO;
import org.project.model.request.StockRequestItemDTO;
import org.project.model.response.*;
import org.project.repository.*;
import org.project.service.StockService;
import org.project.utils.TimestampUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRequestRepository stockRequestRepository;
    private final StockInvoiceRepository stockInvoiceRepository;
    private final ProductRepository productRepository;
    private final DepartmentRepository departmentRepository;
    private final InventoryManagerRepository inventoryManagerRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public StockRequestResponse createStockRequest(StockRequestDTO stockRequestDTO, Long inventoryManagerId) {
        if (stockRequestDTO == null) {
            throw new IllegalArgumentException("Stock request data cannot be null");
        }
        
        InventoryManagerEntity inventoryManager = inventoryManagerRepository.findById(inventoryManagerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory Manager not found with ID: " + inventoryManagerId));

        StockRequestEntity stockRequest = new StockRequestEntity();
        stockRequest.setTransactionType(stockRequestDTO.getTransactionType());
        stockRequest.setRequestDate(TimestampUtils.getCurrentTimestamp());
        stockRequest.setRequestedBy(inventoryManager);
        stockRequest.setStatus(StockStatus.PENDING);
        stockRequest.setNotes(stockRequestDTO.getNotes());
        stockRequest.setExpectedDeliveryDate(stockRequestDTO.getExpectedDeliveryDate());

        // Set supplier if it's a stock-in request
        if (stockRequestDTO.getTransactionType() == StockTransactionType.STOCK_IN && stockRequestDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(stockRequestDTO.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + stockRequestDTO.getSupplierId()));
            stockRequest.setSupplier(supplier);
        }

        // Set department if it's a stock-out request
        if (stockRequestDTO.getTransactionType() == StockTransactionType.STOCK_OUT && stockRequestDTO.getDepartmentId() != null) {
            DepartmentEntity department = departmentRepository.findById(stockRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + stockRequestDTO.getDepartmentId()));
            stockRequest.setDepartment(department);
        }

        // Save the stock request first to get an ID
        stockRequest = stockRequestRepository.save(stockRequest);

        // Create and add stock request items
        Set<StockRequestItemEntity> items = new HashSet<>();
        if (stockRequestDTO.getItems() != null && !stockRequestDTO.getItems().isEmpty()) {
        for (StockRequestItemDTO itemDTO : stockRequestDTO.getItems()) {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDTO.getProductId()));
                
                // Validate quantity
                if (itemDTO.getQuantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be greater than 0 for product ID: " + itemDTO.getProductId());
                }
                
                // For STOCK_OUT transactions, check stock availability
                if (stockRequestDTO.getTransactionType() == StockTransactionType.STOCK_OUT && 
                    (product.getStockQuantities() == null || product.getStockQuantities() < itemDTO.getQuantity())) {
                    throw new IllegalStateException("Insufficient stock quantity for product ID: " + itemDTO.getProductId());
                }

            StockRequestItemEntity item = new StockRequestItemEntity();
            StockRequestItemEntityId itemId = new StockRequestItemEntityId();
            itemId.setStockRequestId(stockRequest.getId());
            itemId.setProductId(product.getId());
            item.setId(itemId);
            item.setStockRequest(stockRequest);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setManufactureDate(itemDTO.getManufactureDate());
            item.setExpirationDate(itemDTO.getExpirationDate());
            item.setBatchNumber(itemDTO.getBatchNumber());
            item.setStorageLocation(itemDTO.getStorageLocation());
            item.setNotes(itemDTO.getNotes());

            items.add(item);
            }
        } else {
            throw new IllegalArgumentException("Stock request must contain at least one item");
        }

        stockRequest.setStockRequestItems(items);
        stockRequest = stockRequestRepository.save(stockRequest);

        return convertToStockRequestResponse(stockRequest);
    }

    @Override
    @Transactional
    public StockRequestResponse updateStockRequest(Long requestId, StockRequestDTO stockRequestDTO) {
        if (requestId == null || stockRequestDTO == null) {
            throw new IllegalArgumentException("Request ID and stock request data cannot be null");
        }
        
        StockRequestEntity stockRequest = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Request not found with ID: " + requestId));

        // Check if the request can be updated (only PENDING requests can be updated)
        if (stockRequest.getStatus() != StockStatus.PENDING) {
            throw new IllegalStateException("Cannot update a Stock Request that is not in PENDING status");
        }

        // Update the basic fields
        stockRequest.setNotes(stockRequestDTO.getNotes());
        stockRequest.setExpectedDeliveryDate(stockRequestDTO.getExpectedDeliveryDate());

        // Update supplier if it's a stock-in request
        if (stockRequest.getTransactionType() == StockTransactionType.STOCK_IN && stockRequestDTO.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(stockRequestDTO.getSupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + stockRequestDTO.getSupplierId()));
            stockRequest.setSupplier(supplier);
        }

        // Update department if it's a stock-out request
        if (stockRequest.getTransactionType() == StockTransactionType.STOCK_OUT && stockRequestDTO.getDepartmentId() != null) {
            DepartmentEntity department = departmentRepository.findById(stockRequestDTO.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + stockRequestDTO.getDepartmentId()));
            stockRequest.setDepartment(department);
        }

        // Instead of clearing the collection, we'll remove and add items properly
        // First, get the existing items to track what needs to be removed
        Set<StockRequestItemEntity> existingItems = new HashSet<>(stockRequest.getStockRequestItems());
        Set<StockRequestItemEntity> updatedItems = new HashSet<>();
        
        if (stockRequestDTO.getItems() == null || stockRequestDTO.getItems().isEmpty()) {
            throw new IllegalArgumentException("Stock request must contain at least one item");
        }

        // Process all items in the DTO
        for (StockRequestItemDTO itemDTO : stockRequestDTO.getItems()) {
            ProductEntity product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDTO.getProductId()));

            // Validate quantity
            if (itemDTO.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for product ID: " + itemDTO.getProductId());
            }
            
            // For STOCK_OUT transactions, check stock availability
            if (stockRequest.getTransactionType() == StockTransactionType.STOCK_OUT && 
                (product.getStockQuantities() == null || product.getStockQuantities() < itemDTO.getQuantity())) {
                throw new IllegalStateException("Insufficient stock quantity for product ID: " + itemDTO.getProductId());
            }

            // Check if the item already exists
            StockRequestItemEntityId itemId = new StockRequestItemEntityId();
            itemId.setStockRequestId(stockRequest.getId());
            itemId.setProductId(product.getId());
            
            // Find or create the item
            StockRequestItemEntity item = existingItems.stream()
                .filter(i -> i.getId().getProductId().equals(product.getId()))
                .findFirst()
                .orElse(new StockRequestItemEntity());
                
            item.setId(itemId);
            item.setStockRequest(stockRequest);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setManufactureDate(itemDTO.getManufactureDate());
            item.setExpirationDate(itemDTO.getExpirationDate());
            item.setBatchNumber(itemDTO.getBatchNumber());
            item.setStorageLocation(itemDTO.getStorageLocation());
            item.setNotes(itemDTO.getNotes());

            updatedItems.add(item);
        }

        // Update the stock request items with the new set
        stockRequest.getStockRequestItems().clear();
        stockRequest.getStockRequestItems().addAll(updatedItems);

        stockRequest = stockRequestRepository.save(stockRequest);
        return convertToStockRequestResponse(stockRequest);
    }

    @Override
    public StockRequestResponse getStockRequestById(Long requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException("Request ID cannot be null");
        }
        
        StockRequestEntity stockRequest = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Request not found with ID: " + requestId));
        return convertToStockRequestResponse(stockRequest);
    }

    @Override
    public Page<StockRequestResponse> getAllStockRequests(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return stockRequestRepository.findAll(pageable)
                .map(this::convertToStockRequestResponse);
    }

    @Override
    public Page<StockRequestResponse> getStockRequestsByType(StockTransactionType type, Pageable pageable) {
        if (type == null || pageable == null) {
            throw new IllegalArgumentException("Transaction type and pageable cannot be null");
        }
        
        return stockRequestRepository.findByTransactionType(type, pageable)
                .map(this::convertToStockRequestResponse);
    }

    @Override
    public Page<StockRequestResponse> getStockRequestsByStatus(StockStatus status, Pageable pageable) {
        if (status == null || pageable == null) {
            throw new IllegalArgumentException("Status and pageable cannot be null");
        }
        
        return stockRequestRepository.findByStatus(status, pageable)
                .map(this::convertToStockRequestResponse);
    }

    @Override
    public Page<StockRequestResponse> getStockRequestsByTypeAndStatus(StockTransactionType type, StockStatus status, Pageable pageable) {
        if (type == null || status == null || pageable == null) {
            throw new IllegalArgumentException("Transaction type, status, and pageable cannot be null");
        }
        
        return stockRequestRepository.findByTransactionTypeAndStatus(type, status, pageable)
                .map(this::convertToStockRequestResponse);
    }

    @Override
    public Page<StockRequestResponse> getStockRequestsByDateRange(Timestamp startDate, Timestamp endDate, Pageable pageable) {
        if (startDate == null || endDate == null || pageable == null) {
            throw new IllegalArgumentException("Start date, end date, and pageable cannot be null");
        }
        
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return stockRequestRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::convertToStockRequestResponse);
    }

    @Override
    @Transactional
    public StockRequestResponse approveStockRequest(Long requestId, Long approverId) {
        if (requestId == null || approverId == null) {
            throw new IllegalArgumentException("Request ID and approver ID cannot be null");
        }
        
        StockRequestEntity stockRequest = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Request not found with ID: " + requestId));
        
        // Check if the request can be approved (only PENDING requests can be approved)
        if (stockRequest.getStatus() != StockStatus.PENDING) {
            throw new IllegalStateException("Cannot approve a Stock Request that is not in PENDING status");
        }
        
        InventoryManagerEntity approver = inventoryManagerRepository.findById(approverId)
                .orElseThrow(() -> new EntityNotFoundException("Approver not found with ID: " + approverId));
        
        stockRequest.setStatus(StockStatus.APPROVED);
        stockRequest.setApprovedBy(approver);
        stockRequest.setApprovedDate(TimestampUtils.getCurrentTimestamp());
        
        stockRequest = stockRequestRepository.save(stockRequest);
        return convertToStockRequestResponse(stockRequest);
    }

    @Override
    @Transactional
    public StockRequestResponse cancelStockRequest(Long requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException("Request ID cannot be null");
        }
        
        StockRequestEntity stockRequest = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Request not found with ID: " + requestId));
        
        // Check if the request can be cancelled (only PENDING or APPROVED requests can be cancelled)
        if (stockRequest.getStatus() != StockStatus.PENDING && stockRequest.getStatus() != StockStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel a Stock Request that is already in progress or completed");
        }
        
        stockRequest.setStatus(StockStatus.CANCELLED);
        stockRequest = stockRequestRepository.save(stockRequest);
        return convertToStockRequestResponse(stockRequest);
    }

    @Override
    @Transactional
    public StockInvoiceResponse createStockInvoice(StockInvoiceDTO stockInvoiceDTO, Long inventoryManagerId) {
        if (stockInvoiceDTO == null || inventoryManagerId == null) {
            throw new IllegalArgumentException("Stock invoice data and inventory manager ID cannot be null");
        }
        
        StockRequestEntity stockRequest = stockRequestRepository.findById(stockInvoiceDTO.getStockRequestId())
                .orElseThrow(() -> new EntityNotFoundException("Stock Request not found with ID: " + stockInvoiceDTO.getStockRequestId()));
        
        // Check if the request is approved
        if (stockRequest.getStatus() != StockStatus.APPROVED) {
            throw new IllegalStateException("Cannot create invoice for a Stock Request that is not approved");
        }
        
        // Check if an invoice already exists for this request
        Optional<StockInvoiceEntity> existingInvoice = stockInvoiceRepository.findByStockRequest_Id(stockRequest.getId());
        if (existingInvoice.isPresent()) {
            throw new IllegalStateException("An invoice already exists for this Stock Request");
        }
        
        // Check if the stock request is already completed
        if (stockRequest.getStatus() == StockStatus.COMPLETED) {
            throw new IllegalStateException("Cannot create invoice for a Stock Request that is already completed");
        }
        
        InventoryManagerEntity inventoryManager = inventoryManagerRepository.findById(inventoryManagerId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory Manager not found with ID: " + inventoryManagerId));
        
        StockInvoiceEntity invoice = new StockInvoiceEntity();
        invoice.setInvoiceNumber(generateInvoiceNumber(stockRequest));
        invoice.setStockRequest(stockRequest);
        invoice.setTransactionType(stockRequest.getTransactionType());
        invoice.setInvoiceDate(TimestampUtils.getCurrentTimestamp());
        invoice.setCreatedBy(inventoryManager);
        invoice.setStatus(StockStatus.COMPLETED);
        
        // Set financial details
        invoice.setTotalAmount(calculateTotalAmount(stockRequest));
        invoice.setTaxAmount(stockInvoiceDTO.getTaxAmount());
        invoice.setShippingCost(stockInvoiceDTO.getShippingCost());
        
        // Set additional details
        invoice.setNotes(stockInvoiceDTO.getNotes());
        invoice.setPaymentMethod(stockInvoiceDTO.getPaymentMethod());
        invoice.setDueDate(stockInvoiceDTO.getDueDate());
        invoice.setPaymentDate(stockInvoiceDTO.getPaymentDate());
        
        // Update the stock request status
        stockRequest.setStatus(StockStatus.COMPLETED);
        stockRequestRepository.save(stockRequest);
        
        // Update product stock quantities
        updateStockQuantities(stockRequest);
        
        invoice = stockInvoiceRepository.save(invoice);
        return convertToStockInvoiceResponse(invoice);
    }

    @Override
    @Transactional
    public StockInvoiceResponse updateStockInvoice(Long invoiceId, StockInvoiceDTO stockInvoiceDTO) {
        if (invoiceId == null || stockInvoiceDTO == null) {
            throw new IllegalArgumentException("Invoice ID and invoice data cannot be null");
        }
        
        StockInvoiceEntity invoice = stockInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Invoice not found with ID: " + invoiceId));
        
        // Update financial details
        invoice.setTaxAmount(stockInvoiceDTO.getTaxAmount());
        invoice.setShippingCost(stockInvoiceDTO.getShippingCost());
        
        // Update additional details
        invoice.setNotes(stockInvoiceDTO.getNotes());
        invoice.setPaymentMethod(stockInvoiceDTO.getPaymentMethod());
        invoice.setDueDate(stockInvoiceDTO.getDueDate());
        invoice.setPaymentDate(stockInvoiceDTO.getPaymentDate());
        
        invoice = stockInvoiceRepository.save(invoice);
        return convertToStockInvoiceResponse(invoice);
    }

    @Override
    public StockInvoiceResponse getStockInvoiceById(Long invoiceId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        
        StockInvoiceEntity invoice = stockInvoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Invoice not found with ID: " + invoiceId));
        return convertToStockInvoiceResponse(invoice);
    }

    @Override
    public StockInvoiceResponse getStockInvoiceByRequestId(Long requestId) {
        if (requestId == null) {
            throw new IllegalArgumentException("Request ID cannot be null");
        }
        
        StockInvoiceEntity invoice = stockInvoiceRepository.findByStockRequest_Id(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Stock Invoice not found for Request ID: " + requestId));
        return convertToStockInvoiceResponse(invoice);
    }

    @Override
    public Page<StockInvoiceResponse> getAllStockInvoices(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        return stockInvoiceRepository.findAll(pageable)
                .map(this::convertToStockInvoiceResponse);
    }

    @Override
    public Page<StockInvoiceResponse> getStockInvoicesByType(StockTransactionType type, Pageable pageable) {
        if (type == null || pageable == null) {
            throw new IllegalArgumentException("Transaction type and pageable cannot be null");
        }
        
        return stockInvoiceRepository.findByTransactionType(type, pageable)
                .map(this::convertToStockInvoiceResponse);
    }

    @Override
    public Page<StockInvoiceResponse> getStockInvoicesByStatus(StockStatus status, Pageable pageable) {
        if (status == null || pageable == null) {
            throw new IllegalArgumentException("Status and pageable cannot be null");
        }
        
        return stockInvoiceRepository.findByStatus(status, pageable)
                .map(this::convertToStockInvoiceResponse);
    }

    @Override
    public Page<StockInvoiceResponse> getStockInvoicesByDateRange(Timestamp startDate, Timestamp endDate, Pageable pageable) {
        if (startDate == null || endDate == null || pageable == null) {
            throw new IllegalArgumentException("Start date, end date, and pageable cannot be null");
        }
        
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return stockInvoiceRepository.findByDateRange(startDate, endDate, pageable)
                .map(this::convertToStockInvoiceResponse);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateProductStock(Long productId, Integer quantity, StockTransactionType type) {
        if (productId == null || quantity == null || type == null) {
            throw new IllegalArgumentException("Product ID, quantity, and transaction type cannot be null");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        
        if (product.getStockQuantities() == null) {
            product.setStockQuantities(0);
        }
        
        if (type == StockTransactionType.STOCK_IN) {
            product.setStockQuantities(product.getStockQuantities() + quantity);
        } else if (type == StockTransactionType.STOCK_OUT) {
            int newQuantity = product.getStockQuantities() - quantity;
            if (newQuantity < 0) {
                throw new IllegalStateException("Insufficient stock quantity for product ID: " + productId);
            }
            product.setStockQuantities(newQuantity);
        }
        
        productRepository.save(product);
    }

    // Helper methods
    private StockRequestResponse convertToStockRequestResponse(StockRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        
        StockRequestResponse response = new StockRequestResponse();
        response.setId(entity.getId());
        response.setTransactionType(entity.getTransactionType());
        response.setRequestDate(entity.getRequestDate());
        response.setStatus(entity.getStatus());
        response.setApprovedDate(entity.getApprovedDate());
        response.setNotes(entity.getNotes());
        response.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());
        response.setHasInvoice(entity.getStockInvoice() != null);
        
        // Set supplier if available
        if (entity.getSupplier() != null) {
            SupplierResponse supplier = new SupplierResponse();
            supplier.setId(entity.getSupplier().getId());
            supplier.setName(entity.getSupplier().getName());
            supplier.setEmail(entity.getSupplier().getEmail());
            supplier.setPhoneNumber(entity.getSupplier().getPhoneNumber());
            response.setSupplier(supplier);
        }
        
        // Set department if available
        if (entity.getDepartment() != null) {
            DepartmentResponse department = new DepartmentResponse();
            department.setId(entity.getDepartment().getId());
            department.setName(entity.getDepartment().getName());
            department.setDescription(entity.getDepartment().getDescription());
            response.setDepartment(department);
        }
        
        // Set requested by (with null checks)
        if (entity.getRequestedBy() != null) {
        InventoryManagerResponse requestedBy = new InventoryManagerResponse();
        requestedBy.setId(entity.getRequestedBy().getId());
            
            if (entity.getRequestedBy().getStaffEntity() != null) {
        requestedBy.setFullName(entity.getRequestedBy().getStaffEntity().getFullName());
                
                // Email và Phone number nằm trong UserEntity, không phải StaffEntity
                if (entity.getRequestedBy().getStaffEntity().getUserEntity() != null) {
                    requestedBy.setEmail(entity.getRequestedBy().getStaffEntity().getUserEntity().getEmail());
                    requestedBy.setPhoneNumber(entity.getRequestedBy().getStaffEntity().getUserEntity().getPhoneNumber());
                }
            }
            
        response.setRequestedBy(requestedBy);
        }
        
        // Set approved by if available (with null checks)
        if (entity.getApprovedBy() != null) {
            InventoryManagerResponse approvedBy = new InventoryManagerResponse();
            approvedBy.setId(entity.getApprovedBy().getId());
            
            if (entity.getApprovedBy().getStaffEntity() != null) {
            approvedBy.setFullName(entity.getApprovedBy().getStaffEntity().getFullName());
                
                // Email và Phone number nằm trong UserEntity, không phải StaffEntity
                if (entity.getApprovedBy().getStaffEntity().getUserEntity() != null) {
                    approvedBy.setEmail(entity.getApprovedBy().getStaffEntity().getUserEntity().getEmail());
                    approvedBy.setPhoneNumber(entity.getApprovedBy().getStaffEntity().getUserEntity().getPhoneNumber());
                }
            }
            
            response.setApprovedBy(approvedBy);
        }
        
        // Set items (with null check)
        if (entity.getStockRequestItems() != null) {
        List<StockRequestItemResponse> items = entity.getStockRequestItems().stream()
                .map(this::convertToStockRequestItemResponse)
                .collect(Collectors.toList());
        response.setItems(items);
        } else {
            response.setItems(new ArrayList<>());
        }
        
        return response;
    }
    
    private StockRequestItemResponse convertToStockRequestItemResponse(StockRequestItemEntity entity) {
        if (entity == null || entity.getProduct() == null) {
            return null;
        }
        
        StockRequestItemResponse response = new StockRequestItemResponse();
        response.setProductId(entity.getProduct().getId());
        response.setProductName(entity.getProduct().getName());
        response.setProductImageUrl(entity.getProduct().getImageUrl());
        response.setQuantity(entity.getQuantity());
        response.setUnitPrice(entity.getUnitPrice());
        
        // Calculate total price if unit price is available
        if (entity.getUnitPrice() != null && entity.getQuantity() != null) {
            BigDecimal totalPrice = entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
            response.setTotalPrice(totalPrice);
        }
        
        response.setManufactureDate(entity.getManufactureDate());
        response.setExpirationDate(entity.getExpirationDate());
        response.setBatchNumber(entity.getBatchNumber());
        response.setStorageLocation(entity.getStorageLocation());
        response.setNotes(entity.getNotes());
        
        return response;
    }
    
    private StockInvoiceResponse convertToStockInvoiceResponse(StockInvoiceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        StockInvoiceResponse response = new StockInvoiceResponse();
        response.setId(entity.getId());
        response.setInvoiceNumber(entity.getInvoiceNumber());
        
        if (entity.getStockRequest() != null) {
        response.setStockRequestId(entity.getStockRequest().getId());
        }
        
        response.setTransactionType(entity.getTransactionType());
        response.setInvoiceDate(entity.getInvoiceDate());
        response.setTotalAmount(entity.getTotalAmount());
        response.setTaxAmount(entity.getTaxAmount());
        response.setShippingCost(entity.getShippingCost());
        
        // Calculate grand total
        BigDecimal grandTotal = entity.getTotalAmount() != null ? entity.getTotalAmount() : BigDecimal.ZERO;
        if (entity.getTaxAmount() != null) {
            grandTotal = grandTotal.add(entity.getTaxAmount());
        }
        if (entity.getShippingCost() != null) {
            grandTotal = grandTotal.add(entity.getShippingCost());
        }
        response.setGrandTotal(grandTotal);
        
        response.setStatus(entity.getStatus());
        response.setNotes(entity.getNotes());
        response.setPaymentMethod(entity.getPaymentMethod());
        response.setDueDate(entity.getDueDate());
        response.setPaymentDate(entity.getPaymentDate());
        
        // Set created by (with null checks)
        if (entity.getCreatedBy() != null) {
        InventoryManagerResponse createdBy = new InventoryManagerResponse();
        createdBy.setId(entity.getCreatedBy().getId());
            
            if (entity.getCreatedBy().getStaffEntity() != null) {
        createdBy.setFullName(entity.getCreatedBy().getStaffEntity().getFullName());
                
                // Email và Phone number nằm trong UserEntity, không phải StaffEntity
                if (entity.getCreatedBy().getStaffEntity().getUserEntity() != null) {
                    createdBy.setEmail(entity.getCreatedBy().getStaffEntity().getUserEntity().getEmail());
                    createdBy.setPhoneNumber(entity.getCreatedBy().getStaffEntity().getUserEntity().getPhoneNumber());
                }
            }
            
        response.setCreatedBy(createdBy);
        }
        
        return response;
    }
    
    private String generateInvoiceNumber(StockRequestEntity request) {
        if (request == null) {
            throw new IllegalArgumentException("Stock request cannot be null");
        }
        
        String prefix = request.getTransactionType() == StockTransactionType.STOCK_IN ? "IN" : "OUT";
        String timestamp = String.valueOf(System.currentTimeMillis());
        return prefix + "-" + request.getId() + "-" + timestamp.substring(timestamp.length() - 6);
    }
    
    private BigDecimal calculateTotalAmount(StockRequestEntity request) {
        if (request == null || request.getStockRequestItems() == null) {
            return BigDecimal.ZERO;
        }
        
        return request.getStockRequestItems().stream()
                .filter(item -> item.getUnitPrice() != null && item.getQuantity() != null)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    private void updateStockQuantities(StockRequestEntity request) {
        if (request == null || request.getStockRequestItems() == null) {
            return;
        }
        
        for (StockRequestItemEntity item : request.getStockRequestItems()) {
            if (item.getProduct() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                // Validate stock level for STOCK_OUT transactions before updating
                if (request.getTransactionType() == StockTransactionType.STOCK_OUT) {
                    ProductEntity product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + item.getProduct().getId()));
                    
                    if (product.getStockQuantities() == null || product.getStockQuantities() < item.getQuantity()) {
                        throw new IllegalStateException("Insufficient stock quantity for product ID: " + item.getProduct().getId());
                    }
                }
                
            updateProductStock(item.getProduct().getId(), item.getQuantity(), request.getTransactionType());
            }
        }
    }
} 