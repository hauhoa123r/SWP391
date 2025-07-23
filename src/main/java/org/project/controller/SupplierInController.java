package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.enums.SupplierTransactionStatus;
import org.project.model.dto.SupplierInDTO;
import org.project.model.dto.SupplierInvoiceDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.service.SupplierInInvoiceService;
import org.project.service.SupplierInService;
import org.project.service.SupplierOutInvoiceService;
import org.project.utils.LogUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    
    @Autowired
    private SupplierOutInvoiceService supplierOutInvoiceService;
    
    private static final String REDIRECT_SUPPLIER_INS = "redirect:/supplier-ins";
    private static final String TEMPLATE_STOCK_IN = "templates_storage/StockIn";
    private static final String TEMPLATE_STOCK_IN_DETAIL = "templates_storage/StockInDetail";

    /**
     * Main stock in page displaying supplier ins with relevant statuses
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param status Optional filter by status
     * @param search Optional search term
     * @param type Optional filter by type
     * @param sortBy Optional sorting field (price or date)
     * @param sortDir Optional sorting direction (asc or desc)
     * @return ModelAndView for stock in page
     */
    @GetMapping
    public ModelAndView getAllSupplierIns(
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String type,
                              @RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
                              @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        log.info("Loading stock in page with filters - page: {}, size: {}, status: {}, search: {}, type: {}, sortBy: {}, sortDir: {}", 
                 page, size, status, search, type, sortBy, sortDir);
        
        ModelAndView mv = new ModelAndView(TEMPLATE_STOCK_IN);
        
        try {
            log.debug("===== BEGIN getAllSupplierIns =====");
            // Validate pagination parameters
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            
            // Validate and prepare sort parameters
            String validSortBy = "transactionDate";  // Default sort field
            if ("price".equals(sortBy) || "totalAmount".equals(sortBy)) {
                validSortBy = "totalAmount";
            } else if ("date".equals(sortBy) || "transactionDate".equals(sortBy)) {
                validSortBy = "transactionDate";
            }
            
            Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, validSortBy);
            
            log.debug("Sorting parameters: field={}, direction={}", validSortBy, direction);
            
            // Define allowed statuses for StockIn page 
            // Chỉ hiển thị các đơn với trạng thái: Chờ giao hàng, Đã kiểm tra, Đã nhận hàng
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.WAITING_FOR_DELIVERY,   // Chờ giao hàng
                SupplierTransactionStatus.RECEIVED                // Đã nhận hàng
            );
            
            // Validate the status param if provided
            if (status != null && !status.isEmpty()) {
                try {
                    SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                    if (!allowedStatuses.contains(statusEnum)) {
                        // If status is not allowed on StockIn page, reset it to null
                        status = null;
                        log.warn("Requested status {} is not allowed on StockIn page, resetting filter", statusEnum);
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid status value, reset to null
                    status = null;
                    log.warn("Invalid status value provided: {}", status);
                }
            }
            
            // Log input parameters to service
            Map<String, Object> inputParams = new HashMap<>();
            inputParams.put("page", page);
            inputParams.put("size", size);
            inputParams.put("status", status);
            inputParams.put("search", search);
            inputParams.put("type", type);
            inputParams.put("allowedStatuses", allowedStatuses);
            inputParams.put("sortBy", validSortBy);
            inputParams.put("sortDir", direction);
            
            log.debug("Input parameters prepared for service call");
            LogUtils.logServiceOperation("SupplierInService", "getFilteredSupplierInsForStockIn", inputParams, null);
            
            // Get paginated supplier ins with filters
            log.debug("Calling supplierInService.getFilteredSupplierInsForStockIn");
            try {
                Page<SupplierInDTO> supplierInsPage = supplierInService.getFilteredSupplierInsForStockIn(
                    page, size, status, search, type, allowedStatuses);
                
                log.debug("Service call completed successfully");
                
                // Log the result from service
                LogUtils.logData("SupplierInService result count", supplierInsPage.getTotalElements());
                
                if (!supplierInsPage.isEmpty()) {
                    LogUtils.logData("First item in page", supplierInsPage.getContent().get(0));
                } else {
                    LogUtils.logData("supplierInsPage is empty", null);
                }
                
                // Populate model with data
                populateModelAndView(mv, supplierInsPage, page, size, status, search, type, sortBy, sortDir);
                
                mv.addObject("allowedStatuses", allowedStatuses);
                
                // Add page navigation links
                addPaginationToModelAndView(mv, "supplier-ins", supplierInsPage);
                
                // Add debug info
                mv.addObject("debugInfo", "Trang hiển thị " + supplierInsPage.getNumberOfElements() + 
                    " trong tổng số " + supplierInsPage.getTotalElements() + " đơn nhập kho.");
            } catch (Exception e) {
                log.error("Exception in service call: {}", e.getMessage(), e);
                log.error("Exception type: {}", e.getClass().getName());
                throw e; // Re-throw to be caught by outer try-catch
            }
        } catch (Exception e) {
            log.error("Error preparing stock in page data: {}", e.getMessage(), e);
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception stack trace:");
            for (StackTraceElement element : e.getStackTrace()) {
                log.error("  {}", element.toString());
                if (element.getClassName().contains("org.project")) {
                    // Chỉ log chi tiết cho các lớp trong package của dự án
                    log.error("    Class: {}, Method: {}, Line: {}", 
                        element.getClassName(), element.getMethodName(), element.getLineNumber());
                }
            }
            handleModelAndViewError(mv, size);
        }
        
        return mv;
    }

    /**
     * View supplier in details page
     * @param id Supplier in ID
     * @return ModelAndView for supplier in details page
     */
    @GetMapping("/{id}")
    public ModelAndView getSupplierInById(@PathVariable Long id) {
        log.info("Viewing supplier in with ID: {}", id);
        ModelAndView mv = new ModelAndView(TEMPLATE_STOCK_IN_DETAIL);
        
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            log.debug("Supplier in details: {}", supplierIn);
            log.debug("Items count: {}", supplierIn != null && supplierIn.getItems() != null ? supplierIn.getItems().size() : 0);
            
            if (supplierIn != null) {
                // Ghi log số lượng sản phẩm trong đơn hàng
                if (supplierIn.getItems() != null) {
                    log.info("Found {} items in supplier in with ID: {}", supplierIn.getItems().size(), id);
                    // In thông tin chi tiết về từng sản phẩm để gỡ lỗi
                    supplierIn.getItems().forEach(item -> {
                        log.debug("Item: productId={}, productName={}, quantity={}, unitPrice={}", 
                            item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice());
                    });
                } else {
                    log.warn("Items list is null for supplier in with ID: {}", id);
                }
                
                mv.addObject("supplierIn", supplierIn);
                
                // Add debug info for debugging
                mv.addObject("debugInfo", "Đơn nhập kho ID: " + id + 
                    ", trạng thái: " + supplierIn.getStatus() + 
                    ", số lượng mặt hàng: " + 
                    (supplierIn.getItems() != null ? supplierIn.getItems().size() : 0));
            } else {
                log.warn("Supplier in with ID {} not found", id);
                addErrorMessage(mv, "Không tìm thấy đơn nhập kho với ID: " + id);
                mv.setViewName(REDIRECT_SUPPLIER_INS);
            }
            
        } catch (Exception e) {
            log.error("Error loading supplier in details for ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(mv, "Lỗi khi tải thông tin đơn nhập kho: " + e.getMessage());
            mv.setViewName(REDIRECT_SUPPLIER_INS);
        }
        
        return mv;
    }
    
    private void addErrorMessage(ModelAndView mv, String message) {
        mv.addObject("errorMessage", message);
    }

    /**
     * Create supplier in form submission
     * @param supplierInDTO Supplier in DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier ins page
     */
    @PostMapping
    public ModelAndView createSupplierIn(@ModelAttribute SupplierInDTO supplierInDTO, 
                                  RedirectAttributes redirectAttributes) {
        log.info("Creating new supplier in");
        
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_INS);
        
        try {
            SupplierInDTO created = supplierInService.createSupplierIn(supplierInDTO);
            addSuccessMessage(redirectAttributes, "Đơn nhập kho đã được tạo thành công với ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating supplier in: {}", e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi tạo đơn nhập kho: " + e.getMessage());
        }
        
        return mv;
    }

    /**
     * Update supplier in form submission
     * @param id Supplier in ID
     * @param supplierInDTO Supplier in DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/update")
    public ModelAndView updateSupplierIn(@PathVariable Long id, 
                                  @ModelAttribute SupplierInDTO supplierInDTO,
                                  RedirectAttributes redirectAttributes) {
        log.info("Updating supplier in with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(getRedirectToDetail(id));
        
        try {
            SupplierInDTO updated = supplierInService.updateSupplierIn(id, supplierInDTO);
            if (updated != null) {
                addSuccessMessage(redirectAttributes, "Đơn nhập kho đã được cập nhật thành công");
            } else {
                log.warn("Supplier in with ID {} not found for update", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn nhập kho");
                mv.setViewName(REDIRECT_SUPPLIER_INS);
            }
        } catch (Exception e) {
            log.error("Error updating supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi cập nhật đơn nhập kho: " + e.getMessage());
        }
        
        return mv;
    }

    /**
     * Update supplier in status
     * @param id Supplier in ID
     * @param status New status
     * @param reason Reason for rejection (optional)
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/status")
    public ModelAndView updateSupplierInStatus(@PathVariable Long id, 
                                        @RequestParam String status,
                                        @RequestParam(required = false) String reason,
                                        RedirectAttributes redirectAttributes) {
        log.info("Updating supplier in status: id={}, status={}, reason={}", id, status, reason);
        
        // Redirect to list page instead of detail page
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_INS);
        
        try {
            // Get the current supplier in
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn == null) {
                log.warn("Supplier in with ID {} not found", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn nhập kho");
                return mv;
            }
            
            // Validate status transition
            SupplierTransactionStatus currentStatus = supplierIn.getStatus();
            SupplierTransactionStatus newStatus = SupplierTransactionStatus.valueOf(status);
            
            // Check valid progression
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                String message = String.format(
                    "Không thể chuyển từ trạng thái %s sang %s", 
                    getStatusDisplayName(currentStatus != null ? currentStatus.name() : null),
                    getStatusDisplayName(status)
                );
                addErrorMessage(redirectAttributes, message);
                return mv;
            }
            
            // Update status
            supplierInService.updateSupplierInStatus(id, status);
            
            // Process specific status transitions
            if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
                // Save to invoice when completed
                SupplierInDTO updatedSupplierIn = supplierInService.getSupplierInById(id);
                supplierInInvoiceService.saveInvoice(updatedSupplierIn);
                
                // Show success message
                addSuccessMessage(redirectAttributes, "Đơn nhập kho đã hoàn thành và chuyển tới hóa đơn nhập kho");
                
                // Redirect to invoices
                mv.setViewName("redirect:/supplier-in-invoices");
                return mv;
            } else if (SupplierTransactionStatus.REJECTED.name().equals(status)) {
                // Handle rejection
                if (reason != null && !reason.isEmpty()) {
                    supplierInService.addRejectionReason(id, reason);
                }
                addSuccessMessage(redirectAttributes, "Đơn nhập kho đã bị từ chối" + (reason != null && !reason.isEmpty() ? ": " + reason : ""));
            } else {
                // General status update
                addSuccessMessage(redirectAttributes, "Trạng thái đã được cập nhật thành " + getStatusDisplayName(status));
            }
        } catch (Exception e) {
            log.error("Error updating supplier in status: {}", e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Update supplier in status to INSPECTED, preparing for processing in medicine/equipment page
     * This method supports redirection to medicine or medical equipment pages based on type
     * 
     * @param id Supplier in ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return ModelAndView for appropriate redirection
     */
    @PostMapping("/{id}/process")
    public ModelAndView processSupplierIn(@PathVariable Long id, 
                                        RedirectAttributes redirectAttributes) {
        log.info("Processing supplier in and redirecting to processing page: id={}", id);
        
        // Mặc định redirect về chi tiết, sẽ được ghi đè nếu thành công
        ModelAndView mv = new ModelAndView(getRedirectToDetail(id));
        
        try {
            // Lấy thông tin về đơn hàng
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            
            if (supplierIn == null) {
                throw new IllegalArgumentException("Không tìm thấy đơn hàng với id: " + id);
            }
            
            // Kiểm tra trạng thái hiện tại
            if (supplierIn.getStatus() != SupplierTransactionStatus.RECEIVED) {
                throw new IllegalArgumentException("Đơn hàng phải ở trạng thái 'Đã nhận hàng' để xử lý");
            }
            
            // Thực hiện cập nhật trạng thái sang INSPECTED
            supplierInService.updateSupplierInStatus(id, SupplierTransactionStatus.INSPECTED.name());
            
            // Xác định điểm đến dựa trên loại sản phẩm
            String targetUrl;
            String typeDisplayName;
            
            if (supplierIn.getType() != null) {
                if ("MEDICINE".equals(supplierIn.getType())) {
                    targetUrl = "redirect:/medicine";
                    typeDisplayName = "thuốc";
                } else {
                    // Mặc định là thiết bị y tế
                    targetUrl = "redirect:/medical-equipment";
                    typeDisplayName = "thiết bị y tế";
                }
            } else {
                // Nếu không có thông tin loại, mặc định chuyển đến trang thiết bị y tế
                targetUrl = "redirect:/medical-equipment";
                typeDisplayName = "thiết bị y tế";
            }
            
            // Thêm tham số stockin vào URL để trang đích biết đơn nào cần xử lý
            targetUrl += "?stockin=" + id;
            
            // Thiết lập thông báo thành công
            addSuccessMessage(redirectAttributes, 
                "Đơn hàng đã được chuyển sang trang xử lý. Vui lòng tiếp tục kiểm tra và thêm " + typeDisplayName + " vào kho.");
            
            // Thiết lập view name cho chuyển hướng
            mv.setViewName(targetUrl);
            
            return mv;
        } catch (Exception e) {
            log.error("Lỗi khi xử lý và chuyển hướng đơn hàng: {}", e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi: " + e.getMessage());
            return mv;
        }
    }
    
    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(SupplierTransactionStatus current, SupplierTransactionStatus next) {
        // If current is null, allow any transition
        if (current == null) return true;
        
        switch (current) {
            case WAITING_FOR_DELIVERY:
                return next == SupplierTransactionStatus.RECEIVED || 
                       next == SupplierTransactionStatus.REJECTED;
            case RECEIVED:
                // Từ RECEIVED chuyển thẳng đến INSPECTED (nhưng không hiển thị INSPECTED trên trang StockIn)
                // hoặc có thể chuyển sang REJECTED
                return next == SupplierTransactionStatus.INSPECTED ||
                       next == SupplierTransactionStatus.REJECTED;
            case INSPECTED:
                // INSPECTED không thể chuyển sang COMPLETED trực tiếp từ trang StockIn
                // Việc này sẽ được thực hiện từ trang medicine hoặc medical-equipment
                return next == SupplierTransactionStatus.REJECTED;
            case COMPLETED:
            case REJECTED:
                // Terminal states - no further transitions
                return false;
            default:
                // For any other state, only allow rejection
                return next == SupplierTransactionStatus.REJECTED;
        }
    }

    /**
     * Delete supplier in
     * @param id Supplier in ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier ins page
     */
    @PostMapping("/{id}/delete")
    public ModelAndView deleteSupplierIn(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier in with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_INS);
        
        try {
            supplierInService.deleteSupplierIn(id);
            addSuccessMessage(redirectAttributes, "Đơn nhập kho đã được xóa thành công");
        } catch (Exception e) {
            log.error("Error deleting supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Không thể xóa đơn nhập kho: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Add item to supplier in
     * @param id Supplier in ID
     * @param itemDTO Item DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier in details page
     */
    @PostMapping("/{id}/items")
    public ModelAndView addItemToSupplierIn(
            @PathVariable Long id, 
            @ModelAttribute SupplierRequestItemDTO itemDTO,
            RedirectAttributes redirectAttributes) {
        log.info("Adding item to supplier in with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(getRedirectToDetail(id));
        
        try {
            SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
            if (supplierIn != null) {
                supplierIn.getItems().add(itemDTO);
                supplierInService.updateSupplierIn(id, supplierIn);
                addSuccessMessage(redirectAttributes, "Sản phẩm đã được thêm thành công");
            } else {
                log.warn("Supplier in with ID {} not found for adding item", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn nhập kho");
                mv.setViewName(REDIRECT_SUPPLIER_INS);
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Reject supplier in with reason
     * @param id Supplier in ID
     * @param payload Request payload containing rejection reason
     * @return Response entity
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectSupplierIn(@PathVariable Long id, 
                                            @RequestBody Map<String, String> payload) {
        log.info("Rejecting supplier in with ID: {}, reason: {}", id, payload.get("reason"));
        
        try {
            String reason = payload.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Lý do từ chối không được để trống");
            }
            
            // Update status to REJECTED
            String statusValue = SupplierTransactionStatus.REJECTED.name(); // Sử dụng name() để lấy String
            supplierInService.updateSupplierInStatus(id, statusValue);
            
            // Save the invoice with rejection reason
            processRejectedSupplierIn(id, reason);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error rejecting supplier in with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi từ chối đơn hàng: " + e.getMessage());
        }
    }
    
    // ==================== Helper methods ====================
    
    /**
     * Process a completed supplier in by saving its invoice
     * @param id Supplier in ID
     */
    private void processCompletedSupplierIn(Long id) {
        SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
        if (supplierIn != null) {
            supplierInInvoiceService.saveInvoice(supplierIn);
        }
    }
    
    /**
     * Process a rejected supplier in by saving its invoice with rejection reason
     * @param id Supplier in ID
     * @param reason Rejection reason
     */
    private void processRejectedSupplierIn(Long id, String reason) {
        SupplierInDTO supplierIn = supplierInService.getSupplierInById(id);
        if (supplierIn != null) {
            supplierInInvoiceService.saveInvoiceWithRejection(supplierIn, reason);
        }
    }
    
    /**
     * Populate ModelAndView with supplier in data and pagination
     */
    private void populateModelAndView(ModelAndView mv, Page<SupplierInDTO> supplierInsPage, int page, int size, 
                                      String status, String search, String type, String sortBy, String sortDir) {
        log.debug("populateModelAndView bắt đầu thực thi - Page: {}, Size: {}, Status: {}, Search: {}, Type: {}, SortBy: {}, SortDir: {}", 
                  page, size, status, search, type, sortBy, sortDir);
        
        try {
            // Add supplier ins
            log.debug("Adding supplierIns to model");
            mv.addObject("supplierIns", supplierInsPage.getContent());
            
            // Add pagination data
            log.debug("Adding pagination data to model");
            mv.addObject("currentPage", page);
            mv.addObject("totalPages", supplierInsPage.getTotalPages());
            mv.addObject("totalItems", supplierInsPage.getTotalElements());
            mv.addObject("pageSize", size);
            
            // Add current filters for preserving state
            log.debug("Adding filter data to model");
            mv.addObject("currentStatus", status);
            mv.addObject("currentSearch", search);
            mv.addObject("currentType", type);
            mv.addObject("currentSortBy", sortBy);
            mv.addObject("currentSortDir", sortDir);
            
            // Không load toàn bộ dữ liệu suppliers và products không cần thiết
            // Các dropdown và dữ liệu sẽ được nạp lazy thông qua API khi cần
            
            // Add statusValues for dropdown (không cần truy vấn DB)
            log.debug("Adding statusValues to model");
            mv.addObject("statusValues", SupplierTransactionStatus.values());
            
            // Add user info (replace with actual user service as needed)
            log.debug("Adding isManager flag to model");
            mv.addObject("isManager", true);  // Placeholder - replace with actual authorization check
            
            log.debug("populateModelAndView thực thi thành công");
        } catch (Exception e) {
            log.error("Error in populateModelAndView: {}", e.getMessage(), e);
            throw e; // Re-throw to be handled by caller
        }
    }
    
    /**
     * Ensure a SupplierInDTO has all necessary properties set to avoid null pointer exceptions
     * @param dto The supplier in DTO to validate
     * @return A validated DTO with no null properties
     */
    private SupplierInDTO ensureValidSupplierIn(SupplierInDTO dto) {
        if (dto == null) {
            return new SupplierInDTO();
        }
        
        // Set default values for any null fields
        if (dto.getItems() == null) {
            dto.setItems(new ArrayList<>());
        }
        
        if (dto.getSupplierName() == null) {
            dto.setSupplierName("N/A");
        }
        
        if (dto.getTotalAmount() == null) {
            dto.setTotalAmount(BigDecimal.ZERO);
        }
        
        if (dto.getCreatedAt() == null) {
            dto.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        
        // Đảm bảo status luôn là một enum hợp lệ
        try {
            if (dto.getStatus() == null) {
                dto.setStatus(SupplierTransactionStatus.PENDING);
            }
            
            // Kiểm tra xem status có phải là enum hợp lệ không
            String statusName = dto.getStatus().name();
            SupplierTransactionStatus validStatus = SupplierTransactionStatus.valueOf(statusName);
            
            // Đảm bảo status trong DTO là đối tượng enum thực sự
            dto.setStatus(validStatus);
        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào với status, đặt về PENDING
            log.warn("Invalid status found in DTO with ID: {}, resetting to PENDING", dto.getId());
            dto.setStatus(SupplierTransactionStatus.PENDING);
        }
        
        return dto;
    }
    
    /**
     * Handle model error and populate with basic data
     * @param mv ModelAndView to populate
     * @param size Page size for error fallback
     */
    private void handleModelAndViewError(ModelAndView mv, int size) {
        log.error("handleModelAndViewError method was called - This indicates an exception occurred");
        log.debug("Setting view name to {}", TEMPLATE_STOCK_IN);
        mv.setViewName(TEMPLATE_STOCK_IN);
        
        try {
            // Add empty data
            log.debug("Adding empty supplierIns list to model");
            mv.addObject("supplierIns", Collections.emptyList());
            
            // Add pagination defaults
            log.debug("Adding default pagination data to model");
            mv.addObject("currentPage", 0);
            mv.addObject("totalPages", 0);
            mv.addObject("pageSize", size);
            mv.addObject("totalItems", 0);
            
            // Add page numbers
            log.debug("Adding empty page numbers to model");
            mv.addObject("pageNumbers", Collections.emptyList());
            
            // Add sorting defaults
            mv.addObject("currentSortBy", "transactionDate");
            mv.addObject("currentSortDir", "desc");
            
            // Add allowed statuses
            log.debug("Adding allowed statuses to model");
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.PENDING,
                SupplierTransactionStatus.RECEIVED,
                SupplierTransactionStatus.WAITING_FOR_DELIVERY
            );
            mv.addObject("allowedStatuses", allowedStatuses);
            
            // Add error message
            log.debug("Adding error message to model");
            mv.addObject("errorMessage", "Có lỗi xảy ra khi tải dữ liệu. Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
            
            // Add debug information (helpful for development)
            log.debug("Adding debug info to model");
            mv.addObject("debugInfo", "Xem log server để biết thêm chi tiết về lỗi.");
            
            // Không load dữ liệu suppliers và products khi có lỗi
            mv.addObject("suppliers", Collections.emptyList());
            mv.addObject("products", Collections.emptyList());
            
            log.debug("handleModelAndViewError completed successfully");
        } catch (Exception e) {
            log.error("Exception occurred in handleModelAndViewError: {}", e.getMessage(), e);
            log.error("Exception type: {}", e.getClass().getName());
            // Thiết lập tối thiểu để tránh lỗi phụ
            mv.setViewName(TEMPLATE_STOCK_IN);
            mv.addObject("errorMessage", "Lỗi nghiêm trọng xảy ra. Vui lòng liên hệ admin.");
        }
    }
    
    /**
     * Add success message to redirect attributes
     * @param redirectAttributes RedirectAttributes
     * @param message Success message
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("message", message);
    }
    
    /**
     * Add error message to redirect attributes
     * @param redirectAttributes RedirectAttributes
     * @param message Error message
     */
    private void addErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }
    
    /**
     * Get redirect URL to supplier in detail page
     * @param id Supplier in ID
     * @return Redirect URL
     */
    private String getRedirectToDetail(Long id) {
        return REDIRECT_SUPPLIER_INS + "/" + id;
    }

    /**
     * Add pagination details to ModelAndView
     * @param mv ModelAndView to add pagination to
     * @param baseUrl Base URL for pagination links
     * @param page Page object with pagination data
     */
    private void addPaginationToModelAndView(ModelAndView mv, String baseUrl, Page<?> page) {
        log.debug("BEGIN addPaginationToModelAndView - baseUrl: {}, totalPages: {}", baseUrl, page.getTotalPages());
        try {
            int totalPages = page.getTotalPages();
            if (totalPages > 0) {
                int currentPage = page.getNumber();
                int pageSize = page.getSize();
                long totalItems = page.getTotalElements();
                String sortBy = (String) mv.getModel().getOrDefault("currentSortBy", "transactionDate");
                String sortDir = (String) mv.getModel().getOrDefault("currentSortDir", "desc");
                
                log.debug("Pagination info: currentPage={}, pageSize={}, totalItems={}, sortBy={}, sortDir={}", 
                    currentPage, pageSize, totalItems, sortBy, sortDir);
                
                // Add basic pagination info
                mv.addObject("currentPage", currentPage);
                mv.addObject("totalPages", totalPages);
                mv.addObject("pageSize", pageSize);
                mv.addObject("totalItems", totalItems);
                
                // Calculate page numbers to show (show up to 5 pages, centered around current)
                List<Integer> pageNumbers = new ArrayList<>();
                int startPage = Math.max(0, currentPage - 2);
                int endPage = Math.min(totalPages - 1, currentPage + 2);
                
                log.debug("Page range calculation: startPage={}, endPage={}", startPage, endPage);
                
                // Ensure we show at least 5 pages if available
                if (endPage - startPage < 4) {
                    if (startPage == 0) {
                        endPage = Math.min(4, totalPages - 1);
                    } else if (endPage == totalPages - 1) {
                        startPage = Math.max(0, totalPages - 5);
                    }
                }
                
                log.debug("Adjusted page range: startPage={}, endPage={}", startPage, endPage);
                
                for (int i = startPage; i <= endPage; i++) {
                    pageNumbers.add(i);
                }
                
                log.debug("Page numbers generated: {}", pageNumbers);
                
                mv.addObject("pageNumbers", pageNumbers);
                
                // Add URL base and sort parameters for pagination links
                mv.addObject("baseUrl", baseUrl);
                mv.addObject("sortParam", "&sortBy=" + sortBy + "&sortDir=" + sortDir);
                
                log.debug("END addPaginationToModelAndView - SUCCESS");
            } else {
                log.debug("No pages available, skipping pagination");
                // Nếu không có trang nào, thêm các giá trị mặc định
                mv.addObject("currentPage", 0);
                mv.addObject("totalPages", 0);
                mv.addObject("pageSize", page.getSize());
                mv.addObject("totalItems", 0);
                mv.addObject("pageNumbers", new ArrayList<Integer>());
                mv.addObject("baseUrl", baseUrl);
                mv.addObject("sortParam", "");
                log.debug("END addPaginationToModelAndView - SUCCESS (empty pagination)");
            }
        } catch (Exception e) {
            log.error("Error in addPaginationToModelAndView: {}", e.getMessage(), e);
            log.error("Exception type: {}", e.getClass().getName());
            // Thêm các giá trị mặc định để tránh lỗi trên view
            mv.addObject("currentPage", 0);
            mv.addObject("totalPages", 0);
            mv.addObject("pageSize", 10);
            mv.addObject("totalItems", 0);
            mv.addObject("pageNumbers", new ArrayList<Integer>());
            mv.addObject("baseUrl", baseUrl);
            mv.addObject("sortParam", "");
            log.debug("END addPaginationToModelAndView - ERROR (using defaults)");
        }
    }

    /**
     * API endpoint for StockOutInvoice.html
     * Get paginated stock invoices based on type and filters
     */
    @GetMapping("/inventory/api/stock-invoices")
    @ResponseBody
    public ResponseEntity<?> getStockInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "invoiceDate,desc") String sort,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("API: Getting stock invoices - type: {}, page: {}, size: {}, keyword: {}, status: {}", 
                type, page, size, keyword, status);
        
        try {
            // Validate pagination parameters
            if (page < 0) page = 0;
            if (size <= 0 || size > 100) size = 10; // Giới hạn kích thước trang tối đa là 100
            
            // Parse sort parameter
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? 
                    Sort.Direction.ASC : Sort.Direction.DESC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
            Page<SupplierInvoiceDTO> invoices;
            
            // Use existing service methods
            if ("STOCK_OUT".equalsIgnoreCase(type)) {
                // Get stock out invoices
                invoices = supplierOutInvoiceService.getAllInvoices(page, size, keyword, status);
            } else {
                // Default to stock in invoices
                invoices = supplierInInvoiceService.getAllInvoices(page, size, keyword, status);
            }
            
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error retrieving stock invoices: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get invoices: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * API endpoint for invoice details
     */
    @GetMapping("/inventory/api/stock-invoices/{id}")
    @ResponseBody
    public ResponseEntity<?> getInvoiceById(
            @PathVariable Long id,
            @RequestParam(required = false) String type) {
        
        log.info("API: Getting invoice by ID: {}, type: {}", id, type);
        
        try {
            SupplierInvoiceDTO invoice = null;
            
            if ("STOCK_OUT".equalsIgnoreCase(type)) {
                // Try to get from stock out service
                invoice = supplierOutInvoiceService.getInvoiceById(id);
            } else {
                // Default to stock in service
                invoice = supplierInInvoiceService.getInvoiceById(id);
            }
            
            if (invoice != null) {
                return ResponseEntity.ok(invoice);
            } else {
                // If not found with the specified type, try the other type
                if ("STOCK_OUT".equalsIgnoreCase(type)) {
                    invoice = supplierInInvoiceService.getInvoiceById(id);
                } else {
                    invoice = supplierOutInvoiceService.getInvoiceById(id);
                }
                
                if (invoice != null) {
                    return ResponseEntity.ok(invoice);
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invoice not found with ID: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                }
            }
        } catch (Exception e) {
            log.error("Error getting invoice by ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get invoice: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * API endpoint to load suppliers on demand
     * @return List of suppliers in JSON format
     */
    @GetMapping("/api/suppliers")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getSuppliers(
            @RequestParam(required = false) String query) {
        log.info("API: Loading suppliers on demand, query: {}", query);
        
        try {
            List<SupplierEntity> suppliers;
            
            if (query != null && !query.isEmpty()) {
                // Tìm kiếm supplier theo tên bằng cách filter trên collection
                suppliers = supplierRepository.findAll().stream()
                    .filter(s -> s.getName() != null && 
                            s.getName().toLowerCase().contains(query.toLowerCase()))
                    .limit(20)
                    .collect(Collectors.toList());
            } else {
                // Giới hạn số lượng supplier trả về nếu không có query
                suppliers = supplierRepository.findAll(PageRequest.of(0, 20)).getContent();
            }
            
            // Convert to simple DTOs with only needed fields
            List<Map<String, Object>> result = suppliers.stream()
                .map(supplier -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", supplier.getId());
                    map.put("name", supplier.getName());
                    map.put("email", supplier.getEmail());
                    map.put("phoneNumber", supplier.getPhoneNumber());
                    return map;
                })
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error loading suppliers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }

    /**
     * API endpoint to load products on demand
     * @return List of products in JSON format
     */
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String productType) {
        log.info("API: Loading products on demand, query: {}, type: {}", query, productType);
        
        try {
            List<ProductEntity> products;
            
            // Lọc theo loại sản phẩm (MEDICINE hoặc MEDICAL_PRODUCT)
            if (productType != null && !productType.isEmpty()) {
                products = productRepository.findAllByProductTypeAndProductStatus(
                        ProductType.valueOf(productType), 
                        ProductStatus.ACTIVE);
            } else {
                // Giới hạn số lượng nếu không có bộ lọc loại
                products = productRepository.findAll(PageRequest.of(0, 50)).getContent();
            }
            
            // Lọc theo từ khóa tìm kiếm
            if (query != null && !query.isEmpty()) {
                String lowerCaseQuery = query.toLowerCase();
                products = products.stream()
                    .filter(p -> p.getName() != null && 
                            p.getName().toLowerCase().contains(lowerCaseQuery))
                    .limit(20)
                    .collect(Collectors.toList());
            }
            
            // Giới hạn kết quả
            if (products.size() > 20) {
                products = products.subList(0, 20);
            }
            
            // Convert to simplified DTOs with only needed fields
            List<Map<String, Object>> productDTOs = products.stream()
                .map(product -> {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", product.getId());
                    dto.put("name", product.getName());
                    dto.put("price", product.getPrice());
                    dto.put("stockQuantities", product.getStockQuantities());
                    dto.put("type", product.getProductType() != null ? product.getProductType().name() : "UNKNOWN");
                    dto.put("description", product.getDescription());
                    return dto;
                })
                .collect(Collectors.toList());
            
            log.info("Found {} products matching query: {}, type: {}", 
                    productDTOs.size(), query, productType);
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Error loading products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.emptyList());
        }
    }

    /**
     * Get display name for status
     * @param status Status enum name
     * @return User-friendly status name in Vietnamese
     */
    private String getStatusDisplayName(String status) {
        try {
            // Use displayName property directly from enum if possible
            SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
            return statusEnum.getDisplayName();
        } catch (IllegalArgumentException e) {
            // Fallback for any unknown status values
            Map<String, String> statusNames = new HashMap<>();
            statusNames.put("PENDING", "Chờ thanh toán");
            statusNames.put("COMPLETED", "Hoàn thành");
            statusNames.put("REJECTED", "Từ chối");
            statusNames.put("WAITING_FOR_DELIVERY", "Chờ giao hàng");
            statusNames.put("INSPECTED", "Đã kiểm tra");
            statusNames.put("RECEIVED", "Đã nhận hàng");
            statusNames.put("PREPARE_DELIVERY", "Chuẩn bị giao hàng");
            statusNames.put("DELIVERING", "Đang giao hàng");
            statusNames.put("DELIVERED", "Đã giao hàng");
            statusNames.put("PAID", "Đã thanh toán");
            statusNames.put("PREPARING", "Chuẩn bị đơn hàng");
            statusNames.put("ADDED", "Đã thêm");
            statusNames.put("PREPARED", "Đã chuẩn bị hàng");
            
            return statusNames.getOrDefault(status, status);
        }
    }
}