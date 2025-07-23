package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.service.SupplierInInvoiceService;
import org.project.service.SupplierInService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
 * Controller for handling supplier stock in operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/supplier-ins")
public class SupplierInController {

    private final SupplierInService supplierInService;
    private final SupplierInInvoiceService supplierInInvoiceService;
    private final ProductRepository productRepository;
    private final SupplierEntityRepository supplierRepository;

    /**
     * Main stock in page displaying all supplier ins
     * @param model Spring MVC Model
     * @return View name for stock in page
     */
    @GetMapping
    public String getAllSupplierIns(Model model) {
        log.info("Loading stock in page");
        
        try {
            // Get all supplier ins
            List<SupplierInDTO> supplierIns = supplierInService.getAllSupplierIns();
            model.addAttribute("supplierIns", supplierIns);
            
            // Add suppliers and products for form dropdowns
            model.addAttribute("suppliers", supplierRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            
            log.debug("Stock in page prepared with {} supplier ins", supplierIns.size());
        } catch (Exception e) {
            log.error("Error preparing stock in page data: {}", e.getMessage(), e);
            // Add empty lists on error to avoid template errors
            model.addAttribute("supplierIns", Collections.emptyList());
            model.addAttribute("suppliers", Collections.emptyList());
            model.addAttribute("products", Collections.emptyList());
        }
        
        return "templates_storage/StockIn";
    }

    /**
     * View supplier in details page
     * @param id Supplier in ID
     * @param model Spring MVC Model
     * @return View name for supplier in details page
     */
    @GetMapping("/{id}")
    public String getSupplierInById(@PathVariable Long id, Model model) {
        log.info("Viewing supplier in with ID: {}", id);
        
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null) {
                model.addAttribute("supplierIn", supplierIn);
                model.addAttribute("suppliers", supplierRepository.findAll());
                model.addAttribute("products", productRepository.findAll());
                log.debug("Supplier in details loaded for ID: {}", id);
                return "templates_storage/StockInDetail";
            } else {
                log.warn("Supplier in with ID {} not found", id);
                return "redirect:/supplier-ins";
            }
        } catch (Exception e) {
            log.error("Error loading supplier in details for ID {}: {}", id, e.getMessage(), e);
            return "redirect:/supplier-ins";
        }
    }

    /**
     * Create supplier in form submission
     * @param supplierInDTO Supplier in DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier ins page
     */
    @PostMapping
    public String createSupplierIn(@ModelAttribute SupplierInDTO supplierInDTO, 
                                  RedirectAttributes redirectAttributes) {
        log.info("Creating new supplier in");
        
        try {
            SupplierInDTO created = supplierInService.createSupplierIn(supplierInDTO);
            log.debug("Created supplier in with ID: {}", created.getId());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Stock In request created successfully with ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating supplier in: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to create Stock In request: " + e.getMessage());
        }
        
        return "redirect:/supplier-ins";
    }

    /**
     * Update supplier in form submission
     * @param id Supplier in ID
     * @param supplierInDTO Supplier in DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/update")
    public String updateSupplierIn(@PathVariable Long id, 
                                  @ModelAttribute SupplierInDTO supplierInDTO,
                                  RedirectAttributes redirectAttributes) {
        log.info("Updating supplier in with ID: {}", id);
        
        try {
            SupplierInDTO updated = supplierInService.updateSupplierIn(id, supplierInDTO);
            if (updated != null) {
                log.debug("Updated supplier in with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Stock In request updated successfully");
            } else {
                log.warn("Supplier in with ID {} not found for update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Stock In request not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier in with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update Stock In request: " + e.getMessage());
        }
        
        return "redirect:/supplier-ins/" + id;
    }

    /**
     * Update supplier in status
     * @param id Supplier in ID
     * @param status New status
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/status")
    public String updateSupplierInStatus(@PathVariable Long id, 
                                        @RequestParam String status,
                                        RedirectAttributes redirectAttributes) {
        log.info("Updating supplier in status: id={}, status={}", id, status);
        
        try {
            supplierInService.updateSupplierInStatus(id, status);
            
            // If status is COMPLETED, save invoice
            if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
                log.debug("Status is COMPLETED, saving invoice for supplier in with ID: {}", id);
                SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
                if (supplierIn != null) {
                    supplierInInvoiceService.saveInvoice(supplierIn);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Status updated successfully to " + status);
        } catch (Exception e) {
            log.error("Error updating status for supplier in with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update status: " + e.getMessage());
        }
        
        return "redirect:/supplier-ins/" + id;
    }

    /**
     * Delete supplier in
     * @param id Supplier in ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier ins page
     */
    @PostMapping("/{id}/delete")
    public String deleteSupplierIn(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier in with ID: {}", id);
        
        try {
            supplierInService.deleteSupplierIn(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Stock In request deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier in with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete Stock In request: " + e.getMessage());
        }
        
        return "redirect:/supplier-ins";
    }
    
    /**
     * Add item to supplier in
     * @param id Supplier in ID
     * @param itemDTO Item DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/items")
    public String addItemToSupplierIn(
            @PathVariable Long id, 
            @ModelAttribute SupplierRequestItemDTO itemDTO,
            RedirectAttributes redirectAttributes) {
        log.info("Adding item to supplier in with ID: {}", id);
        
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null) {
                supplierIn.getItems().add(itemDTO);
                SupplierInDTO updated = supplierInService.updateSupplierIn(id, supplierIn);
                log.debug("Added item to supplier in with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Item added successfully");
            } else {
                log.warn("Supplier in with ID {} not found for adding item", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Stock In request not found");
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier in with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to add item: " + e.getMessage());
        }
        
        return "redirect:/supplier-ins/" + id;
    }
    
}