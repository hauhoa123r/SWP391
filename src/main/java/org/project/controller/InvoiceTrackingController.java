package org.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for invoice tracking pages
 */
@Slf4j
@Controller
public class InvoiceTrackingController {

    @Autowired
    private SupplierInService supplierInService;
    
    @Autowired
    private SupplierOutService supplierOutService;

    /**
     * Supplier In tracking page
     * @param id Transaction ID
     * @return View name
     */
    @GetMapping("/warehouse/invoice/in/tracking")
    public String getSupplierInTracking(@RequestParam Long id, Model model) {
        log.info("Loading supplier in tracking page for ID: {}", id);
        
        try {
            // Get transaction data
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn != null) {
                model.addAttribute("supplierIn", supplierIn);
                model.addAttribute("pageTitle", "Theo dõi đơn nhập kho");
                
                // Add current user for forms
                model.addAttribute("currentUser", getCurrentUser());
                
                return "templates_storage/invoiceintracking";
            }
            
            // Not found
            model.addAttribute("errorMessage", "Không tìm thấy đơn nhập kho có ID: " + id);
            return "redirect:/warehouse/stock-in";
        } catch (Exception e) {
            log.error("Error loading supplier in tracking for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu theo dõi đơn nhập kho: " + e.getMessage());
            return "redirect:/warehouse/stock-in";
        }
    }

    /**
     * Supplier Out tracking page
     * @param id Transaction ID
     * @return View name
     */
    @GetMapping("/warehouse/invoice/out/tracking")
    public String getSupplierOutTracking(@RequestParam Long id, Model model) {
        log.info("Loading supplier out tracking page for ID: {}", id);
        
        try {
            // Get transaction data
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            
            if (supplierOut != null) {
                model.addAttribute("supplierOut", supplierOut);
                model.addAttribute("pageTitle", "Theo dõi đơn xuất kho");
                
                // Add current user for forms
                model.addAttribute("currentUser", getCurrentUser());
                
                return "templates_storage/invoiceouttracking";
            }
            
            // Not found
            model.addAttribute("errorMessage", "Không tìm thấy đơn xuất kho có ID: " + id);
            return "redirect:/warehouse/stock-out";
        } catch (Exception e) {
            log.error("Error loading supplier out tracking for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu theo dõi đơn xuất kho: " + e.getMessage());
            return "redirect:/warehouse/stock-out";
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