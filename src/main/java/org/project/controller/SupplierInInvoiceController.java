package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.service.SupplierInInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling supplier in invoices
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/supplier-in-invoices")
public class SupplierInInvoiceController {

    private final SupplierInInvoiceService supplierInInvoiceService;

    /**
     * Main invoice page displaying all supplier in invoices
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
            Page<SupplierInvoiceDTO> invoicesPage = supplierInInvoiceService.getAllInvoices(page, size, keyword, status);
            model.addAttribute("invoices", invoicesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", invoicesPage.getTotalPages());
            model.addAttribute("totalItems", invoicesPage.getTotalElements());
            model.addAttribute("keyword", keyword);
            model.addAttribute("status", status);
            
            // Add available statuses for dropdown
            List<String> availableStatuses = Arrays.stream(SupplierTransactionStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            model.addAttribute("availableStatuses", availableStatuses);
            
            log.debug("Supplier in invoices page prepared with {} invoices", invoicesPage.getContent().size());
        } catch (Exception e) {
            log.error("Error preparing supplier in invoices page data: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading invoices: " + e.getMessage());
        }

        return "templates_storage/StockInInvoice";
    }

    /**
     * View invoice details page
     * @param id Invoice ID
     * @return Model for invoice details page
     */
    @GetMapping("/{id}")
    public String getInvoiceById(@PathVariable Long id, Model model) {
        log.info("Viewing supplier in invoice with ID: {}", id);
        
        try {
            SupplierInvoiceDTO invoice = supplierInInvoiceService.getInvoiceById(id);
            if (invoice != null) {
                model.addAttribute("invoice", invoice);
                log.debug("Supplier in invoice details loaded for ID: {}", id);
                return "templates_storage/StockInDetail";
            } else {
                log.warn("Supplier in invoice with ID {} not found", id);
                model.addAttribute("errorMessage", "Invoice not found");
                return "redirect:/supplier-in-invoices";
            }
        } catch (Exception e) {
            log.error("Error loading supplier in invoice details for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Error loading invoice: " + e.getMessage());
            return "redirect:/supplier-in-invoices";
        }
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
        log.info("Updating supplier in invoice status with ID: {} to status: {}", id, status);
        
        try {
            SupplierInvoiceDTO updated = supplierInInvoiceService.updateInvoiceStatus(id, status);
            if (updated != null) {
                log.debug("Updated supplier in invoice status with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Invoice status updated successfully to " + status);
            } else {
                log.warn("Supplier in invoice with ID {} not found for status update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Invoice not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier in invoice status with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update invoice status: " + e.getMessage());
        }
        
        return "redirect:/supplier-in-invoices/" + id;
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
        log.info("Updating supplier in invoice with ID: {}", id);
        
        try {
            SupplierInvoiceDTO updated = supplierInInvoiceService.updateInvoice(id, invoiceDTO);
            if (updated != null) {
                log.debug("Updated supplier in invoice with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Invoice updated successfully");
            } else {
                log.warn("Supplier in invoice with ID {} not found for update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Invoice not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier in invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update invoice: " + e.getMessage());
        }
        
        return "redirect:/supplier-in-invoices/" + id;
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
        log.info("Deleting supplier in invoice with ID: {}", id);
        
        try {
            supplierInInvoiceService.deleteInvoice(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Invoice deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier in invoice with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete invoice: " + e.getMessage());
        }
        
        return "redirect:/supplier-in-invoices";
    }
}