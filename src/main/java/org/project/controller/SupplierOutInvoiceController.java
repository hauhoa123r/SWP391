package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.service.SupplierOutInvoiceService;
import org.project.utils.PageUtils;
import org.project.utils.specification.PageSpecificationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.project.enums.SupplierTransactionType;
import org.project.entity.SupplierInvoiceEntity;
import java.math.BigDecimal;

/**
 * Controller for handling supplier out invoices
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/supplier-out-invoices")
public class SupplierOutInvoiceController {

    private final SupplierOutInvoiceService supplierOutInvoiceService;
    private final PageUtils<SupplierInvoiceDTO> pageUtils;

    /**
     * Main invoice page displaying all supplier out invoices
     * @return ModelAndView for invoice page
     */
    @GetMapping
    public ModelAndView getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Loading supplier out invoices page with filters - page: {}, size: {}, keyword: {}, status: {}, startDate: {}, endDate: {}", 
                page, size, keyword, status, startDate, endDate);
        
        ModelAndView mv = new ModelAndView("templates_storage/StockOutInvoice");
        
        try {
            // Parse sort parameter
            Sort sort = Sort.by(Sort.Direction.DESC, "invoiceDate");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Convert LocalDateTime to Timestamp if dates are provided
            Timestamp startTimestamp = startDate != null ? Timestamp.valueOf(startDate) : null;
            Timestamp endTimestamp = endDate != null ? Timestamp.valueOf(endDate) : null;
            
            // Define allowed statuses for StockOutInvoice page
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.COMPLETED,  // Hoàn thành
                SupplierTransactionStatus.REJECTED    // Từ chối
            );
            
            // Override status parameter if it's not in the allowed list
            SupplierTransactionStatus statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = SupplierTransactionStatus.valueOf(status);
                    if (!allowedStatuses.contains(statusEnum)) {
                        statusEnum = null; // Reset if not in allowed list
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid status, keep it null
                }
            }
            
            // Convert enum to string if valid
            String filteredStatus = statusEnum != null ? statusEnum.name() : null;
            
            // DEBUG: Log before calling service
            log.debug("Calling supplierOutInvoiceService.getFilteredInvoices with page={}, size={}, keyword={}, filteredStatus={}, allowedStatuses={}",
                    page, size, keyword, filteredStatus, allowedStatuses);
            
            Page<SupplierInvoiceDTO> invoicesPage = supplierOutInvoiceService.getFilteredInvoices(
                page, size, keyword, filteredStatus, allowedStatuses);
                
            // DEBUG: Log fetched invoices size and content
            log.info("Fetched {} invoices from service", invoicesPage.getContent().size());
            invoicesPage.getContent().forEach(invoice -> 
                log.debug("Invoice ID: {}, Number: {}, Date: {}, Status: {}", 
                    invoice.getId(), invoice.getInvoiceNumber(), 
                    invoice.getInvoiceDate(), invoice.getStatus()));
            
            // Add all data to model
            mv.addObject("invoices", invoicesPage.getContent());
            mv.addObject("currentPage", page);
            mv.addObject("totalPages", invoicesPage.getTotalPages());
            mv.addObject("totalItems", invoicesPage.getTotalElements());
            mv.addObject("keyword", keyword);
            mv.addObject("status", status);
            mv.addObject("startDate", startDate);
            mv.addObject("endDate", endDate);
            mv.addObject("pageSize", size);
            
            // Add allowed statuses for dropdown
            mv.addObject("availableStatuses", allowedStatuses);
            
            log.debug("Supplier out invoices page prepared with {} invoices", invoicesPage.getContent().size());
        } catch (Exception e) {
            log.error("Error preparing supplier out invoices page data: {}", e.getMessage(), e);
            mv.addObject("errorMessage", "Error loading invoices: " + e.getMessage());
        }

        return mv;
    }

    /**
     * View invoice details page
     * @param id Invoice ID
     * @return ModelAndView for invoice details page
     */
    @GetMapping("/{id}")
    public ModelAndView getInvoiceById(@PathVariable Long id) {
        log.info("Viewing supplier out invoice with ID: {}", id);
        
        ModelAndView mv = new ModelAndView("templates_storage/StockOutDetail");
        
        try {
            SupplierInvoiceDTO invoice = supplierOutInvoiceService.getInvoiceById(id);
            if (invoice != null) {
                mv.addObject("invoice", invoice);
                // Add with the alternative name that the template is expecting
                mv.addObject("supplierOut", invoice);
                
                // Debug info
                log.debug("Supplier out invoice details loaded for ID: {}", id);
                mv.addObject("debugInfo", "Invoice ID: " + id + 
                    ", status: " + invoice.getStatus() + 
                    ", invoice number: " + invoice.getInvoiceNumber());
                
            } else {
                log.warn("Supplier out invoice with ID {} not found", id);
                mv.addObject("errorMessage", "Invoice not found");
                mv.setViewName("redirect:/supplier-out-invoices");
            }
        } catch (Exception e) {
            log.error("Error loading supplier out invoice details for ID {}: {}", id, e.getMessage(), e);
            mv.addObject("errorMessage", "Error loading invoice: " + e.getMessage());
            mv.setViewName("redirect:/supplier-out-invoices");
        }

        return mv;
    }

    /**
     * Update invoice status
     * @param id Invoice ID
     * @param status New status
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to invoice details page
     */
    @PostMapping("/{id}/update-status")
    public String updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam SupplierTransactionStatus status,
            RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out invoice status with ID: {} to status: {}", id, status);
        
        try {
            SupplierInvoiceDTO updated = supplierOutInvoiceService.updateInvoiceStatus(id, status);
            if (updated != null) {
                log.debug("Updated supplier out invoice status with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Invoice status updated successfully to " + status);
            } else {
                log.warn("Supplier out invoice with ID {} not found for status update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Invoice not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier out invoice status with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update invoice status: " + e.getMessage());
        }
        
        return "redirect:/supplier-out-invoices/" + id;
    }

    /**
     * Update invoice form submission
     * @param id Invoice ID
     * @param invoiceDTO Invoice DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to invoice details page
     */
    @PostMapping("/{id}/update")
    public String updateInvoice(@PathVariable Long id, 
                               @ModelAttribute SupplierInvoiceDTO invoiceDTO,
                               RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out invoice with ID: {}", id);
        
        try {
            SupplierInvoiceDTO updated = supplierOutInvoiceService.updateInvoice(id, invoiceDTO);
            if (updated != null) {
                log.debug("Updated supplier out invoice with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Invoice updated successfully");
            } else {
                log.warn("Supplier out invoice with ID {} not found for update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Invoice not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier out invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update invoice: " + e.getMessage());
        }
        
        return "redirect:/supplier-out-invoices/" + id;
    }

    /**
     * Delete invoice
     * @param id Invoice ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to invoices page
     */
    @PostMapping("/{id}/delete")
    public String deleteInvoice(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier out invoice with ID: {}", id);
        
        try {
            supplierOutInvoiceService.deleteInvoice(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Invoice deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier out invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete invoice: " + e.getMessage());
        }
        
        return "redirect:/supplier-out-invoices";
    }
    
    /**
     * Redirect from StockOutInvoice.html to this controller
     */
    @GetMapping("/inventory/api/stock-invoices")
    public String redirectApiToController(
            @RequestParam(required = false) Map<String, String> allParams) {
        StringBuilder redirectUrl = new StringBuilder("redirect:/supplier-out-invoices");
        
        // Forward any query parameters
        if (!allParams.isEmpty()) {
            redirectUrl.append("?");
            allParams.forEach((key, value) -> redirectUrl.append(key).append("=").append(value).append("&"));
            // Remove the trailing "&"
            redirectUrl.setLength(redirectUrl.length() - 1);
        }
        
        return redirectUrl.toString();
    }
    
    /**
     * Redirect from StockOutInvoice.html detail view to this controller
     */
    @GetMapping("/inventory/api/stock-invoices/{id}")
    public String redirectApiDetailToController(@PathVariable Long id) {
        return "redirect:/supplier-out-invoices/" + id;
    }

    /**
     * Create test invoices for debugging
     * @return Response with status
     */
    @GetMapping("/create-test")
    @ResponseBody
    public String createTestData() {
        log.info("Creating test supplier out invoices");
        
        try {
            // Tạo hóa đơn COMPLETED
            SupplierInvoiceEntity invoice1 = new SupplierInvoiceEntity();
            invoice1.setInvoiceNumber("SO-TEST-001");
            invoice1.setTransactionType(SupplierTransactionType.STOCK_OUT);
            invoice1.setInvoiceDate(new Timestamp(System.currentTimeMillis()));
            invoice1.setTotalAmount(new BigDecimal("1500000"));
            invoice1.setTaxAmount(new BigDecimal("150000"));
            invoice1.setShippingCost(new BigDecimal("75000"));
            invoice1.setGrandTotal(new BigDecimal("1725000"));
            invoice1.setStatus(SupplierTransactionStatus.COMPLETED);
            invoice1.setNotes("Test completed stock out invoice");
            
            // Tạo hóa đơn REJECTED
            SupplierInvoiceEntity invoice2 = new SupplierInvoiceEntity();
            invoice2.setInvoiceNumber("SO-TEST-002");
            invoice2.setTransactionType(SupplierTransactionType.STOCK_OUT);
            invoice2.setInvoiceDate(new Timestamp(System.currentTimeMillis()));
            invoice2.setTotalAmount(new BigDecimal("3000000"));
            invoice2.setTaxAmount(new BigDecimal("300000"));
            invoice2.setShippingCost(new BigDecimal("100000"));
            invoice2.setGrandTotal(new BigDecimal("3400000"));
            invoice2.setStatus(SupplierTransactionStatus.REJECTED);
            invoice2.setNotes("Test rejected stock out invoice");
            
            // Lưu hóa đơn
            supplierOutInvoiceService.saveTestInvoices(List.of(invoice1, invoice2));
            
            return "Đã tạo 2 hóa đơn mẫu thành công";
        } catch (Exception e) {
            log.error("Error creating test invoices: {}", e.getMessage(), e);
            return "Lỗi khi tạo hóa đơn mẫu: " + e.getMessage();
        }
    }
}