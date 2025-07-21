package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

/**
 * Controller for handling direct template navigation requests
 * for warehouse-related pages
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class WarehouseViewController {

    private final SupplierInService supplierInService;
    private final SupplierOutService supplierOutService;

    /**
     * Direct access to StockIn.html page
     * @param model Spring MVC Model
     * @return View name for stock in page
     */
    @GetMapping("/StockIn.html")
    public String stockInPage(Model model) {
        log.info("Direct navigation to StockIn.html");
        
        try {
            // Get all supplier ins
            List<SupplierInDTO> supplierIns = supplierInService.getAllSupplierIns();
            
            // Add data to model
            model.addAttribute("supplierIns", supplierIns != null ? supplierIns : Collections.emptyList());
            
            // Add pagination attributes
            int totalItems = supplierIns != null ? supplierIns.size() : 0;
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalItems > 0 ? 1 : 0);
            model.addAttribute("pageSize", 10);
            model.addAttribute("totalItems", totalItems);
            
            log.debug("StockIn.html prepared with {} supplier ins", totalItems);
        } catch (Exception e) {
            log.error("Error preparing StockIn.html page data: {}", e.getMessage(), e);
            // Set empty list and pagination attributes
            model.addAttribute("supplierIns", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", 10);
            model.addAttribute("totalItems", 0);
            model.addAttribute("errorMessage", "Không thể tải dữ liệu từ hệ thống: " + e.getMessage());
        }
        
        return "templates_storage/StockIn";
    }

    /**
     * Direct access to StockOut.html page
     * @param model Spring MVC Model
     * @return View name for stock out page
     */
    @GetMapping("/StockOut.html")
    public String stockOutPage(Model model) {
        log.info("Direct navigation to StockOut.html");
        
        try {
            // Get all supplier outs
            List<SupplierOutDTO> supplierOuts = supplierOutService.getAllSupplierOuts();
            
            // Add data to model
            model.addAttribute("supplierOuts", supplierOuts != null ? supplierOuts : Collections.emptyList());
            
            // Add pagination attributes
            int totalItems = supplierOuts != null ? supplierOuts.size() : 0;
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", totalItems > 0 ? 1 : 0);
            model.addAttribute("pageSize", 10);
            model.addAttribute("totalItems", totalItems);
            
            log.debug("StockOut.html prepared with {} supplier outs", totalItems);
        } catch (Exception e) {
            log.error("Error preparing StockOut.html page data: {}", e.getMessage(), e);
            // Set empty list and pagination attributes
            model.addAttribute("supplierOuts", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("pageSize", 10);
            model.addAttribute("totalItems", 0);
            model.addAttribute("errorMessage", "Không thể tải dữ liệu từ hệ thống: " + e.getMessage());
        }
        
        return "templates_storage/StockOut";
    }
    
    /**
     * Direct access to StockInDetail.html page
     * @param id Supplier in ID from request parameter
     * @param model Spring MVC Model
     * @return View name for stock in detail page
     */
    @GetMapping("/StockInDetail.html")
    public String stockInDetailPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access StockInDetail.html without an ID");
            return "redirect:/StockIn.html";
        }
        
        log.info("Direct navigation to StockInDetail.html for ID: {}", id);
        
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null) {
                model.addAttribute("supplierIn", supplierIn);
                log.debug("StockInDetail.html prepared for ID: {}", id);
            } else {
                log.warn("Supplier in with ID {} not found", id);
                model.addAttribute("errorMessage", "Không tìm thấy đơn nhập kho với ID: " + id);
                return "redirect:/StockIn.html";
            }
        } catch (Exception e) {
            log.error("Error preparing StockInDetail.html for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải thông tin đơn nhập kho: " + e.getMessage());
            return "redirect:/StockIn.html";
        }
        
        return "templates_storage/StockInDetail";
    }
    
    /**
     * Direct access to StockOutDetail.html page
     * @param id Supplier out ID from request parameter
     * @param model Spring MVC Model
     * @return View name for stock out detail page
     */
    @GetMapping("/StockOutDetail.html")
    public String stockOutDetailPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access StockOutDetail.html without an ID");
            return "redirect:/StockOut.html";
        }
        
        log.info("Direct navigation to StockOutDetail.html for ID: {}", id);
        
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                model.addAttribute("supplierOut", supplierOut);
                log.debug("StockOutDetail.html prepared for ID: {}", id);
            } else {
                log.warn("Supplier out with ID {} not found", id);
                model.addAttribute("errorMessage", "Không tìm thấy đơn xuất kho với ID: " + id);
                return "redirect:/StockOut.html";
            }
        } catch (Exception e) {
            log.error("Error preparing StockOutDetail.html for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải thông tin đơn xuất kho: " + e.getMessage());
            return "redirect:/StockOut.html";
        }
        
        return "templates_storage/StockOutDetail";
    }
    
    /**
     * Direct access to invoiceintracking.html page
     * @param id Supplier in invoice ID from request parameter
     * @param model Spring MVC Model
     * @return View name for invoice tracking page
     */
    @GetMapping("/invoiceintracking.html")
    public String invoiceInTrackingPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access invoiceintracking.html without an ID");
            return "redirect:/StockIn.html";
        }
        
        log.info("Direct navigation to invoiceintracking.html for ID: {}", id);
        model.addAttribute("invoiceId", id);
        
        return "templates_storage/invoiceintracking";
    }
    
    /**
     * Direct access to invoiceouttracking.html page
     * @param id Supplier out invoice ID from request parameter
     * @param model Spring MVC Model
     * @return View name for invoice tracking page
     */
    @GetMapping("/invoiceouttracking.html")
    public String invoiceOutTrackingPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access invoiceouttracking.html without an ID");
            return "redirect:/StockOut.html";
        }
        
        log.info("Direct navigation to invoiceouttracking.html for ID: {}", id);
        model.addAttribute("invoiceId", id);
        
        return "templates_storage/invoiceouttracking";
    }
    
    /**
     * Direct access to StockInInvoice.html page
     * @param model Spring MVC Model
     * @return View name for stock in invoice page
     */
    @GetMapping("/StockInInvoice.html")
    public String stockInInvoicePage(Model model) {
        log.info("Direct navigation to StockInInvoice.html");
        return "templates_storage/StockInInvoice";
    }
    
    /**
     * Direct access to StockOutInvoice.html page
     * @param model Spring MVC Model
     * @return View name for stock out invoice page
     */
    @GetMapping("/StockOutInvoice.html")
    public String stockOutInvoicePage(Model model) {
        log.info("Direct navigation to StockOutInvoice.html");
        return "templates_storage/StockOutInvoice";
    }
    
    /**
     * Direct access to CustomerInvoice.html page
     * @param model Spring MVC Model
     * @return View name for customer invoice page
     */
    @GetMapping("/CustomerInvoice.html")
    public String customerInvoicePage(Model model) {
        log.info("Direct navigation to CustomerInvoice.html");
        return "templates_storage/CustomerInvoice";
    }
    
    /**
     * Direct access to medicine.html page
     * @param model Spring MVC Model
     * @return View name for medicine page
     */
    @GetMapping("/medicine.html")
    public String medicinePage(Model model) {
        log.info("Direct navigation to medicine.html");
        return "templates_storage/medicine";
    }
    
    /**
     * Direct access to medical-equipment.html page
     * @param model Spring MVC Model
     * @return View name for medical equipment page
     */
    @GetMapping("/medical-equipment.html")
    public String medicalEquipmentPage(Model model) {
        log.info("Direct navigation to medical-equipment.html");
        return "templates_storage/medical-equipment";
    }
    
    /**
     * Direct access to index.html page
     * @param model Spring MVC Model
     * @return View name for dashboard page
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        log.info("Direct navigation to index.html or root path");
        return "templates_storage/index";
    }
    
    /**
     * Direct access to order-list.html page
     * @param model Spring MVC Model
     * @return View name for order list page
     */
    @GetMapping("/order-list.html")
    public String orderListPage(Model model) {
        log.info("Direct navigation to order-list.html");
        return "templates_storage/order-list";
    }
    
    /**
     * Direct access to order-detail.html page
     * @param id Order ID from request parameter
     * @param model Spring MVC Model
     * @return View name for order detail page
     */
    @GetMapping("/order-detail.html")
    public String orderDetailPage(Long id, Model model) {
        log.info("Direct navigation to order-detail.html for ID: {}", id);
        model.addAttribute("orderId", id);
        return "templates_storage/order-detail";
    }
    
    /**
     * Direct access to order-tracking.html page
     * @param id Order ID from request parameter
     * @param model Spring MVC Model
     * @return View name for order tracking page
     */
    @GetMapping("/order-tracking.html")
    public String orderTrackingPage(Long id, Model model) {
        log.info("Direct navigation to order-tracking.html for ID: {}", id);
        model.addAttribute("orderId", id);
        return "templates_storage/order-tracking";
    }
    
    /**
     * Direct access to coupon-list.html page
     * @param model Spring MVC Model
     * @return View name for coupon list page
     */
    @GetMapping("/coupon-list.html")
    public String couponListPage(Model model) {
        log.info("Direct navigation to coupon-list.html");
        return "templates_storage/coupon-list";
    }
    
    /**
     * Direct access to create-coupon.html page
     * @param model Spring MVC Model
     * @return View name for create coupon page
     */
    @GetMapping("/create-coupon.html")
    public String createCouponPage(Model model) {
        log.info("Direct navigation to create-coupon.html");
        return "templates_storage/create-coupon";
    }
    
    /**
     * Direct access to product-review.html page
     * @param model Spring MVC Model
     * @return View name for product review page
     */
    @GetMapping("/product-review.html")
    public String productReviewPage(Model model) {
        log.info("Direct navigation to product-review.html");
        return "templates_storage/product-review";
    }
    
    /**
     * Direct access to reports.html page
     * @param model Spring MVC Model
     * @return View name for reports page
     */
    @GetMapping("/reports.html")
    public String reportsPage(Model model) {
        log.info("Direct navigation to reports.html");
        return "templates_storage/reports";
    }
} 