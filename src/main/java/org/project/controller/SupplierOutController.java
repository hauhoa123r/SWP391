package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.service.SupplierOutInvoiceService;
import org.project.service.SupplierOutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Controller for handling supplier stock out operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/supplier-outs")
public class SupplierOutController {

    private final SupplierOutService supplierOutService;
    private final SupplierOutInvoiceService supplierOutInvoiceService;
    private final ProductRepository productRepository;
    private final SupplierEntityRepository supplierRepository;
    
    private static final String REDIRECT_SUPPLIER_OUTS = "redirect:/supplier-outs";
    private static final String TEMPLATE_STOCK_OUT = "templates_storage/StockOut";
    private static final String TEMPLATE_STOCK_OUT_DETAIL = "templates_storage/StockOutDetail";

    /**
     * Main stock out page displaying all supplier outs
     * @param model Spring MVC Model
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param status Optional filter by status
     * @param search Optional search term
     * @param type Optional filter by type
     * @return View name for stock out page
     */
    @GetMapping
    public String getAllSupplierOuts(Model model, 
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String type) {
        log.info("Loading stock out page with filters - page: {}, size: {}, status: {}, search: {}, type: {}", 
                 page, size, status, search, type);
        
        try {
            // Get paginated supplier outs with filters
            Page<SupplierOutDTO> supplierOutsPage = supplierOutService.getFilteredSupplierOuts(page, size, status, search, type);
            
            // Populate model with data
            populateListPageModel(model, supplierOutsPage, page, size, status, search, type);
            
            log.debug("Stock out page prepared with {} supplier outs", supplierOutsPage.getNumberOfElements());
        } catch (Exception e) {
            log.error("Error preparing stock out page data: {}", e.getMessage(), e);
            handleListPageError(model, size);
        }
        
        return TEMPLATE_STOCK_OUT;
    }

