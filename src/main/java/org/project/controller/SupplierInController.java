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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

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
    
    private static final String REDIRECT_SUPPLIER_INS = "redirect:/supplier-ins";
    private static final String TEMPLATE_STOCK_IN = "templates_storage/StockIn";
    private static final String TEMPLATE_STOCK_IN_DETAIL = "templates_storage/StockInDetail";

    /**
     * Main stock in page displaying supplier ins with relevant statuses
     * @param model Spring MVC Model
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param status Optional filter by status
     * @param search Optional search term
     * @param type Optional filter by type
     * @return View name for stock in page
     */
    @GetMapping
    public String getAllSupplierIns(Model model, 
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String type) {
        log.info("Loading stock in page with filters - page: {}, size: {}, status: {}, search: {}, type: {}", 
                 page, size, status, search, type);
        
        try {
            // Validate pagination parameters
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            
            // Define allowed statuses for StockIn page (exclude COMPLETED, REJECTED, and INSPECTED)
            // Đơn sau khi đã kiểm tra (INSPECTED) sẽ không hiển thị ở trang stockin nữa
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.PENDING,
                SupplierTransactionStatus.RECEIVED,
                SupplierTransactionStatus.WAITING_FOR_DELIVERY
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
            
            // Get paginated supplier ins with filters
            Page<SupplierInDTO> supplierInsPage = supplierInService.getFilteredSupplierInsForStockIn(
                page, size, status, search, type, allowedStatuses);
            
            // Populate model with data
            populateListPageModel(model, supplierInsPage, page, size, status, search, type);
            model.addAttribute("allowedStatuses", allowedStatuses);
            
            // Add page navigation links
            addPaginationModel(model, "supplier-ins", supplierInsPage);
            
            log.debug("Stock in page prepared with {} supplier ins", supplierInsPage.getNumberOfElements());
        } catch (Exception e) {
            log.error("Error preparing stock in page data: {}", e.getMessage(), e);
            handleListPageError(model, size);
        }
        
        return TEMPLATE_STOCK_IN;
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
                return TEMPLATE_STOCK_IN_DETAIL;
            } else {
                log.warn("Supplier in with ID {} not found", id);
                return REDIRECT_SUPPLIER_INS;
            }
        } catch (Exception e) {
            log.error("Error loading supplier in details for ID {}: {}", id, e.getMessage(), e);
            return REDIRECT_SUPPLIER_INS;
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
            addSuccessMessage(redirectAttributes, "Stock In request created successfully with ID: " + created.getId());
        } catch (Exception e) {
            log.error("Error creating supplier in: {}", e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to create Stock In request: " + e.getMessage());
        }
        
        return REDIRECT_SUPPLIER_INS;
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
                addSuccessMessage(redirectAttributes, "Stock In request updated successfully");
            } else {
                log.warn("Supplier in with ID {} not found for update", id);
                addErrorMessage(redirectAttributes, "Stock In request not found");
            }
        } catch (Exception e) {
            log.error("Error updating supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to update Stock In request: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
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
            // Bảo vệ trong trường hợp status là null hoặc rỗng
            if (status == null || status.trim().isEmpty()) {
                throw new IllegalArgumentException("Status không được để trống");
            }
            
            // Kiểm tra xem status có phải là một giá trị hợp lệ của enum không
            try {
                SupplierTransactionStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                log.error("Invalid status value: {}", status);
                addErrorMessage(redirectAttributes, "Trạng thái không hợp lệ: " + status);
                return getRedirectToDetail(id);
            }
            
            supplierInService.updateSupplierInStatus(id, status);
            
            // If status is COMPLETED, save invoice
            if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
                processCompletedSupplierIn(id);
            }
            
            addSuccessMessage(redirectAttributes, "Status updated successfully to " + status);
        } catch (Exception e) {
            log.error("Error updating status for supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to update status: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
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
            addSuccessMessage(redirectAttributes, "Stock In request deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to delete Stock In request: " + e.getMessage());
        }
        
        return REDIRECT_SUPPLIER_INS;
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
                supplierInService.updateSupplierIn(id, supplierIn);
                log.debug("Added item to supplier in with ID: {}", id);
                addSuccessMessage(redirectAttributes, "Item added successfully");
            } else {
                log.warn("Supplier in with ID {} not found for adding item", id);
                addErrorMessage(redirectAttributes, "Stock In request not found");
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier in with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Failed to add item: " + e.getMessage());
        }
        
        return getRedirectToDetail(id);
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
        log.debug("Status is COMPLETED, saving invoice for supplier in with ID: {}", id);
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
     * Populate model for list page
     * @param model Spring MVC model
     * @param supplierInsPage Page of supplier ins
     * @param page Current page number
     * @param size Page size
     * @param status Current status filter
     * @param search Current search term
     * @param type Current type filter
     */
    private void populateListPageModel(Model model, Page<SupplierInDTO> supplierInsPage, int page, int size, 
                                      String status, String search, String type) {
        // Add supplier ins - ensure each item is valid
        List<SupplierInDTO> items = supplierInsPage.getContent().stream()
            .filter(item -> item != null)
            .map(this::ensureValidSupplierIn)
            .toList();
        
        model.addAttribute("supplierIns", items);
        
        // Add pagination data
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", supplierInsPage.getTotalPages());
        model.addAttribute("totalItems", supplierInsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        // Add current filters for preserving state
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentType", type);
        
        // Add reference data for dropdowns
        model.addAttribute("suppliers", supplierRepository.findAll());
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("statusValues", SupplierTransactionStatus.values());
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
     * Handle error in list page by adding empty collections to model
     * @param model Spring MVC model
     * @param size Page size
     */
    private void handleListPageError(Model model, int size) {
        model.addAttribute("supplierIns", Collections.emptyList());
        model.addAttribute("suppliers", Collections.emptyList());
        model.addAttribute("products", Collections.emptyList());
        model.addAttribute("statusValues", SupplierTransactionStatus.values());
        model.addAttribute("currentPage", 0);
        model.addAttribute("totalPages", 0);
        model.addAttribute("totalItems", 0);
        model.addAttribute("pageSize", size);
    }
    
    /**
     * Add success message to redirect attributes
     * @param redirectAttributes Redirect attributes
     * @param message Success message
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
    }
    
    /**
     * Add error message to redirect attributes
     * @param redirectAttributes Redirect attributes
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
     * Add pagination data to model
     * @param model The model to add pagination data to
     * @param baseUrl Base URL for pagination links
     * @param page Page object with pagination information
     */
    private void addPaginationModel(Model model, String baseUrl, Page<?> page) {
        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        
        // For better UI experience with pagination
        if (totalPages > 0) {
            List<Integer> pageNumbers = new ArrayList<>();
            
            // Determine range of page numbers to display
            int startPage = Math.max(0, currentPage - 2);
            int endPage = Math.min(totalPages - 1, currentPage + 2);
            
            // Always show at least 5 pages if available
            if (endPage - startPage < 4) {
                if (startPage == 0) {
                    endPage = Math.min(4, totalPages - 1);
                } else {
                    startPage = Math.max(0, endPage - 4);
                }
            }
            
            // Create list of page numbers to display
            for (int i = startPage; i <= endPage; i++) {
                pageNumbers.add(i);
            }
            
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("baseUrl", baseUrl);
            
            // Check if first, prev, next, last buttons should be enabled
            model.addAttribute("hasPrevious", page.hasPrevious());
            model.addAttribute("hasNext", page.hasNext());
            model.addAttribute("isFirst", currentPage == 0);
            model.addAttribute("isLast", currentPage == totalPages - 1);
        }
    }
}