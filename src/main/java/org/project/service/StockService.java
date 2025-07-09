package org.project.service;

import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.model.request.StockInvoiceDTO;
import org.project.model.request.StockRequestDTO;
import org.project.model.response.StockInvoiceResponse;
import org.project.model.response.StockRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;

public interface StockService {
    
    // Stock Request Management
    StockRequestResponse createStockRequest(StockRequestDTO stockRequestDTO, Long inventoryManagerId);
    
    StockRequestResponse updateStockRequest(Long requestId, StockRequestDTO stockRequestDTO);
    
    StockRequestResponse getStockRequestById(Long requestId);
    
    Page<StockRequestResponse> getAllStockRequests(Pageable pageable);
    
    Page<StockRequestResponse> getStockRequestsByType(StockTransactionType type, Pageable pageable);
    
    Page<StockRequestResponse> getStockRequestsByStatus(StockStatus status, Pageable pageable);
    
    Page<StockRequestResponse> getStockRequestsByTypeAndStatus(
            StockTransactionType type, StockStatus status, Pageable pageable);
    
    Page<StockRequestResponse> getStockRequestsByDateRange(
            Timestamp startDate, Timestamp endDate, Pageable pageable);
    
    StockRequestResponse approveStockRequest(Long requestId, Long approverId);
    
    StockRequestResponse cancelStockRequest(Long requestId);
    
    // Stock Invoice Management
    StockInvoiceResponse createStockInvoice(StockInvoiceDTO stockInvoiceDTO, Long inventoryManagerId);
    
    StockInvoiceResponse updateStockInvoice(Long invoiceId, StockInvoiceDTO stockInvoiceDTO);
    
    StockInvoiceResponse getStockInvoiceById(Long invoiceId);
    
    StockInvoiceResponse getStockInvoiceByRequestId(Long requestId);
    
    Page<StockInvoiceResponse> getAllStockInvoices(Pageable pageable);
    
    Page<StockInvoiceResponse> getStockInvoicesByType(StockTransactionType type, Pageable pageable);
    
    Page<StockInvoiceResponse> getStockInvoicesByStatus(StockStatus status, Pageable pageable);
    
    Page<StockInvoiceResponse> getStockInvoicesByDateRange(
            Timestamp startDate, Timestamp endDate, Pageable pageable);
    
    // Inventory Updates
    void updateProductStock(Long productId, Integer quantity, StockTransactionType type);
} 