    /**
     * View supplier out details page
     * @param id Supplier out ID
     * @param model Spring MVC Model
     * @return View name for supplier out details page
     */
    @GetMapping("/{id}")
    public String getSupplierOutById(@PathVariable Long id, Model model) {
        log.info("Viewing supplier out with ID: {}", id);
        
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                model.addAttribute("supplierOut", supplierOut);
                model.addAttribute("suppliers", supplierRepository.findAll());
                model.addAttribute("products", productRepository.findAll());
                log.debug("Supplier out details loaded for ID: {}", id);
                return TEMPLATE_STOCK_OUT_DETAIL;
            } else {
                log.warn("Supplier out with ID {} not found", id);
                return REDIRECT_SUPPLIER_OUTS;
            }
        } catch (Exception e) {
            log.error("Error loading supplier out details for ID {}: {}", id, e.getMessage(), e);
            return REDIRECT_SUPPLIER_OUTS;
        }
    }

    /**
     * Create supplier out form submission
     * @param supplierOutDTO Supplier out DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier outs page
     */
    @PostMapping
    public String createSupplierOut(@ModelAttribute SupplierOutDTO supplierOutDTO, 
                                   RedirectAttributes redirectAttributes) {
        log.info("Creating new supplier out");
        
        try {
            SupplierOutDTO created = supplierOutService.createSupplierOut(supplierOutDTO);
            log.debug("Created supplier out with ID: {}", created.getId());
            addSuccessMessage(redirectAttributes, "Stock Out request created successfully with ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating supplier out: {}", e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to create Stock Out request: " + e.getMessage());
        }
        
        return REDIRECT_SUPPLIER_OUTS;
    }

    /**
     * Update supplier out form submission
     * @param id Supplier out ID
     * @param supplierOutDTO Supplier out DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier out details page
     */
    @PostMapping("/{id}/update")
    public String updateSupplierOut(@PathVariable Long id, 
                                   @ModelAttribute SupplierOutDTO supplierOutDTO,
                                   RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out with ID: {}", id);
        
        try {
            SupplierOutDTO updated = supplierOutService.updateSupplierOut(id, supplierOutDTO);
            if (updated != null) {
                log.debug("Updated supplier out with ID: {}", id);
                addSuccessMessage(redirectAttributes, "Stock Out request updated successfully");
            } else {
                log.warn("Supplier out with ID {} not found for update", id);
                addErrorMessage(redirectAttributes, "Stock Out request not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to update Stock Out request: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
    }

    /**
     * Update supplier out status
     * @param id Supplier out ID
     * @param status New status
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier out details page
     */
    @PostMapping("/{id}/status")
    public String updateSupplierOutStatus(@PathVariable Long id, 
                                         @RequestParam String status,
                                         RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out status: id={}, status={}", id, status);
        
        try {
            supplierOutService.updateSupplierOutStatus(id, status);
            
            // If status is COMPLETED, save invoice
            if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
                processCompletedSupplierOut(id);
            }
            
            addSuccessMessage(redirectAttributes, "Status updated successfully to " + status);
        } catch (Exception e) {
            log.error("Error updating status for supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to update status: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
    }

    /**
     * Delete supplier out
     * @param id Supplier out ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier outs page
     */
    @PostMapping("/{id}/delete")
    public String deleteSupplierOut(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier out with ID: {}", id);
        
        try {
            supplierOutService.deleteSupplierOut(id);
            addSuccessMessage(redirectAttributes, "Stock Out request deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to delete Stock Out request: " + e.getMessage());
        }
        
        return REDIRECT_SUPPLIER_OUTS;
    }
    
    /**
     * Add item to supplier out
     * @param id Supplier out ID
     * @param itemDTO Item DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier out details page
     */
    @PostMapping("/{id}/items")
    public String addItemToSupplierOut(
            @PathVariable Long id, 
            @ModelAttribute SupplierRequestItemDTO itemDTO,
            RedirectAttributes redirectAttributes) {
        log.info("Adding item to supplier out with ID: {}", id);
        
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                supplierOut.getItems().add(itemDTO);
                supplierOutService.updateSupplierOut(id, supplierOut);
                log.debug("Added item to supplier out with ID: {}", id);
                addSuccessMessage(redirectAttributes, "Item added successfully");
            } else {
                log.warn("Supplier out with ID {} not found for adding item", id);
                addErrorMessage(redirectAttributes, "Stock Out request not found");
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to add item: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
    }
    
    /**
     * Test method to display sample data in the StockOut page
     * @param model Spring MVC Model
     * @return View name for stock out page with sample data
     */
    @GetMapping("/test")
    public String testStockOutPage(Model model) {
        log.info("Loading test stock out page with real data from database");
        
        try {
            // Get all supplier outs from database with default pagination
            Page<SupplierOutDTO> supplierOutsPage = supplierOutService.getFilteredSupplierOuts(0, 10, null, null, null);
            
            // Populate model with data from database
            populateListPageModel(model, supplierOutsPage, 0, 10, null, null, null);
            
            log.debug("Test stock out page prepared with {} real supplier outs", supplierOutsPage.getNumberOfElements());
        } catch (Exception e) {
            log.error("Error preparing test stock out page data: {}", e.getMessage(), e);
            handleListPageError(model, 10);
        }
        
        return TEMPLATE_STOCK_OUT;
    }
    
    // ==================== Helper methods ====================
    
    /**
     * Process a completed supplier out by saving its invoice
     * @param id Supplier out ID
     */
    private void processCompletedSupplierOut(Long id) {
        log.debug("Status is COMPLETED, saving invoice for supplier out with ID: {}", id);
        SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
        if (supplierOut != null) {
            supplierOutInvoiceService.saveInvoice(supplierOut);
        }
    }
    
    /**
     * Populate model for list page
     * @param model Spring MVC model
     * @param supplierOutsPage Page of supplier outs
     * @param page Current page number
     * @param size Page size
     * @param status Current status filter
     * @param search Current search term
     * @param type Current type filter
     */
    private void populateListPageModel(Model model, Page<SupplierOutDTO> supplierOutsPage, int page, int size, 
                                      String status, String search, String type) {
        // Add supplier outs
        model.addAttribute("supplierOuts", supplierOutsPage.getContent());
        
        // Add pagination data
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supplierOutsPage.getTotalPages());
        model.addAttribute("totalItems", supplierOutsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        // Add current filters for preserving state
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentType", type);
        
        // Add reference data for dropdowns
        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("statusValues", SupplierTransactionStatus.values());
    }
    
    /**
     * Handle error in list page by adding empty collections to model
     * @param model Spring MVC model
     * @param size Page size
     */
    private void handleListPageError(Model model, int size) {
        model.addAttribute("supplierOuts", Collections.emptyList());
        model.addAttribute("suppliers", Collections.emptyList());
        model.addAttribute("products", Collections.emptyList());
        model.addAttribute("statusValues", SupplierTransactionStatus.values());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 0);
        model.addAttribute("totalItems", 0);
        model.addAttribute("pageSize", size);
    }
    
    /**
     * Handle test page error by adding empty collections and error message to model
     * @param model Spring MVC model
     * @param errorMessage Error message
     */
    private void handleTestPageError(Model model, String errorMessage) {
        model.addAttribute("supplierOuts", Collections.emptyList());
        model.addAttribute("suppliers", Collections.emptyList());
        model.addAttribute("products", Collections.emptyList());
        model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + errorMessage);
    }
    
    /**
     * Add success message to redirect attributes
     * @param redirectAttributes Redirect attributes
     * @param message Success message
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
    }
    
    /**
     * Add error message to redirect attributes
     * @param redirectAttributes Redirect attributes
     * @param message Error message
     */
    private void addErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }
    
    /**
     * Get redirect URL to supplier out detail page
     * @param id Supplier out ID
     * @return Redirect URL
     */
    private String getRedirectToDetail(Long id) {
        return REDIRECT_SUPPLIER_OUTS + "/" + id;
    }
}