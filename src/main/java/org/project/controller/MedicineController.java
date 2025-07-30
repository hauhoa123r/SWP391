package org.project.controller;

import org.project.model.dto.MedicineDTO;
import org.project.model.dto.SupplierInDTO;
import org.project.service.MedicineService;
import org.project.service.SupplierInService;
import org.project.enums.operation.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.project.enums.SupplierTransactionStatus;
import org.project.entity.ProductEntity;
import org.project.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.project.service.SupplierInInvoiceService;
import org.project.repository.SupplierTransactionRepository;
import org.project.enums.SupplierTransactionType;
import org.project.entity.SupplierTransactionsEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/warehouse/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private SupplierInService supplierInService;
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierTransactionRepository supplierTransactionRepository;
    
    @Autowired
    private SupplierInInvoiceService supplierInInvoiceService;

    @GetMapping
    public String getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) SortDirection sortDirection,
            @RequestParam(required = false) String sortField,
            Model model) {
        try {
            Page<MedicineDTO> medicinePage = medicineService.getAllMedicines(page, size, name, sortDirection, sortField);
            int totalPages = medicinePage.getTotalPages();
            int startPage = Math.max(0, page - 2);
            int endPage = Math.min(startPage + 4, totalPages - 1);
            model.addAttribute("medicines", medicinePage.getContent());
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", medicinePage.getTotalPages());
            model.addAttribute("newMedicine", new MedicineDTO());
            model.addAttribute("supplierIn", new SupplierInDTO());
            //add medicine page for paging
            model.addAttribute("medicinePage", medicinePage);
        
        // Get stock in orders for medicine and convert to simple DTOs to avoid circular references
        List<SupplierTransactionsEntity> stockInOrdersEntities = supplierTransactionRepository
            .findByTransactionTypeAndStatusIn(SupplierTransactionType.STOCK_IN, 
                List.of(SupplierTransactionStatus.NEED_TO_ADD),
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
                // Add supplier info without circular reference
                if (entity.getSupplierEntity() != null) {
                    dto.put("supplierName", entity.getSupplierEntity().getName());
                    dto.put("supplierId", entity.getSupplierEntity().getId());
                }
                // Add products info
                if (entity.getSupplierTransactionItemEntities() != null) {
                    List<Map<String, Object>> products = entity.getSupplierTransactionItemEntities().stream()
                        .map(item -> {
                            Map<String, Object> productDto = new HashMap<>();
                            if (item.getProductEntity() != null) {
                                productDto.put("id", item.getProductEntity().getId());
                                productDto.put("name", item.getProductEntity().getName());
                                productDto.put("type", item.getProductEntity().getProductType());
                            }
                            productDto.put("quantity", item.getQuantity());
                            productDto.put("price", item.getUnitPrice());
                            return productDto;
                        })
                        .collect(Collectors.toList());
                    dto.put("products", products);
                }
                return dto;
            })
            .collect(Collectors.toList());
        model.addAttribute("stockInOrders", stockInOrders);
        
        // Debug: Log stock in orders count
        System.out.println("Stock In Orders found: " + stockInOrders.size());
        System.out.println("Stock In Orders data: " + stockInOrders);

        // Get stock out orders for medicine and convert to simple DTOs
        List<SupplierTransactionsEntity> stockOutOrdersEntities = supplierTransactionRepository
            .findByTransactionTypeAndStatusIn(SupplierTransactionType.STOCK_OUT, 
                List.of(SupplierTransactionStatus.PREPARING),
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
                // Add supplier info without circular reference
                if (entity.getSupplierEntity() != null) {
                    dto.put("supplierName", entity.getSupplierEntity().getName());
                    dto.put("supplierId", entity.getSupplierEntity().getId());
                }
                // Add products info
                if (entity.getSupplierTransactionItemEntities() != null) {
                    List<Map<String, Object>> products = entity.getSupplierTransactionItemEntities().stream()
                        .map(item -> {
                            Map<String, Object> productDto = new HashMap<>();
                            if (item.getProductEntity() != null) {
                                productDto.put("id", item.getProductEntity().getId());
                                productDto.put("name", item.getProductEntity().getName());
                                productDto.put("type", item.getProductEntity().getProductType());
                            }
                            productDto.put("quantity", item.getQuantity());
                            productDto.put("price", item.getUnitPrice());
                            return productDto;
                        })
                        .collect(Collectors.toList());
                    dto.put("products", products);
                }
                return dto;
            })
            .collect(Collectors.toList());
        model.addAttribute("stockOutOrders", stockOutOrders);
        
        // Debug: Log stock out orders count
        System.out.println("Stock Out Orders found: " + stockOutOrders.size());
        System.out.println("Stock Out Orders data: " + stockOutOrders);

        // Add current user for forms
        model.addAttribute("currentUser", getCurrentUser());

        return "templates_storage/medicine";
        } catch (Exception e) {
            // Log error and return empty data
            System.err.println("Error loading medicines: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("medicines", new ArrayList<>());
            model.addAttribute("stockInOrders", new ArrayList<>());
            model.addAttribute("stockOutOrders", new ArrayList<>());
            model.addAttribute("currentUser", getCurrentUser());
            return "templates_storage/medicine";
        }
    }

    @PostMapping("/create")
    public String createMedicine(@ModelAttribute MedicineDTO medicineDTO, Model model) {
        medicineService.createMedicine(medicineDTO);
        return "redirect:/warehouse/medicines";
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute MedicineDTO medicineDTO, Model model) {
        medicineDTO.setId(id);
        medicineService.updateMedicine(medicineDTO);
        return "redirect:/warehouse/medicines";
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id, Model model) {
        medicineService.deleteById(id);
        return "redirect:/warehouse/medicines";
    }

    @PostMapping("/create-supplier-in")
    public String createSupplierIn(@ModelAttribute SupplierInDTO supplierInDTO, Model model) {
        supplierInService.createSupplierIn(supplierInDTO);
        return "redirect:/warehouse/medicines";
    }

    @PostMapping("/process-supplier-in/{supplierInId}")
    public String processSupplierIn(@PathVariable Long supplierInId, Model model) {
        SupplierInDTO supplierIn = supplierInService.getSupplierInById(supplierInId);
        if (supplierIn != null && supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED) {
            medicineService.processSupplierIn(supplierIn);
            supplierInService.updateSupplierInStatus(supplierInId, "COMPLETED");
        }
        return "redirect:/warehouse/medicines";
    }
    
    /**
     * Process stock in for medicine
     * @param id Stock in ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to medicine page
     */
    @PostMapping("/process-stockin/{id}")
    public String processStockIn(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Get supplier in details
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn != null && (supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED || supplierIn.getStatus() == SupplierTransactionStatus.NEED_TO_ADD)) {
                // Update stock quantities for each product
                if (supplierIn.getItems() != null) {
                    for (var item : supplierIn.getItems()) {
                        ProductEntity product = productRepository.findById(item.getProductId()).orElse(null);
                        if (product != null) {
                            // Update stock quantity
                            int currentStock = product.getStockQuantities() != null ? product.getStockQuantities() : 0;
                            product.setStockQuantities(currentStock + item.getQuantity());
                            productRepository.save(product);
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
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi khi xử lý nhập kho: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medicines";
    }
    
    /**
     * Từ chối đơn nhập kho
     * @param id ID của đơn nhập kho
     * @param reason Lý do từ chối
     * @param redirectAttributes Redirect attributes
     * @return Redirect URL
     */
    @PostMapping("/reject/{id}")
    public String rejectStockInOrder(@PathVariable Long id, 
                                   @RequestParam String reason,
                                   RedirectAttributes redirectAttributes) {
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null && (supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED || supplierIn.getStatus() == SupplierTransactionStatus.NEED_TO_ADD)) {
                // Update order status to REJECTED
                supplierInService.updateSupplierInStatus(id, "REJECTED");
                
                redirectAttributes.addFlashAttribute("successMessage", 
                        "Đã từ chối đơn hàng #" + id + ". Lý do: " + reason);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Đơn hàng không tồn tại hoặc không ở trạng thái đã kiểm tra");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi khi từ chối đơn hàng: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medicines";
    }
    
    /**
     * API endpoint for AJAX - Process stock in for medicine
     * @param id Stock in ID
     * @return JSON response
     */
    @PostMapping("/api/process-stockin/{id}")
    @ResponseBody
    public ResponseEntity<String> apiProcessStockIn(@PathVariable Long id) {
        try {
            // Get supplier in details
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn != null && (supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED || supplierIn.getStatus() == SupplierTransactionStatus.NEED_TO_ADD)) {
                // Update stock quantities for each product
                if (supplierIn.getItems() != null) {
                    for (var item : supplierIn.getItems()) {
                        ProductEntity product = productRepository.findById(item.getProductId()).orElse(null);
                        if (product != null) {
                            // Update stock quantity
                            int currentStock = product.getStockQuantities() != null ? product.getStockQuantities() : 0;
                            product.setStockQuantities(currentStock + item.getQuantity());
                            productRepository.save(product);
                        }
                    }
                }
                
                // Update order status to COMPLETED
                supplierInService.updateSupplierInStatus(id, "COMPLETED");
                
                // Move order to StockInInvoice
                supplierInInvoiceService.saveInvoice(supplierIn);
                
                return ResponseEntity.ok("Đã nhập kho thành công đơn hàng #" + id);
            } else {
                return ResponseEntity.badRequest().body("Đơn hàng không tồn tại hoặc không ở trạng thái đã kiểm tra");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xử lý nhập kho: " + e.getMessage());
        }
    }
    
    /**
     * API endpoint for AJAX - Reject stock order
     * @param id Order ID
     * @param reason Rejection reason
     * @return JSON response
     */
    @PostMapping("/api/reject/{id}")
    @ResponseBody
    public ResponseEntity<String> apiRejectOrder(@PathVariable Long id, @RequestParam String reason) {
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null && (supplierIn.getStatus() == SupplierTransactionStatus.INSPECTED || supplierIn.getStatus() == SupplierTransactionStatus.NEED_TO_ADD)) {
                // Update order status to REJECTED
                supplierInService.updateSupplierInStatus(id, "REJECTED");
                
                return ResponseEntity.ok("Đã từ chối đơn hàng #" + id + ". Lý do: " + reason);
            } else {
                return ResponseEntity.badRequest().body("Đơn hàng không tồn tại hoặc không ở trạng thái đã kiểm tra");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi từ chối đơn hàng: " + e.getMessage());
        }
    }
    
    /**
     * API endpoint for AJAX - Prepare stock out order
     * @param id Stock out order ID
     * @return JSON response
     */
    @PostMapping("/api/prepare/{id}")
    @ResponseBody
    public ResponseEntity<String> apiPrepareStockOut(@PathVariable Long id) {
        try {
            SupplierTransactionsEntity supplierOut = supplierTransactionRepository.findById(id).orElse(null);
            if (supplierOut != null && supplierOut.getTransactionType() == SupplierTransactionType.STOCK_OUT && 
                supplierOut.getStatus() == SupplierTransactionStatus.NEED_TO_ADD) {
                
                // Kiểm tra và giảm số lượng tồn kho cho các sản phẩm trong đơn hàng
                boolean canPrepare = true;
                if (supplierOut.getSupplierTransactionItemEntities() != null) {
                    for (var item : supplierOut.getSupplierTransactionItemEntities()) {
                        ProductEntity product = item.getProductEntity();
                        if (product != null && item.getQuantity() != null) {
                            if (product.getStockQuantities() < item.getQuantity()) {
                                canPrepare = false;
                                break;
                            }
                        }
                    }
                    
                    if (canPrepare) {
                        // Giảm số lượng tồn kho
                        for (var item : supplierOut.getSupplierTransactionItemEntities()) {
                            ProductEntity product = item.getProductEntity();
                            if (product != null && item.getQuantity() != null) {
                                product.setStockQuantities(product.getStockQuantities() - item.getQuantity());
                                productRepository.save(product);
                            }
                        }
                        
                        // Cập nhật trạng thái đơn hàng thành PREPARE_DELIVERY
                        supplierOut.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                        supplierTransactionRepository.save(supplierOut);
                        
                        return ResponseEntity.ok("Đã chuẩn bị đơn xuất kho #" + id + " thành công");
                    } else {
                        return ResponseEntity.badRequest().body("Không đủ hàng tồn kho để chuẩn bị đơn xuất kho #" + id);
                    }
                } else {
                    // Nếu không có sản phẩm, chỉ cập nhật trạng thái
                    supplierOut.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                    supplierTransactionRepository.save(supplierOut);
                    
                    return ResponseEntity.ok("Đã chuẩn bị đơn xuất kho #" + id + " thành công");
                }
            } else {
                return ResponseEntity.badRequest().body("Đơn hàng không tồn tại hoặc không ở trạng thái phù hợp");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi chuẩn bị đơn xuất kho: " + e.getMessage());
        }
    }

    /**
     * Chuẩn bị đơn xuất kho
     * @param id ID của đơn xuất kho
     * @param redirectAttributes Redirect attributes
     * @return Redirect URL
     */
    @PostMapping("/prepare/{id}")
    public String prepareStockOutOrder(@PathVariable Long id, 
                                   RedirectAttributes redirectAttributes) {
        try {
            SupplierTransactionsEntity supplierOut = supplierTransactionRepository.findById(id).orElse(null);
            if (supplierOut != null && supplierOut.getTransactionType() == SupplierTransactionType.STOCK_OUT && 
                supplierOut.getStatus() == SupplierTransactionStatus.NEED_TO_ADD) {
                
                // Kiểm tra và giảm số lượng tồn kho cho các sản phẩm trong đơn hàng
                boolean canPrepare = true;
                if (supplierOut.getSupplierTransactionItemEntities() != null) {
                    for (var item : supplierOut.getSupplierTransactionItemEntities()) {
                        ProductEntity product = item.getProductEntity();
                        if (product != null && item.getQuantity() != null) {
                            if (product.getStockQuantities() < item.getQuantity()) {
                                canPrepare = false;
                                break;
                            }
                        }
                    }
                    
                    if (canPrepare) {
                        // Giảm số lượng tồn kho
                        for (var item : supplierOut.getSupplierTransactionItemEntities()) {
                            ProductEntity product = item.getProductEntity();
                            if (product != null && item.getQuantity() != null) {
                                product.setStockQuantities(product.getStockQuantities() - item.getQuantity());
                                productRepository.save(product);
                            }
                        }
                        
                        // Cập nhật trạng thái đơn hàng thành PREPARE_DELIVERY
                        supplierOut.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                        supplierTransactionRepository.save(supplierOut);
                        
                        redirectAttributes.addFlashAttribute("successMessage", 
                                "Đã chuẩn bị đơn xuất kho #" + id + " thành công");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", 
                                "Không đủ hàng tồn kho để chuẩn bị đơn xuất kho #" + id);
                    }
                } else {
                    // Nếu không có sản phẩm, chỉ cập nhật trạng thái
                    supplierOut.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                    supplierTransactionRepository.save(supplierOut);
                    
                    redirectAttributes.addFlashAttribute("successMessage", 
                            "Đã chuẩn bị đơn xuất kho #" + id + " thành công");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                        "Đơn hàng không tồn tại hoặc không ở trạng thái phù hợp");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Lỗi khi chuẩn bị đơn xuất kho: " + e.getMessage());
        }
        
        return "redirect:/warehouse/medicines";
    }
    
    // API endpoint để xử lý đơn nhập kho - chuyển trạng thái thành COMPLETED
    @PostMapping("/api/stock-in/{id}/process")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processStockInOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            
            // Kiểm tra trạng thái hiện tại
            if (order.getStatus() != SupplierTransactionStatus.NEED_TO_ADD) {
                response.put("success", false);
                response.put("message", "Đơn hàng không ở trạng thái có thể xử lý");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Chuyển trạng thái thành COMPLETED
            System.out.println("Changing order status from " + order.getStatus() + " to COMPLETED");
            order.setStatus(SupplierTransactionStatus.COMPLETED);
            SupplierTransactionsEntity savedOrder = supplierTransactionRepository.save(order);
            System.out.println("Order saved with new status: " + savedOrder.getStatus());
            
            response.put("success", true);
            response.put("message", "Đã xử lý đơn nhập kho thành công");
            response.put("orderId", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // API endpoint để chuẩn bị đơn xuất kho - chuyển trạng thái thành PREPARE_DELIVERY
    @PostMapping("/api/stock-out/{id}/prepare")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> prepareStockOutOrder(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            
            // Kiểm tra trạng thái hiện tại
            if (order.getStatus() != SupplierTransactionStatus.PREPARING) {
                response.put("success", false);
                response.put("message", "Đơn hàng không ở trạng thái có thể chuẩn bị");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Chuyển trạng thái thành PREPARE_DELIVERY
            System.out.println("Changing order status from " + order.getStatus() + " to PREPARE_DELIVERY");
            order.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
            SupplierTransactionsEntity savedOrder = supplierTransactionRepository.save(order);
            System.out.println("Order saved with new status: " + savedOrder.getStatus());
            
            response.put("success", true);
            response.put("message", "Đã chuẩn bị đơn xuất kho thành công");
            response.put("orderId", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // API endpoint để từ chối đơn nhập kho - chuyển trạng thái thành REJECTED
    @PostMapping("/api/stock-in/{id}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectStockInOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Lý do từ chối không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            
            // Kiểm tra trạng thái hiện tại
            if (order.getStatus() != SupplierTransactionStatus.NEED_TO_ADD) {
                response.put("success", false);
                response.put("message", "Đơn hàng không ở trạng thái có thể từ chối");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Chuyển trạng thái thành REJECTED và lưu lý do
            order.setStatus(SupplierTransactionStatus.REJECTED);
            order.setNotes("Lý do từ chối: " + reason.trim());
            supplierTransactionRepository.save(order);
            
            response.put("success", true);
            response.put("message", "Đã từ chối đơn nhập kho");
            response.put("orderId", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // API endpoint để từ chối đơn xuất kho - chuyển trạng thái thành REJECTED
    @PostMapping("/api/stock-out/{id}/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> rejectStockOutOrder(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Lý do từ chối không được để trống");
                return ResponseEntity.badRequest().body(response);
            }
            
            SupplierTransactionsEntity order = supplierTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
            
            // Kiểm tra trạng thái hiện tại
            if (order.getStatus() != SupplierTransactionStatus.PREPARING) {
                response.put("success", false);
                response.put("message", "Đơn hàng không ở trạng thái có thể từ chối");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Chuyển trạng thái thành REJECTED và lưu lý do
            order.setStatus(SupplierTransactionStatus.REJECTED);
            order.setNotes("Lý do từ chối: " + reason.trim());
            supplierTransactionRepository.save(order);
            
            response.put("success", true);
            response.put("message", "Đã từ chối đơn xuất kho");
            response.put("orderId", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
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