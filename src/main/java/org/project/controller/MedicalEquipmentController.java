package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.MedicalProductEntity;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.ProductType;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierInDTO;
import org.project.repository.MedicalProductRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.MedicalProductService;
import org.project.service.SupplierInInvoiceService;
import org.project.service.SupplierInService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.web.csrf.CsrfToken;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Controller for handling medical equipment operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/warehouse/medical-equipment")
public class MedicalEquipmentController {

    private final MedicalProductService medicalProductService;
    private final MedicalProductRepository medicalProductRepository;
    private final ProductRepository productRepository;
    private final SupplierTransactionRepository supplierTransactionRepository;
    private final SupplierInService supplierInService;
    private final SupplierInInvoiceService supplierInInvoiceService;

    /**
     * Main page displaying all medical equipment
     * @param model Spring MVC Model
     * @return View name for medical equipment page
     */
    @GetMapping
    public String getAllMedicalEquipment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String sortField,
            Model model,
            HttpServletRequest request) {
        log.info("Loading medical equipment page with pagination - page: {}, size: {}", page, size);
        
        try {
            // Get medical products with pagination
            Page<ProductEntity> equipmentPage = productRepository.findByProductType(ProductType.MEDICAL_PRODUCT, PageRequest.of(page, size));
            List<ProductEntity> equipments = equipmentPage.getContent();
            
            // Convert ProductEntity to DTO Map để tránh vòng lặp
            List<Map<String, Object>> equipmentDTOs = equipments.stream().map(e -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", e.getId());
                dto.put("name", e.getName());
                dto.put("unit", e.getUnit());
                dto.put("price", e.getPrice());
                dto.put("stockQuantities", e.getStockQuantities());
                dto.put("description", e.getDescription());
                dto.put("productStatus", e.getProductStatus());
                dto.put("label", e.getLabel());
                dto.put("imageUrl", e.getImageUrl());
                // Lấy category (nếu có)
                if (e.getCategoryEntities() != null && !e.getCategoryEntities().isEmpty()) {
                    dto.put("category", e.getCategoryEntities().iterator().next().getName());
                } else {
                    dto.put("category", null);
                }
                return dto;
            }).collect(Collectors.toList());
            model.addAttribute("equipments", equipmentDTOs);
            
            // Calculate pagination variables
            int totalPages = equipmentPage.getTotalPages();
            int startPage = Math.max(0, page - 2);
            int endPage = Math.min(startPage + 4, totalPages - 1);
            
            log.info("Found {} medical equipment products on page {} of {}", equipments.size(), page + 1, totalPages);
            equipments.forEach(equipment -> {
                log.debug("Equipment: ID={}, Name={}, Category={}, Stock={}", 
                    equipment.getId(), equipment.getName(), equipment.getProductType(), equipment.getStockQuantities());
            });
            
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("equipmentPage", equipmentPage);
            
            // Get stock in orders for medical equipment and convert to simple DTOs to avoid circular references
            List<SupplierTransactionsEntity> stockInOrdersEntities = supplierTransactionRepository
                .findByTransactionTypeAndStatusIn(SupplierTransactionType.STOCK_IN, 
                    List.of(SupplierTransactionStatus.NEED_TO_ADD, SupplierTransactionStatus.WAITING_FOR_DELIVERY, SupplierTransactionStatus.RECEIVED),
                    PageRequest.of(0, 1000)).getContent();
            
            // Convert to simple DTOs to avoid circular references
            List<Map<String, Object>> stockInOrders = stockInOrdersEntities.stream()
                .map(entity -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", entity.getId());
                    dto.put("invoiceNumber", entity.getInvoiceNumber());
                    dto.put("transactionDate", entity.getTransactionDate());
                    dto.put("status", entity.getStatus());
                    dto.put("totalAmount", entity.getTotalAmount());
                    dto.put("notes", entity.getNotes());
                    dto.put("expectedDeliveryDate", entity.getExpectedDeliveryDate());
                    dto.put("date", entity.getTransactionDate() != null ? entity.getTransactionDate().toString() : "");
                    dto.put("total", entity.getTotalAmount() != null ? entity.getTotalAmount() : 0.0);
                    
                    // Add supplier info without circular reference
                    if (entity.getSupplierEntity() != null) {
                        dto.put("supplierName", entity.getSupplierEntity().getName());
                        dto.put("supplierId", entity.getSupplierEntity().getId());
                    }
                    
                    // Add products info
                    if (entity.getSupplierTransactionItemEntities() != null) {
                        List<Map<String, Object>> products = entity.getSupplierTransactionItemEntities().stream()
                            .map(item -> {
                                Map<String, Object> product = new HashMap<>();
                                product.put("id", item.getProductEntity().getId());
                                product.put("name", item.getProductEntity().getName());
                                product.put("quantity", item.getQuantity());
                                product.put("price", item.getUnitPrice());
                                product.put("type", item.getProductEntity().getProductType());
                                return product;
                            })
                            .collect(Collectors.toList());
                        dto.put("products", products);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            model.addAttribute("stockInOrders", stockInOrders);
            
            // Get stock out orders for medical equipment and convert to simple DTOs
            List<SupplierTransactionsEntity> stockOutOrdersEntities = supplierTransactionRepository
                .findByTransactionTypeAndStatusIn(SupplierTransactionType.STOCK_OUT, 
                    List.of(SupplierTransactionStatus.PREPARING, SupplierTransactionStatus.PREPARE_DELIVERY, SupplierTransactionStatus.DELIVERING),
                    PageRequest.of(0, 1000)).getContent();
            
            // Convert to simple DTOs to avoid circular references
            List<Map<String, Object>> stockOutOrders = stockOutOrdersEntities.stream()
                .map(entity -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", entity.getId());
                    dto.put("invoiceNumber", entity.getInvoiceNumber());
                    dto.put("transactionDate", entity.getTransactionDate());
                    dto.put("status", entity.getStatus());
                    dto.put("totalAmount", entity.getTotalAmount());
                    dto.put("notes", entity.getNotes());
                    dto.put("expectedDeliveryDate", entity.getExpectedDeliveryDate());
                    dto.put("recipient", entity.getRecipient());
                    dto.put("stockOutReason", entity.getStockOutReason());
                    dto.put("date", entity.getTransactionDate() != null ? entity.getTransactionDate().toString() : "");
                    dto.put("total", entity.getTotalAmount() != null ? entity.getTotalAmount() : 0.0);
                    dto.put("customerName", entity.getRecipient() != null ? entity.getRecipient() : "Khách hàng");
                    
                    // Add supplier info without circular reference
                    if (entity.getSupplierEntity() != null) {
                        dto.put("supplierName", entity.getSupplierEntity().getName());
                        dto.put("supplierId", entity.getSupplierEntity().getId());
                    }
                    
                    // Add products info
                    if (entity.getSupplierTransactionItemEntities() != null) {
                        List<Map<String, Object>> products = entity.getSupplierTransactionItemEntities().stream()
                            .map(item -> {
                                Map<String, Object> product = new HashMap<>();
                                product.put("id", item.getProductEntity().getId());
                                product.put("name", item.getProductEntity().getName());
                                product.put("quantity", item.getQuantity());
                                product.put("price", item.getUnitPrice());
                                product.put("type", item.getProductEntity().getProductType());
                                return product;
                            })
                            .collect(Collectors.toList());
                        dto.put("products", products);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            model.addAttribute("stockOutOrders", stockOutOrders);
            
            // Add default values for form inputs
            model.addAttribute("supplierId", ""); // Default empty supplier ID
            model.addAttribute("currentUser", getCurrentUser()); // Add current user for forms
            
            // Count statistics
            model.addAttribute("productCount", equipments.size());
            model.addAttribute("stockInCount", stockInOrders.size());
            model.addAttribute("stockOutCount", stockOutOrders.size());
            
            // Thêm truyền _csrf vào model
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            model.addAttribute("_csrf", csrfToken);

            log.debug("Medical equipment page prepared with {} products", equipments.size());
        } catch (Exception e) {
            log.error("Error preparing medical equipment page data: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải dữ liệu thiết bị y tế: " + e.getMessage());
        }
        
        return "templates_storage/medical-equipment";
    }

    /**
     * Get medical equipment details by ID
     * @param id Medical equipment ID
     * @param model Spring MVC Model
     * @return View name for medical equipment details
     */
    @GetMapping("/{id}")
    public String getMedicalEquipmentById(@PathVariable Long id, Model model) {
        log.info("Viewing medical equipment with ID: {}", id);
        
        try {
            ProductEntity equipment = productRepository.findById(id).orElse(null);
            if (equipment != null) {
                model.addAttribute("equipment", equipment);
                log.debug("Medical equipment details loaded for ID: {}", id);
                // Redirect to main equipment page instead of non-existent detail page
                model.addAttribute("successMessage", "Đã tải thông tin thiết bị y tế: " + equipment.getName());
                return "redirect:/warehouse/medical-equipment";
            } else {
                log.warn("Medical equipment with ID {} not found", id);
                model.addAttribute("errorMessage", "Không tìm thấy thiết bị y tế với ID: " + id);
                return "redirect:/warehouse/medical-equipment";
            }
        } catch (Exception e) {
            log.error("Error loading medical equipment details for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải thông tin thiết bị y tế: " + e.getMessage());
            return "redirect:/warehouse/medical-equipment";
        }
    }

    /**
     * Create new medical equipment
     * @param equipment Medical equipment data from form
     * @param model Spring MVC Model
     * @return Redirect to medical equipment page
     */
    @PostMapping
    public String createMedicalEquipment(@ModelAttribute ProductEntity equipment, Model model) {
        log.info("Creating new medical equipment");
        
        try {
            // Set type to EQUIPMENT/MEDICAL_PRODUCT
            equipment.setProductType(ProductType.MEDICAL_PRODUCT);
            ProductEntity created = productRepository.save(equipment);
            log.debug("Created medical equipment with ID: {}", created.getId());
            model.addAttribute("successMessage", "Thiết bị y tế đã được tạo thành công với ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating medical equipment: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tạo thiết bị y tế: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medical-equipment";
    }

    /**
     * Update medical equipment
     * @param id Medical equipment ID
     * @param equipment Medical equipment data from form
     * @param model Spring MVC Model
     * @return Redirect to medical equipment page
     */
    @PostMapping("/{id}/update")
    public String updateMedicalEquipment(@PathVariable Long id, @ModelAttribute ProductEntity equipment, Model model) {
        log.info("Updating medical equipment with ID: {}", id);
        
        try {
            ProductEntity existing = productRepository.findById(id).orElse(null);
            if (existing != null) {
                // Update fields
                existing.setName(equipment.getName());
                existing.setDescription(equipment.getDescription());
                existing.setPrice(equipment.getPrice());
                existing.setStockQuantities(equipment.getStockQuantities());
                existing.setUnit(equipment.getUnit());
                
                productRepository.save(existing);
                log.debug("Updated medical equipment with ID: {}", id);
                model.addAttribute("successMessage", "Thiết bị y tế đã được cập nhật thành công");
            } else {
                log.warn("Medical equipment with ID {} not found for update", id);
                model.addAttribute("errorMessage", "Không tìm thấy thiết bị y tế với ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error updating medical equipment with ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi cập nhật thiết bị y tế: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medical-equipment";
    }

    /**
     * Delete medical equipment
     * @param id Medical equipment ID
     * @param model Spring MVC Model
     * @return Redirect to medical equipment page
     */
    @PostMapping("/{id}/delete")
    public String deleteMedicalEquipment(@PathVariable Long id, Model model) {
        log.info("Deleting medical equipment with ID: {}", id);
        
        try {
            productRepository.deleteById(id);
            log.debug("Deleted medical equipment with ID: {}", id);
            model.addAttribute("successMessage", "Thiết bị y tế đã được xóa thành công");
        } catch (Exception e) {
            log.error("Error deleting medical equipment with ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi xóa thiết bị y tế: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medical-equipment";
    }

    /**
     * Process stock in for medical equipment
     * @param id Stock in ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to medical equipment page
     */
    @PostMapping("/process-stockin/{id}")
    public String processStockIn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Processing stock in with ID: {}", id);
            
            // Get supplier in details
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn != null && supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED) {
                // Update stock quantities for each product
                if (supplierIn.getItems() != null) {
                    for (var item : supplierIn.getItems()) {
                        ProductEntity product = productRepository.findById(item.getProductId()).orElse(null);
                        if (product != null) {
                            // Update stock quantity
                            int currentStock = product.getStockQuantities() != null ? product.getStockQuantities() : 0;
                            product.setStockQuantities(currentStock + item.getQuantity());
                            productRepository.save(product);
                            log.debug("Updated stock for product {}: {} + {} = {}", 
                                     product.getId(), currentStock, item.getQuantity(), product.getStockQuantities());
                        }
                    }
                }
                
                // Update order status to COMPLETED
                supplierInService.updateSupplierInStatus(id, "COMPLETED");
                
                // Move order to StockInInvoice
                supplierInInvoiceService.saveInvoice(supplierIn);
                
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Đã nhập kho thành công đơn hàng #" + id);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Đơn hàng không tồn tại hoặc không ở trạng thái đã kiểm tra");
            }
        } catch (Exception e) {
            log.error("Error processing stock in with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi khi xử lý nhập kho: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medical-equipment";
    }
    
    // API endpoint để xử lý đơn nhập kho (stock-in)
    @PostMapping("/api/stock-in/{id}/process")
    @ResponseBody
    public Map<String, Object> processStockInOrder(@PathVariable Long id) {
        log.info("Processing stock-in order for medical equipment with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            
            if (order.getTransactionType() != SupplierTransactionType.STOCK_IN) {
                response.put("success", false);
                response.put("message", "Invalid order type");
                return response;
            }
            
            if (order.getStatus() != SupplierTransactionStatus.NEED_TO_ADD) {
                response.put("success", false);
                response.put("message", "Order is not in correct status for processing");
                return response;
            }
            
            // Chuyển trạng thái thành COMPLETED
            System.out.println("Changing order status from " + order.getStatus() + " to COMPLETED");
            order.setStatus(SupplierTransactionStatus.COMPLETED);
            SupplierTransactionsEntity savedOrder = supplierTransactionRepository.save(order);
            System.out.println("Order saved with new status: " + savedOrder.getStatus());
            
            response.put("success", true);
            response.put("message", "Order processed successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error processing stock-in order: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error processing order: " + e.getMessage());
            return response;
        }
    }
    
    // API endpoint để từ chối đơn nhập kho (stock-in)
    @PostMapping("/api/stock-in/{id}/reject")
    @ResponseBody
    public Map<String, Object> rejectStockInOrder(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        log.info("Rejecting stock-in order for medical equipment with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            
            if (order.getTransactionType() != SupplierTransactionType.STOCK_IN) {
                response.put("success", false);
                response.put("message", "Invalid order type");
                return response;
            }
            
            if (order.getStatus() != SupplierTransactionStatus.NEED_TO_ADD) {
                response.put("success", false);
                response.put("message", "Order is not in correct status for rejection");
                return response;
            }
            
            String reason = requestBody.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Rejection reason is required");
                return response;
            }
            
            // Chuyển trạng thái thành REJECTED và lưu lý do
            order.setStatus(SupplierTransactionStatus.REJECTED);
            order.setNotes("Lý do từ chối: " + reason);
            supplierTransactionRepository.save(order);
            
            response.put("success", true);
            response.put("message", "Order rejected successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error rejecting stock-in order: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error rejecting order: " + e.getMessage());
            return response;
        }
    }
    
    // API endpoint để chuẩn bị đơn xuất kho (stock-out)
    @PostMapping("/api/stock-out/{id}/prepare")
    @ResponseBody
    public Map<String, Object> prepareStockOutOrder(@PathVariable Long id) {
        log.info("Preparing stock-out order for medical equipment with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            
            if (order.getTransactionType() != SupplierTransactionType.STOCK_OUT) {
                response.put("success", false);
                response.put("message", "Invalid order type");
                return response;
            }
            
            if (order.getStatus() != SupplierTransactionStatus.PREPARING) {
                response.put("success", false);
                response.put("message", "Order is not in correct status for preparation");
                return response;
            }
            
            // Chuyển trạng thái thành PREPARE_DELIVERY
            System.out.println("Changing order status from " + order.getStatus() + " to PREPARE_DELIVERY");
            order.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
            SupplierTransactionsEntity savedOrder = supplierTransactionRepository.save(order);
            System.out.println("Order saved with new status: " + savedOrder.getStatus());
            
            response.put("success", true);
            response.put("message", "Order prepared successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error preparing stock-out order: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error preparing order: " + e.getMessage());
            return response;
        }
    }
    
    // API endpoint để từ chối đơn xuất kho (stock-out)
    @PostMapping("/api/stock-out/{id}/reject")
    @ResponseBody
    public Map<String, Object> rejectStockOutOrder(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        log.info("Rejecting stock-out order for medical equipment with ID: {}", id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            
            if (order.getTransactionType() != SupplierTransactionType.STOCK_OUT) {
                response.put("success", false);
                response.put("message", "Invalid order type");
                return response;
            }
            
            if (order.getStatus() != SupplierTransactionStatus.PREPARING) {
                response.put("success", false);
                response.put("message", "Order is not in correct status for rejection");
                return response;
            }
            
            String reason = requestBody.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Rejection reason is required");
                return response;
            }
            
            // Chuyển trạng thái thành REJECTED và lưu lý do
            order.setStatus(SupplierTransactionStatus.REJECTED);
            order.setNotes("Lý do từ chối: " + reason);
            supplierTransactionRepository.save(order);
            
            response.put("success", true);
            response.put("message", "Order rejected successfully");
            return response;
            
        } catch (Exception e) {
            log.error("Error rejecting stock-out order: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error rejecting order: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Get current user - placeholder method
     * @return Current user object or null
     */
    private java.util.Map<String, Object> getCurrentUser() {
        // TODO: Implement proper user authentication
        // For now, return a simple map with required properties
        java.util.Map<String, Object> user = new java.util.HashMap<>();
        user.put("id", 256L);
        user.put("fullName", "Người dùng");
        user.put("roleName", "STAFF");
        return user;
    }
}