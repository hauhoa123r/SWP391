package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.service.SupplierInService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.project.enums.SupplierTransactionType;

/**
 * Controller for handling supplier in invoices
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/warehouse/invoices/in")
public class SupplierInInvoiceController {

    private final SupplierInService supplierInService;

    /**
     * Main invoice page displaying all supplier in invoices
     * 
     * @return Model for invoice page
     */
    @GetMapping
    public String getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            Model model) {
        log.info("Loading supplier in invoices page with filters - page: {}, size: {}, keyword: {}, status: {}",
                page, size, keyword, status);

        try {
            // Define allowed statuses for StockInInvoice page
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                    SupplierTransactionStatus.COMPLETED, // Hoàn thành
                    SupplierTransactionStatus.REJECTED // Từ chối
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
            log.debug(
                    "Calling supplierInService.getFilteredTransactions with page={}, size={}, keyword={}, filteredStatus={}, allowedStatuses={}",
                    page, size, keyword, filteredStatus, allowedStatuses);

            // Sử dụng SupplierInService để lấy các transactions có trạng thái COMPLETED
            // hoặc REJECTED
            Page<SupplierInDTO> transactionsPage = supplierInService.getFilteredTransactions(
                    page, size, keyword, filteredStatus, allowedStatuses, SupplierTransactionType.STOCK_IN);
            
            // Debug: Log all transactions to see what's being fetched
            System.out.println("=== StockInInvoice Debug ===");
            System.out.println("Total transactions found: " + transactionsPage.getTotalElements());
            System.out.println("Transactions on current page: " + transactionsPage.getContent().size());
            transactionsPage.getContent().forEach(transaction -> {
                System.out.println("Transaction ID: " + transaction.getId() + 
                                 ", Invoice: " + transaction.getInvoiceNumber() + 
                                 ", Status: " + transaction.getStatus() + 
                                 ", Date: " + transaction.getTransactionDate());
            });
            System.out.println("=== End Debug ===");

            // DEBUG: Log fetched transactions size and content
            log.info("Fetched {} transactions from service", transactionsPage.getContent().size());
            transactionsPage.getContent()
                    .forEach(transaction -> log.debug("Transaction ID: {}, Number: {}, Date: {}, Status: {}",
                            transaction.getId(), transaction.getInvoiceNumber(),
                            transaction.getTransactionDate(), transaction.getStatus()));

            model.addAttribute("invoices", transactionsPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", transactionsPage.getTotalPages());
            model.addAttribute("totalItems", transactionsPage.getTotalElements());
            model.addAttribute("keyword", keyword);
            model.addAttribute("status", status);

            // Add allowed statuses for dropdown
            model.addAttribute("availableStatuses", allowedStatuses);

            // Add current user for forms
            model.addAttribute("currentUser", getCurrentUser());

            log.debug("Supplier in invoices page prepared with {} transactions", transactionsPage.getContent().size());
        } catch (Exception e) {
            log.error("Error preparing supplier in invoices page data: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading invoices: " + e.getMessage());
        }

        return "templates_storage/StockInInvoice";
    }

    /**
     * View invoice details page
     * 
     * @param id Invoice ID
     * @return Model for invoice details page
     */
    @GetMapping("/{id}")
    public String getInvoiceById(@PathVariable Long id, Model model) {
        log.info("Viewing supplier in invoice with ID: {}", id);

        try {
            SupplierInDTO transaction = supplierInService.getSupplierInById(id);
            if (transaction != null &&
                    (transaction.getStatus() == SupplierTransactionStatus.COMPLETED ||
                            transaction.getStatus() == SupplierTransactionStatus.REJECTED)) {
                model.addAttribute("invoice", transaction);
                // Add with the alternative name that the template is expecting
                model.addAttribute("supplierIn", transaction);

                // Debug info
                log.debug("Supplier in invoice details loaded for ID: {}", id);
                model.addAttribute("debugInfo", "Invoice ID: " + id +
                        ", status: " + transaction.getStatus() +
                        ", invoice number: " + transaction.getInvoiceNumber());

                return "templates_storage/StockInDetail";
            } else {
                log.warn("Supplier in invoice with ID {} not found or not in COMPLETED/REJECTED status", id);
                model.addAttribute("errorMessage", "Invoice not found or not in appropriate status");
                return "redirect:/warehouse/invoices/in";
            }
        } catch (Exception e) {
            log.error("Error loading supplier in invoice details for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading invoice: " + e.getMessage());
            return "redirect:/warehouse/invoices/in";
        }
    }

    /**
     * Update invoice status
     * 
     * @param id                 Invoice ID
     * @param status             New status
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to invoice details page
     */
    @PostMapping("/{id}/status")
    public String updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam SupplierTransactionStatus status,
            RedirectAttributes redirectAttributes) {
        log.info("Updating supplier in invoice status: ID={}, newStatus={}", id, status);

        try {
            SupplierInDTO updatedTransaction = supplierInService.updateSupplierInStatus(id, status.name());
            if (updatedTransaction != null) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Invoice status updated to " + status.getDisplayName());
                return "redirect:/warehouse/invoices/in/" + id;
            } else {
                log.warn("Failed to update supplier in invoice status: ID={}, newStatus={}", id, status);
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update invoice status");
                return "redirect:/warehouse/invoices/in";
            }
        } catch (Exception e) {
            log.error("Error updating supplier in invoice status: ID={}, newStatus={}, error={}",
                    id, status, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating invoice status: " + e.getMessage());
            return "redirect:/warehouse/invoices/in";
        }
    }

    /**
     * Delete invoice
     * 
     * @param id                 Invoice ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to invoice list page
     */
    @PostMapping("/{id}/delete")
    public String deleteInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier in invoice with ID: {}", id);

        try {
            // Kiểm tra xem ID tồn tại không và có phải là trạng thái COMPLETED/REJECTED hay
            // không
            SupplierInDTO transaction = supplierInService.getSupplierInById(id);
            if (transaction != null &&
                    (transaction.getStatus() == SupplierTransactionStatus.COMPLETED ||
                            transaction.getStatus() == SupplierTransactionStatus.REJECTED)) {

                supplierInService.deleteSupplierIn(id);
                redirectAttributes.addFlashAttribute("successMessage", "Invoice deleted successfully");
                return "redirect:/warehouse/invoices/in";
            } else {
                log.warn("Cannot delete supplier in invoice: ID={} not found or not in COMPLETED/REJECTED status", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Invoice not found or cannot be deleted due to its status");
                return "redirect:/warehouse/invoices/in";
            }
        } catch (Exception e) {
            log.error("Error deleting supplier in invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting invoice: " + e.getMessage());
            return "redirect:/warehouse/invoices/in";
        }
    }
    
    /**
     * Get current user - placeholder method
     * @return Current user object or null
     */
    private Object getCurrentUser() {
        // TODO: Implement proper user authentication
        // For now, return a simple object with required properties
        return new Object() {
            public Long getId() { return 256L; }
            public String getFullName() { return "Người dùng"; }
            public String getRoleName() { return "STAFF"; }
            public String getAvatar() { return "/templates_storage/assets/images/avatar.png"; }
        };
    }
}