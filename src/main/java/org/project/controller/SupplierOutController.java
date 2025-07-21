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

    /**
     * Main stock out page displaying all supplier outs
     * @param model Spring MVC Model
     * @return View name for stock out page
     */
    @GetMapping
    public String getAllSupplierOuts(Model model) {
        log.info("Loading stock out page");
        
        try {
            // Get all supplier outs
            List<SupplierOutDTO> supplierOuts = supplierOutService.getAllSupplierOuts();
            model.addAttribute("supplierOuts", supplierOuts);
            
            // Add suppliers and products for form dropdowns
            model.addAttribute("suppliers", supplierRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            
            log.debug("Stock out page prepared with {} supplier outs", supplierOuts.size());
        } catch (Exception e) {
            log.error("Error preparing stock out page data: {}", e.getMessage(), e);
            // Add empty lists on error to avoid template errors
            model.addAttribute("supplierOuts", Collections.emptyList());
            model.addAttribute("suppliers", Collections.emptyList());
            model.addAttribute("products", Collections.emptyList());
        }
        
        return "templates_storage/StockOut";
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
                return "templates_storage/StockOutDetail";
            } else {
                log.warn("Supplier out with ID {} not found", id);
                return "redirect:/supplier-outs";
            }
        } catch (Exception e) {
            log.error("Error loading supplier out details for ID {}: {}", id, e.getMessage(), e);
            return "redirect:/supplier-outs";
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
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Stock Out request created successfully with ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating supplier out: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to create Stock Out request: " + e.getMessage());
        }
        
        return "redirect:/supplier-outs";
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
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Stock Out request updated successfully");
            } else {
                log.warn("Supplier out with ID {} not found for update", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Stock Out request not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier out with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update Stock Out request: " + e.getMessage());
        }
        
        return "redirect:/supplier-outs/" + id;
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
                log.debug("Status is COMPLETED, saving invoice for supplier out with ID: {}", id);
                SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
                if (supplierOut != null) {
                    supplierOutInvoiceService.saveInvoice(supplierOut);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Status updated successfully to " + status);
        } catch (Exception e) {
            log.error("Error updating status for supplier out with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to update status: " + e.getMessage());
        }
        
        return "redirect:/supplier-outs/" + id;
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
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Stock Out request deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier out with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to delete Stock Out request: " + e.getMessage());
        }
        
        return "redirect:/supplier-outs";
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
                SupplierOutDTO updated = supplierOutService.updateSupplierOut(id, supplierOut);
                log.debug("Added item to supplier out with ID: {}", id);
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Item added successfully");
            } else {
                log.warn("Supplier out with ID {} not found for adding item", id);
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Stock Out request not found");
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier out with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to add item: " + e.getMessage());
        }
        
        return "redirect:/supplier-outs/" + id;
    }
    
    /**
     * Test method to display sample data in the StockOut page
     * @param model Spring MVC Model
     * @return View name for stock out page with sample data
     */
    @GetMapping("/test")
    public String testStockOutPage(Model model) {
        log.info("Loading test stock out page with sample data");
        
        try {
            // Create sample data directly instead of using the service
            List<SupplierOutDTO> sampleSupplierOuts = createSampleSupplierOutData();
            
            model.addAttribute("supplierOuts", sampleSupplierOuts);
            
            // Add suppliers and products for form dropdowns
            List<SupplierEntity> suppliers = supplierRepository.findAll();
            List<ProductEntity> products = productRepository.findAll();
            
            // If no suppliers or products exist, create sample ones
            if (suppliers.isEmpty()) {
                suppliers = Collections.singletonList(createSampleSupplier());
            }
            
            if (products.isEmpty()) {
                products = createSampleProducts();
            }
            
            model.addAttribute("suppliers", suppliers);
            model.addAttribute("products", products);
            
            log.debug("Test stock out page prepared with {} sample supplier outs", sampleSupplierOuts.size());
        } catch (Exception e) {
            log.error("Error preparing test stock out page data: {}", e.getMessage(), e);
            // Add empty lists on error to avoid template errors
            model.addAttribute("supplierOuts", Collections.emptyList());
            model.addAttribute("suppliers", Collections.emptyList());
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu mẫu: " + e.getMessage());
        }
        
        return "templates_storage/StockOut";
    }
    
    /**
     * Creates sample supplier out data for display
     * @return List of sample supplier out DTOs
     */
    private List<SupplierOutDTO> createSampleSupplierOutData() {
        List<SupplierOutDTO> sampleData = new ArrayList<>();
        
        // Create sample data with different statuses
        SupplierTransactionStatus[] statuses = SupplierTransactionStatus.values();
        
        for (int i = 1; i <= 5; i++) {
            SupplierOutDTO dto = new SupplierOutDTO();
            dto.setId((long) i);
            dto.setSupplierId((long) (i % 3 + 1));
            dto.setSupplierName("Nhà cung cấp " + (i % 3 + 1));
            dto.setSupplierContact("0987654" + (310 + i));
            dto.setInventoryManagerId(1L);
            dto.setInventoryManagerName("Quản lý kho");
            dto.setTotalAmount(new BigDecimal("" + (800 * i + 300)));
            
            // Set different dates for each transaction
            LocalDateTime transactionDate = LocalDateTime.now().minusDays(i);
            dto.setTransactionDate(Timestamp.valueOf(transactionDate));
            
            // Set different statuses
            SupplierTransactionStatus status = statuses[i % statuses.length];
            dto.setStatus(status);
            
            if (status != SupplierTransactionStatus.PENDING) {
                dto.setApprovedDate(Timestamp.valueOf(transactionDate.plusDays(1)));
                dto.setApprovedById(2L);
                dto.setApprovedByName("Người phê duyệt");
            }
            
            dto.setNotes("Ghi chú cho đơn xuất kho #" + i);
            dto.setExpectedDeliveryDate(Timestamp.valueOf(transactionDate.plusDays(2)));
            
            if (status == SupplierTransactionStatus.COMPLETED) {
                dto.setInvoiceNumber("OUT-" + (1000 + i));
                dto.setTaxAmount(new BigDecimal("" + (i * 40)));
                dto.setShippingCost(new BigDecimal("" + (i * 15)));
                dto.setPaymentMethod("Tiền mặt");
                dto.setDueDate(Timestamp.valueOf(transactionDate.plusDays(10)));
                dto.setPaymentDate(Timestamp.valueOf(transactionDate.plusDays(8)));
            }
            
            // Add sample items
            List<SupplierRequestItemDTO> items = createSampleItems(i, 1 + i % 4);
            dto.setItems(items);
            
            sampleData.add(dto);
        }
        
        return sampleData;
    }
    
    /**
     * Creates sample items for a supplier transaction
     * @param transactionId The transaction ID
     * @param count Number of items to create
     * @return List of sample items
     */
    private List<SupplierRequestItemDTO> createSampleItems(int transactionId, int count) {
        List<SupplierRequestItemDTO> items = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 1; i <= count; i++) {
            SupplierRequestItemDTO item = new SupplierRequestItemDTO();
            item.setProductId((long) (i % 5 + 1));
            item.setProductName("Sản phẩm " + (i % 5 + 1));
            item.setQuantity(5 * (i + 2));
            item.setUnitPrice(new BigDecimal("" + (60 + random.nextInt(40))));
            
            // Set manufacture date to 6 months ago
            LocalDate manufactureDate = LocalDate.now().minusMonths(6);
            item.setManufactureDate(Date.valueOf(manufactureDate));
            
            // Set expiration date to 1-2 years in the future
            LocalDate expirationDate = LocalDate.now().plusYears(1).plusMonths(random.nextInt(12));
            item.setExpirationDate(Date.valueOf(expirationDate));
            
            item.setBatchNumber("OUT-BATCH-" + transactionId + "-" + i);
            item.setStorageLocation("Kho " + (char)('A' + random.nextInt(3)) + "-" + (random.nextInt(9) + 1));
            item.setNotes("Ghi chú cho sản phẩm xuất kho " + i);
            
            items.add(item);
        }
        
        return items;
    }
    
    /**
     * Creates a sample supplier if none exists
     * @return Sample supplier entity
     */
    private SupplierEntity createSampleSupplier() {
        SupplierEntity supplier = new SupplierEntity();
        supplier.setId(1L);
        supplier.setName("Nhà cung cấp mẫu");
        supplier.setEmail("supplier@example.com");
        supplier.setPhoneNumber("0987654321");
        return supplier;
    }
    
    /**
     * Creates sample products if none exist
     * @return List of sample product entities
     */
    private List<ProductEntity> createSampleProducts() {
        List<ProductEntity> products = new ArrayList<>();
        
        String[] productNames = {
            "Paracetamol", "Amoxicillin", "Omeprazole", 
            "Ibuprofen", "Cetirizine"
        };
        
        for (int i = 0; i < productNames.length; i++) {
            ProductEntity product = new ProductEntity();
            product.setId((long) (i + 1));
            product.setName(productNames[i]);
            product.setDescription("Mô tả cho " + productNames[i]);
            product.setStockQuantities(100 + i * 20);
            product.setPrice(new BigDecimal("" + (50 + i * 10)));
            product.setUnit("Viên");
            products.add(product);
        }
        
        return products;
    }
}