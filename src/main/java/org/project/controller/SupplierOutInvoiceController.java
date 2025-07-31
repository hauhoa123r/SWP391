package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierOutDTO;
import org.project.service.SupplierOutService;
import org.project.utils.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for handling supplier out invoices
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/supplier-out-invoices")
public class SupplierOutInvoiceController {

    private final SupplierOutService supplierOutService;
    private final PageUtils<SupplierOutDTO> pageUtils;

    /**
     * Main invoice page displaying all supplier out invoices
     *
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
            Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate");
            Pageable pageable = PageRequest.of(page, size, sort);

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
            log.debug("Calling supplierOutService.getFilteredTransactions with page={}, size={}, keyword={}, filteredStatus={}, allowedStatuses={}",
                    page, size, keyword, filteredStatus, allowedStatuses);

            Page<SupplierOutDTO> transactionsPage = supplierOutService.getFilteredTransactions(
                    page, size, keyword, filteredStatus, allowedStatuses, SupplierTransactionType.STOCK_OUT);

            // DEBUG: Log fetched transactions size and content
            log.info("Fetched {} transactions from service", transactionsPage.getContent().size());
            transactionsPage.getContent().forEach(transaction ->
                    log.debug("Transaction ID: {}, Number: {}, Date: {}, Status: {}, Reason: {}",
                            transaction.getId(), transaction.getInvoiceNumber(),
                            transaction.getTransactionDate(), transaction.getStatus(), transaction.getStockOutReason()));

            // Lọc các transaction theo stockOutReason (Bán hàng hoặc Khác)
            List<SupplierOutDTO> filteredTransactions = transactionsPage.getContent().stream()
                    .filter(transaction -> {
                        // Nếu lý do là "Bán hàng" hoặc không thuộc các lý do cụ thể khác thì hiển thị
                        String reason = transaction.getStockOutReason() != null ? transaction.getStockOutReason().toUpperCase() : "";
                        return reason.contains("BÁN HÀNG") || reason.contains("SALE") ||
                                (!reason.contains("CHUYỂN KHO") && !reason.contains("TRANSFER") &&
                                        !reason.contains("TRẢ HÀNG") && !reason.contains("RETURN") &&
                                        !reason.contains("HẾT HẠN") && !reason.contains("EXPIRED") &&
                                        !reason.contains("HƯ HỎNG") && !reason.contains("DAMAGED"));
                    })
                    .collect(Collectors.toList());

            // Create a new page with filtered content
            Page<SupplierOutDTO> filteredPage = pageUtils.createPage(filteredTransactions,
                    PageRequest.of(page, size), transactionsPage.getTotalElements());

            // Add all data to model
            mv.addObject("invoices", filteredPage.getContent());
            mv.addObject("currentPage", page);
            mv.addObject("totalPages", filteredPage.getTotalPages());
            mv.addObject("totalItems", filteredPage.getTotalElements());
            mv.addObject("keyword", keyword);
            mv.addObject("status", status);
            mv.addObject("startDate", startDate);
            mv.addObject("endDate", endDate);
            mv.addObject("pageSize", size);

            // Add allowed statuses for dropdown
            mv.addObject("availableStatuses", allowedStatuses);

            log.debug("Supplier out invoices page prepared with {} transactions", filteredPage.getContent().size());
        } catch (Exception e) {
            log.error("Error preparing supplier out invoices page data: {}", e.getMessage(), e);
            mv.addObject("errorMessage", "Error loading invoices: " + e.getMessage());
        }

        return mv;
    }

    /**
     * View invoice details page
     *
     * @param id Invoice ID
     * @return ModelAndView for invoice details page
     */
    @GetMapping("/{id}")
    public ModelAndView getInvoiceById(@PathVariable Long id) {
        log.info("Viewing supplier out invoice with ID: {}", id);

        ModelAndView mv = new ModelAndView("templates_storage/StockOutDetail");

        try {
            SupplierOutDTO transaction = supplierOutService.getSupplierOutById(id);
            if (transaction != null &&
                    (transaction.getStatus() == SupplierTransactionStatus.COMPLETED ||
                            transaction.getStatus() == SupplierTransactionStatus.REJECTED)) {

                mv.addObject("invoice", transaction);
                // Add with the alternative name that the template is expecting
                mv.addObject("supplierOut", transaction);

                // Debug info
                log.debug("Supplier out invoice details loaded for ID: {}", id);
                mv.addObject("debugInfo", "Invoice ID: " + id +
                        ", status: " + transaction.getStatus() +
                        ", invoice number: " + transaction.getInvoiceNumber());

            } else {
                log.warn("Supplier out invoice with ID {} not found or not in completed/rejected status", id);
                mv.addObject("errorMessage", "Invoice not found or not in appropriate status");
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
        log.info("Updating supplier out invoice status: ID={}, newStatus={}", id, status);

        try {
            SupplierOutDTO updatedTransaction = supplierOutService.updateSupplierOutStatus(id, status.name());
            if (updatedTransaction != null) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Invoice status updated to " + status.getDisplayName());
                return "redirect:/supplier-out-invoices/" + id;
            } else {
                log.warn("Failed to update supplier out invoice status: ID={}, newStatus={}", id, status);
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update invoice status");
                return "redirect:/supplier-out-invoices";
            }
        } catch (Exception e) {
            log.error("Error updating supplier out invoice status: ID={}, newStatus={}, error={}",
                    id, status, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating invoice status: " + e.getMessage());
            return "redirect:/supplier-out-invoices";
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
    public String deleteInvoice(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier out invoice with ID: {}", id);

        try {
            // Kiểm tra xem ID tồn tại không và có phải là trạng thái COMPLETED/REJECTED hay không
            SupplierOutDTO transaction = supplierOutService.getSupplierOutById(id);
            if (transaction != null &&
                    (transaction.getStatus() == SupplierTransactionStatus.COMPLETED ||
                            transaction.getStatus() == SupplierTransactionStatus.REJECTED)) {

                supplierOutService.deleteSupplierOut(id);
                redirectAttributes.addFlashAttribute("successMessage", "Invoice deleted successfully");
                return "redirect:/supplier-out-invoices";
            } else {
                log.warn("Cannot delete supplier out invoice: ID={} not found or not in COMPLETED/REJECTED status", id);
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Invoice not found or cannot be deleted due to its status");
                return "redirect:/supplier-out-invoices";
            }
        } catch (Exception e) {
            log.error("Error deleting supplier out invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting invoice: " + e.getMessage());
            return "redirect:/supplier-out-invoices";
        }
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
}