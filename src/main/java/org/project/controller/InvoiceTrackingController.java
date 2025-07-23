package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.service.SupplierInInvoiceService;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutInvoiceService;
import org.project.service.SupplierOutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for invoice tracking pages
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class InvoiceTrackingController {

    private final SupplierInService supplierInService;
    private final SupplierOutService supplierOutService;
    private final SupplierInInvoiceService supplierInInvoiceService;
    private final SupplierOutInvoiceService supplierOutInvoiceService;

    /**
     * Supplier In tracking page
     * @param id Transaction ID
     * @return View name
     */
    @GetMapping("/invoiceintracking.html")
    public String getSupplierInTracking(@RequestParam Long id, Model model) {
        log.info("Loading supplier in tracking page for ID: {}", id);
        
        try {
            // First try to get from regular transaction
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn != null) {
                model.addAttribute("supplierIn", supplierIn);
                model.addAttribute("pageTitle", "Theo dõi đơn nhập kho");
                return "templates_storage/invoiceintracking";
            }
            
            // If not found, try invoice
            SupplierInvoiceDTO invoice = supplierInInvoiceService.getInvoiceById(id);
            if (invoice != null) {
                model.addAttribute("supplierIn", invoice);
                model.addAttribute("pageTitle", "Theo dõi hóa đơn nhập kho");
                return "templates_storage/invoiceintracking";
            }
            
            // Not found
            model.addAttribute("errorMessage", "Không tìm thấy đơn nhập kho có ID: " + id);
            return "redirect:/supplier-ins";
        } catch (Exception e) {
            log.error("Error loading supplier in tracking for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu theo dõi đơn nhập kho: " + e.getMessage());
            return "redirect:/supplier-ins";
        }
    }

    /**
     * Supplier Out tracking page
     * @param id Transaction ID
     * @return View name
     */
    @GetMapping("/invoiceouttracking.html")
    public String getSupplierOutTracking(@RequestParam Long id, Model model) {
        log.info("Loading supplier out tracking page for ID: {}", id);
        
        try {
            // First try to get from regular transaction
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            
            if (supplierOut != null) {
                model.addAttribute("supplierOut", supplierOut);
                model.addAttribute("pageTitle", "Theo dõi đơn xuất kho");
                return "templates_storage/invoiceouttracking";
            }
            
            // If not found, try invoice
            SupplierInvoiceDTO invoice = supplierOutInvoiceService.getInvoiceById(id);
            if (invoice != null) {
                model.addAttribute("supplierOut", invoice);
                model.addAttribute("pageTitle", "Theo dõi hóa đơn xuất kho");
                return "templates_storage/invoiceouttracking";
            }
            
            // Not found
            model.addAttribute("errorMessage", "Không tìm thấy đơn xuất kho có ID: " + id);
            return "redirect:/supplier-outs";
        } catch (Exception e) {
            log.error("Error loading supplier out tracking for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu theo dõi đơn xuất kho: " + e.getMessage());
            return "redirect:/supplier-outs";
        }
    }
} 