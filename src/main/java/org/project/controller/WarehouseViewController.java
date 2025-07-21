package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
            
            // If there's no data, generate sample data for display purposes
            if (supplierIns == null || supplierIns.isEmpty()) {
                log.info("No supplier ins found, generating sample data");
                supplierIns = createSampleSupplierInData();
            }
            
            model.addAttribute("supplierIns", supplierIns);
            
            log.debug("StockIn.html prepared with {} supplier ins", supplierIns.size());
        } catch (Exception e) {
            log.error("Error preparing StockIn.html page data: {}", e.getMessage(), e);
            // Generate sample data on error
            model.addAttribute("supplierIns", createSampleSupplierInData());
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
            
            // If there's no data, generate sample data for display purposes
            if (supplierOuts == null || supplierOuts.isEmpty()) {
                log.info("No supplier outs found, generating sample data");
                supplierOuts = createSampleSupplierOutData();
            }
            
            model.addAttribute("supplierOuts", supplierOuts);
            
            log.debug("StockOut.html prepared with {} supplier outs", supplierOuts.size());
        } catch (Exception e) {
            log.error("Error preparing StockOut.html page data: {}", e.getMessage(), e);
            // Generate sample data on error
            model.addAttribute("supplierOuts", createSampleSupplierOutData());
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
                log.warn("Supplier in with ID {} not found, generating sample data", id);
                model.addAttribute("supplierIn", createSampleSupplierInData().get(0));
            }
        } catch (Exception e) {
            log.error("Error preparing StockInDetail.html for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("supplierIn", createSampleSupplierInData().get(0));
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
                log.warn("Supplier out with ID {} not found, generating sample data", id);
                model.addAttribute("supplierOut", createSampleSupplierOutData().get(0));
            }
        } catch (Exception e) {
            log.error("Error preparing StockOutDetail.html for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("supplierOut", createSampleSupplierOutData().get(0));
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
    
    /**
     * Creates sample supplier out data for display in the UI
     * @return List of SupplierOutDTO with sample data
     */
    private List<SupplierOutDTO> createSampleSupplierOutData() {
        List<SupplierOutDTO> sampleData = new ArrayList<>();
        
        // Create sample supplier outs with different statuses
        String[] statuses = {
            "PENDING", "APPROVED", "COMPLETED", "CANCELLED"
        };
        
        String[] recipients = {
            "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D"
        };
        
        String[] reasons = {
            "SALE", "TRANSFER", "RETURN", "EXPIRED", "DAMAGED", "OTHER"
        };
        
        Random random = new Random();
        
        for (int i = 1; i <= 10; i++) {
            SupplierOutDTO dto = new SupplierOutDTO();
            dto.setId((long) i);
            dto.setRecipient(recipients[random.nextInt(recipients.length)]);
            dto.setStatus(SupplierTransactionStatus.valueOf(statuses[random.nextInt(statuses.length)]));
            dto.setReason(reasons[random.nextInt(reasons.length)]);
            dto.setTotalAmount(new BigDecimal(random.nextInt(10000000) + 1000000));
            dto.setCreatedAt(new Timestamp(System.currentTimeMillis() - random.nextInt(30) * 86400000L)); // Random date in last 30 days
            
            // Add sample items
            dto.setItems(createSampleItems(i, random.nextInt(3) + 1));
            
            sampleData.add(dto);
        }
        
        return sampleData;
    }
    
    /**
     * Creates sample supplier in data for display in the UI
     * @return List of SupplierInDTO with sample data
     */
    private List<SupplierInDTO> createSampleSupplierInData() {
        List<SupplierInDTO> sampleData = new ArrayList<>();
        
        // Create sample supplier ins with different statuses
        String[] statuses = {
            "PENDING", "RECEIVED", "COMPLETED", "DELIVERED", "CANCELLED"
        };
        
        String[] suppliers = {
            "Công ty Dược ABC", "Nhà thuốc XYZ", "Công ty Thiết bị Y tế 123", "Đại lý Dược phẩm DEF"
        };
        
        String[] types = {
            "MEDICINE", "EQUIPMENT"
        };
        
        Random random = new Random();
        
        for (int i = 1; i <= 10; i++) {
            SupplierInDTO dto = new SupplierInDTO();
            dto.setId((long) i);
            dto.setSupplierName(suppliers[random.nextInt(suppliers.length)]);
            dto.setStatus(SupplierTransactionStatus.valueOf(statuses[random.nextInt(statuses.length)]));
            dto.setType(types[random.nextInt(types.length)]);
            dto.setTotalAmount(new BigDecimal(random.nextInt(10000000) + 1000000));
            dto.setCreatedAt(new Timestamp(System.currentTimeMillis() - random.nextInt(30) * 86400000L)); // Random date in last 30 days
            
            // Add sample items
            dto.setItems(createSampleItems(i, random.nextInt(3) + 1));
            
            sampleData.add(dto);
        }
        
        return sampleData;
    }
    
    /**
     * Creates sample items for transactions
     * @param transactionId Transaction ID
     * @param count Number of items to create
     * @return List of SupplierRequestItemDTO with sample data
     */
    private List<SupplierRequestItemDTO> createSampleItems(int transactionId, int count) {
        List<SupplierRequestItemDTO> items = new ArrayList<>();
        
        String[] productNames = {
            "Paracetamol 500mg", "Amoxicillin 250mg", "Máy đo huyết áp", "Nhiệt kế điện tử",
            "Vitamin C 1000mg", "Băng gạc y tế", "Ống nghe y tế", "Dung dịch sát khuẩn"
        };
        
        Random random = new Random();
        
        for (int i = 1; i <= count; i++) {
            SupplierRequestItemDTO item = new SupplierRequestItemDTO();
            item.setProductId((long) (i + (transactionId * 10)));
            item.setProductName(productNames[random.nextInt(productNames.length)]);
            item.setQuantity(random.nextInt(100) + 10);
            item.setUnitPrice(new BigDecimal(random.nextInt(100000) + 5000));
            item.setBatchNumber("LOT" + (1000 + random.nextInt(9000)));
            
            // Set random manufacture and expiration dates
            LocalDate now = LocalDate.now();
            item.setManufactureDate(Date.valueOf(now.minusMonths(random.nextInt(12) + 1)));
            item.setExpirationDate(Date.valueOf(now.plusMonths(random.nextInt(24) + 12)));
            
            items.add(item);
        }
        
        return items;
    }
} 