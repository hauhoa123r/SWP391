package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
     * Direct access to StockIn.html page - redirects to supplier-ins controller
     * @param model Spring MVC Model
     * @return Redirect to supplier-ins controller
     */
    @GetMapping("/StockIn.html")
    public String stockInPage(Model model) {
        log.info("Redirecting from StockIn.html to /supplier-ins");
        return "redirect:/supplier-ins";
    }

    /**
     * Direct access to StockOut.html page - redirects to supplier-outs controller
     * @param model Spring MVC Model
     * @return Redirect to supplier-outs controller
     */
    @GetMapping("/StockOut.html")
    public String stockOutPage(Model model) {
        log.info("Redirecting from StockOut.html to /supplier-outs");
        return "redirect:/supplier-outs";
    }
    
    /**
     * Direct access to StockInDetail.html page - redirects to supplier-ins controller
     * @param id Supplier in ID from request parameter
     * @param model Spring MVC Model
     * @return Redirect to supplier-ins controller
     */
    @GetMapping("/StockInDetail.html")
    public String stockInDetailPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access StockInDetail.html without an ID");
            return "redirect:/supplier-ins";
        }
        
        log.info("Redirecting from StockInDetail.html for ID: {} to /supplier-ins/{}", id, id);
        return "redirect:/supplier-ins/" + id;
    }
    
    /**
     * Direct access to StockOutDetail.html page - redirects to supplier-outs controller
     * @param id Supplier out ID from request parameter
     * @param model Spring MVC Model
     * @return Redirect to supplier-outs controller
     */
    @GetMapping("/StockOutDetail.html")
    public String stockOutDetailPage(Long id, Model model) {
        if (id == null) {
            log.warn("Attempted to access StockOutDetail.html without an ID");
            return "redirect:/supplier-outs";
        }
        
        log.info("Redirecting from StockOutDetail.html for ID: {} to /supplier-outs/{}", id, id);
        return "redirect:/supplier-outs/" + id;
    }
    
    /**
     * Direct access to StockInInvoice.html page - redirects to supplier-in-invoices controller
     * @param model Spring MVC Model
     * @return Redirect to supplier-in-invoices controller
     */
    @GetMapping("/StockInInvoice.html")
    public String stockInInvoicePage(Model model) {
        log.info("Redirecting from StockInInvoice.html to /supplier-in-invoices");
        return "redirect:/supplier-in-invoices";
    }
    
    /**
     * Direct access to StockOutInvoice.html page - redirects to supplier-out-invoices controller
     * @param model Spring MVC Model
     * @return Redirect to supplier-out-invoices controller
     */
    @GetMapping("/StockOutInvoice.html")
    public String stockOutInvoicePage(Model model) {
        log.info("Redirecting from StockOutInvoice.html to /supplier-out-invoices");
        return "redirect:/supplier-out-invoices";
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
     * Direct access to warehouse dashboard page
     * @param model Spring MVC Model
     * @return View name for warehouse dashboard page
     */
    @GetMapping("/dashboard/warehouse")
    public String indexPage(Model model) {
        log.info("Direct navigation to warehouse dashboard page");
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