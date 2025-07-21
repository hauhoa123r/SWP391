package org.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.entity.InventoryManagerEntity;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.model.dto.SupplierRequestDTO;
import org.project.model.request.StockInvoiceDTO;
import org.project.model.response.StockInvoiceResponse;
import org.project.model.response.StockRequestResponse;
import org.project.repository.InventoryManagerRepository;
import org.project.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class StockController {

    private final StockService stockService;
    private final InventoryManagerRepository inventoryManagerRepository;

    // Stock-In (Create)
    @GetMapping("/stock-in")
    public String stockInPage(Model model) {
        model.addAttribute("transactionType", StockTransactionType.STOCK_IN);
        return "frontend/StockIn";
    }

    // Stock-In Invoice
    @GetMapping("/stock-in-invoice")
    public String stockInInvoicePage(Model model) {
        return "frontend/StockInInvoice";
    }

    // Stock-Out (Create)
    @GetMapping("/stock-out")
    public String stockOutPage(Model model) {
        model.addAttribute("transactionType", StockTransactionType.STOCK_OUT);
        return "frontend/StockOut";
    }

    // Stock-Out Invoice
    @GetMapping("/stock-out-invoice")
    public String stockOutInvoicePage(Model model) {
        return "frontend/StockOutInvoice";
    }

    // Customer Invoice
    @GetMapping("/customer-invoice")
    public String customerInvoicePage(Model model) {
        return "frontend/CustomerInvoice";
    }

    // Medical Equipment
    @GetMapping("/medical-equipment")
    public String medicalEquipmentPage(Model model) {
        return "frontend/medical-equipment";
    }

    // Medicine
    @GetMapping("/medicine")
    public String medicinePage(Model model) {
        return "frontend/medicine";
    }

    // Test Supplies
    @GetMapping("/test-supplies")
    public String testSuppliesPage(Model model) {
        return "frontend/test-supplies";
    }

    // API Endpoints for AJAX

    // Stock Request Management
    @PostMapping("/api/stock-requests")
    @ResponseBody
    public ResponseEntity<StockRequestResponse> createStockRequest(@Valid @RequestBody SupplierRequestDTO stockRequestDTO) {
        Long inventoryManagerId = getCurrentInventoryManagerId();
        StockRequestResponse response = stockService.createStockRequest(stockRequestDTO, inventoryManagerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/stock-requests/{id}")
    @ResponseBody
    public ResponseEntity<StockRequestResponse> updateStockRequest(
            @PathVariable("id") Long requestId,
            @Valid @RequestBody SupplierRequestDTO stockRequestDTO) {
        StockRequestResponse response = stockService.updateStockRequest(requestId, stockRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/stock-requests/{id}")
    @ResponseBody
    public ResponseEntity<StockRequestResponse> getStockRequestById(@PathVariable("id") Long requestId) {
        StockRequestResponse response = stockService.getStockRequestById(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/stock-requests")
    @ResponseBody
    public ResponseEntity<Page<StockRequestResponse>> getAllStockRequests(
            @RequestParam(required = false) StockTransactionType type,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @PageableDefault(size = 10, sort = "requestDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<StockRequestResponse> page;
        
        if (type != null && status != null) {
            page = stockService.getStockRequestsByTypeAndStatus(type, status, pageable);
        } else if (type != null) {
            page = stockService.getStockRequestsByType(type, pageable);
        } else if (status != null) {
            page = stockService.getStockRequestsByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            page = stockService.getStockRequestsByDateRange(startDate, endDate, pageable);
        } else {
            page = stockService.getAllStockRequests(pageable);
        }
        
        return ResponseEntity.ok(page);
    }

    @PostMapping("/api/stock-requests/{id}/approve")
    @ResponseBody
    public ResponseEntity<StockRequestResponse> approveStockRequest(@PathVariable("id") Long requestId) {
        Long inventoryManagerId = getCurrentInventoryManagerId();
        StockRequestResponse response = stockService.approveStockRequest(requestId, inventoryManagerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/stock-requests/{id}/cancel")
    @ResponseBody
    public ResponseEntity<StockRequestResponse> cancelStockRequest(@PathVariable("id") Long requestId) {
        StockRequestResponse response = stockService.cancelStockRequest(requestId);
        return ResponseEntity.ok(response);
    }

    // Stock Invoice Management
    @PostMapping("/api/stock-invoices")
    @ResponseBody
    public ResponseEntity<StockInvoiceResponse> createStockInvoice(@Valid @RequestBody StockInvoiceDTO stockInvoiceDTO) {
        Long inventoryManagerId = getCurrentInventoryManagerId();
        StockInvoiceResponse response = stockService.createStockInvoice(stockInvoiceDTO, inventoryManagerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/stock-invoices/{id}")
    @ResponseBody
    public ResponseEntity<StockInvoiceResponse> updateStockInvoice(
            @PathVariable("id") Long invoiceId,
            @Valid @RequestBody StockInvoiceDTO stockInvoiceDTO) {
        StockInvoiceResponse response = stockService.updateStockInvoice(invoiceId, stockInvoiceDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/stock-invoices/{id}")
    @ResponseBody
    public ResponseEntity<StockInvoiceResponse> getStockInvoiceById(@PathVariable("id") Long invoiceId) {
        StockInvoiceResponse response = stockService.getStockInvoiceById(invoiceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/stock-requests/{id}/invoice")
    @ResponseBody
    public ResponseEntity<StockInvoiceResponse> getStockInvoiceByRequestId(@PathVariable("id") Long requestId) {
        try {
            StockInvoiceResponse response = stockService.getStockInvoiceByRequestId(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/stock-invoices")
    @ResponseBody
    public ResponseEntity<Page<StockInvoiceResponse>> getAllStockInvoices(
            @RequestParam(required = false) StockTransactionType type,
            @RequestParam(required = false) StockStatus status,
            @RequestParam(required = false) Timestamp startDate,
            @RequestParam(required = false) Timestamp endDate,
            @PageableDefault(size = 10, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<StockInvoiceResponse> page;
        
        if (type != null && status != null) {
            page = stockService.getStockInvoicesByType(type, pageable);
        } else if (type != null) {
            page = stockService.getStockInvoicesByType(type, pageable);
        } else if (status != null) {
            page = stockService.getStockInvoicesByStatus(status, pageable);
        } else if (startDate != null && endDate != null) {
            page = stockService.getStockInvoicesByDateRange(startDate, endDate, pageable);
        } else {
            page = stockService.getAllStockInvoices(pageable);
        }
        
        return ResponseEntity.ok(page);
    }

    // Utility methods
    @GetMapping("/api/stock-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStockStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("stockStatus", StockStatus.values());
        response.put("transactionTypes", StockTransactionType.values());
        return ResponseEntity.ok(response);
    }

    private Long getCurrentInventoryManagerId() {
        // Tạm thời sử dụng inventory manager đầu tiên trong database
        // Trong thực tế, cần implement logic để lấy ID của user hiện tại
        return inventoryManagerRepository.findAll().stream()
                .findFirst()
                .map(InventoryManagerEntity::getId)
                .orElse(1L); // Fallback value
    }

//    private Long getCurrentInventoryManagerId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        // Assuming that username is the email of the staff entity
//        return inventoryManagerRepository.findByStaffEntity_Id(1L) // Replace with actual logic to find staff by email
//                .orElseThrow(() -> new IllegalStateException("Current user is not an inventory manager"))
//                .getId();
//    }
} 