package org.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierEntity;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.model.dto.SupplierOutDTO;
import org.project.model.dto.SupplierRequestItemDTO;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierEntityRepository;
import org.project.service.SupplierOutInvoiceService;
import org.project.service.SupplierOutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for handling supplier stock out operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/warehouse/stock-out")
public class SupplierOutController {

    private final SupplierOutService supplierOutService;
    private final SupplierOutInvoiceService supplierOutInvoiceService;
    private final ProductRepository productRepository;
    private final SupplierEntityRepository supplierRepository;
    
    private static final String REDIRECT_SUPPLIER_OUTS = "redirect:/warehouse/stock-out";
    private static final String TEMPLATE_STOCK_OUT = "templates_storage/StockOut";
    private static final String TEMPLATE_STOCK_OUT_DETAIL = "templates_storage/StockOutDetail";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    if (text == null || text.isEmpty()) {
                        setValue(null);
                    } else if (text.contains("T")) {
                        // Handle ISO format like 2025-07-27T14:52:14.522Z
                        Instant instant = Instant.parse(text);
                        setValue(new Timestamp(instant.toEpochMilli()));
                    } else if (text.contains("-") && text.contains(":")) {
                        // Handle format like 2025-07-27 14:52:14
                        LocalDateTime dateTime = LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        setValue(Timestamp.valueOf(dateTime));
                    } else if (text.contains("-")) {
                        // Handle date only format like 2025-07-27
                        LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        setValue(Timestamp.valueOf(date.atStartOfDay()));
                    } else {
                        // Default fallback
                        setValue(null);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing date string '{}': {}", text, e.getMessage());
                    setValue(null);
                }
            }
        });
    }

    /**
     * Main stock out page displaying supplier outs with relevant statuses
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param status Optional filter by status
     * @param search Optional search term
     * @param type Optional filter by type
     * @param sortBy Optional sorting field (price or date)
     * @param sortDir Optional sorting direction (asc or desc)
     * @return ModelAndView for stock out page
     */
    @GetMapping
    public ModelAndView getAllSupplierOuts(
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String search,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false, defaultValue = "transactionDate") String sortBy,
                               @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        log.debug("===== START getAllSupplierOuts =====");
        ModelAndView mv = new ModelAndView(TEMPLATE_STOCK_OUT);
        
        try {
            // Only show specific statuses for StockOut page
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.PREPARE_DELIVERY,  // Chuẩn bị giao hàng
                SupplierTransactionStatus.DELIVERING,       // Đang giao hàng
                SupplierTransactionStatus.DELIVERED,        // Đã giao hàng
                SupplierTransactionStatus.PENDING,          // Chờ thanh toán
                SupplierTransactionStatus.PAID             // Đã thanh toán
            );
            
            // Handle paging
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            
            // Validate status parameter
            if (status != null && !status.isEmpty()) {
                try {
                    SupplierTransactionStatus statusEnum = SupplierTransactionStatus.valueOf(status);
                    if (!allowedStatuses.contains(statusEnum)) {
                        // If status is not allowed on StockOut page, reset it to null
                        status = null;
                        log.warn("Requested status {} is not allowed on StockOut page, resetting filter", statusEnum);
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid status value, reset to null
                    status = null;
                    log.warn("Invalid status value provided: {}", status);
                }
            }
            
            // Validate and prepare sort parameters
            String validSortBy = "transactionDate";  // Default sort field
            if ("price".equals(sortBy) || "totalAmount".equals(sortBy)) {
                validSortBy = "totalAmount";
            } else if ("date".equals(sortBy) || "transactionDate".equals(sortBy)) {
                validSortBy = "transactionDate";
            }
            
            Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Sort sort = Sort.by(direction, validSortBy);
            
            // Get paginated supplier outs with filters - service handles some of the filtering
            Page<SupplierOutDTO> supplierOutsPage = supplierOutService.getFilteredSupplierOuts(page, size, status, search, type);
            
            // MANUALLY FILTER RESULTS BY ALLOWED STATUSES - this ensures only allowed statuses are displayed
            List<SupplierOutDTO> filteredContent = supplierOutsPage.getContent().stream()
                .filter(dto -> dto.getStatus() != null && allowedStatuses.contains(dto.getStatus()))
                .collect(Collectors.toList());
            
            // Create a new PageImpl with filtered content
            Page<SupplierOutDTO> filteredPage = new PageImpl<>(
                filteredContent,
                supplierOutsPage.getPageable(),
                // We're not updating the total count, which might make pagination imprecise, 
                // but it's acceptable for this fix
                supplierOutsPage.getTotalElements() 
            );
            
            // Debug log to see what's coming from the database after filtering
            log.info("Raw supplier outs from database: count={}, filtered count={}", 
                     supplierOutsPage.getNumberOfElements(), 
                     filteredPage.getNumberOfElements());
            
            for (SupplierOutDTO dto : filteredPage.getContent()) {
                log.info("SupplierOut (filtered): id={}, status={}, supplier={}", 
                    dto.getId(), dto.getStatus(), dto.getSupplierName());
            }
            
            // Populate model with FILTERED data
            populateModelAndView(mv, filteredPage, page, size, status, search, type, sortBy, sortDir);
            
            // Add allowed statuses
            mv.addObject("allowedStatuses", allowedStatuses);
            
            // Add pagination links
            addPaginationToModelAndView(mv, "warehouse/stock-out", filteredPage);
            
            // Add debug info
            mv.addObject("debugInfo", "Trang hiển thị " + filteredPage.getContent().size() + 
                " trong tổng số " + filteredPage.getTotalElements() + " đơn xuất kho (đã lọc theo trạng thái cho phép).");
            
            log.debug("Stock out page prepared with {} supplier outs", filteredPage.getContent().size());
            log.debug("===== END getAllSupplierOuts: SUCCESS =====");
        } catch (Exception e) {
            log.error("Error preparing stock out page data: {}", e.getMessage(), e);
            handleModelAndViewError(mv, size);
            log.debug("===== END getAllSupplierOuts: ERROR =====");
        }
        
        return mv;
    }

    /**
     * View supplier out details page
     * @param id Supplier out ID
     * @return ModelAndView for supplier out details page
     */
    @GetMapping("/{id}")
    public ModelAndView getSupplierOutById(@PathVariable Long id) {
        log.info("Viewing supplier out with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(TEMPLATE_STOCK_OUT_DETAIL);
        
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            
            log.debug("Supplier out details: {}", supplierOut);
            log.debug("Items count: {}", supplierOut != null && supplierOut.getItems() != null ? supplierOut.getItems().size() : 0);
            
            if (supplierOut != null) {
                // Ghi log số lượng sản phẩm trong đơn hàng
                if (supplierOut.getItems() != null) {
                    log.info("Found {} items in supplier out with ID: {}", supplierOut.getItems().size(), id);
                    // In thông tin chi tiết về từng sản phẩm để gỡ lỗi
                    supplierOut.getItems().forEach(item -> {
                        log.debug("Item: productId={}, productName={}, quantity={}, unitPrice={}", 
                            item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice());
                    });
                } else {
                    log.warn("Items list is null for supplier out with ID: {}", id);
                }
                
                mv.addObject("supplierOut", supplierOut);
                
                // Add debug info
                mv.addObject("debugInfo", "Đơn xuất kho ID: " + id + 
                    ", trạng thái: " + supplierOut.getStatus() + 
                    ", số lượng mặt hàng: " + 
                    (supplierOut.getItems() != null ? supplierOut.getItems().size() : 0));
                
                log.debug("Supplier out details loaded for ID: {}", id);
            } else {
                log.warn("Supplier out with ID {} not found", id);
                addMvErrorMessage(mv, "Không tìm thấy đơn xuất kho với ID: " + id);
                mv.setViewName(REDIRECT_SUPPLIER_OUTS);
            }
        } catch (Exception e) {
            log.error("Error loading supplier out details for ID {}: {}", id, e.getMessage(), e);
            addMvErrorMessage(mv, "Lỗi khi tải thông tin đơn xuất kho: " + e.getMessage());
            mv.setViewName(REDIRECT_SUPPLIER_OUTS);
        }
        
        return mv;
    }

    /**
     * Create supplier out form submission
     * @param supplierOutDTO Supplier out DTO from form
     * @param result Binding result
     * @param model Model for view
     * @return Redirect to supplier outs page
     */
    @PostMapping
    public String createSupplierOut(@ModelAttribute("supplierOut") @Valid SupplierOutDTO supplierOutDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("suppliers", supplierRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            return TEMPLATE_STOCK_OUT + "Create";
        }

        log.info("Creating new supplier out transaction");
        try {
            // Set transaction type for stock out
            supplierOutDTO.setTransactionType(SupplierTransactionType.STOCK_OUT);
            
            // Save the supplier out transaction with items
            SupplierOutDTO savedSupplierOut = supplierOutService.createTransaction(supplierOutDTO);
            log.info("Successfully created supplier out transaction with ID: {}", savedSupplierOut.getId());

            // Create an invoice from this supplier out
            supplierOutInvoiceService.createInvoiceFromSupplierOut(savedSupplierOut);
            
            return REDIRECT_SUPPLIER_OUTS;
        } catch (Exception e) {
            log.error("Error creating supplier out transaction: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error creating supplier out transaction: " + e.getMessage());
            model.addAttribute("suppliers", supplierRepository.findAll());
            model.addAttribute("products", productRepository.findAll());
            return TEMPLATE_STOCK_OUT + "Create";
        }
    }

    /**
     * Update supplier out form submission
     * @param id Supplier out ID
     * @param supplierOutDTO Supplier out DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier out details page
     */
    @PostMapping("/{id}/update")
    public ModelAndView updateSupplierOut(@PathVariable Long id, 
                                   @ModelAttribute SupplierOutDTO supplierOutDTO,
                                   RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(getRedirectToDetail(id));
        
        try {
            SupplierOutDTO updated = supplierOutService.updateSupplierOut(id, supplierOutDTO);
            if (updated != null) {
                log.debug("Updated supplier out with ID: {}", id);
                addSuccessMessage(redirectAttributes, "Đơn xuất kho đã được cập nhật thành công");
            } else {
                log.warn("Supplier out with ID {} not found for update", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn xuất kho");
                mv.setViewName(REDIRECT_SUPPLIER_OUTS);
            }
        } catch (Exception e) {
            log.error("Error updating supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi cập nhật đơn xuất kho: " + e.getMessage());
        }
        
        return mv;
    }

    /**
     * Update supplier out status
     * @param id Supplier out ID
     * @param status New status
     * @param reason Reason for rejection (optional)
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier outs list page
     */
    @PostMapping("/{id}/status")
    public ModelAndView updateSupplierOutStatus(@PathVariable Long id, 
                                         @RequestParam String status,
                                         @RequestParam(required = false) String reason,
                                         RedirectAttributes redirectAttributes) {
        log.info("Updating supplier out status: id={}, status={}, reason={}", id, status, reason);
        
        // Redirect về trang danh sách thay vì trang chi tiết
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_OUTS);
        
        try {
            // Validate status progression
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                SupplierTransactionStatus currentStatus = supplierOut.getStatus();
                
                if (!validateStatusProgression(currentStatus, status)) {
                    String message = "Không thể chuyển từ trạng thái " + 
                            getStatusDisplayName(currentStatus.name()) + " sang " + getStatusDisplayName(status);
                    addErrorMessage(redirectAttributes, message);
                    return mv;
                }
                
                // Update status
            supplierOutService.updateSupplierOutStatus(id, status);
            
                // Special handling for PAID status
                if (SupplierTransactionStatus.PAID.name().equals(status)) {
                    // Set payment date to current time for paid orders
                    try {
                        SupplierOutDTO currentSupplierOut = supplierOutService.getSupplierOutById(id);
                        Timestamp paymentDate = Timestamp.valueOf(LocalDateTime.now());
                        
                        // Update DTO
                        currentSupplierOut.setPaymentDate(paymentDate);
                        
                        // Save updated DTO
                        supplierOutService.updateSupplierOut(id, currentSupplierOut);
                        
                        log.debug("Set payment date for supplier out ID {}: {}", id, paymentDate);
                    } catch (Exception e) {
                        log.warn("Could not set payment date: {}", e.getMessage());
                    }
                }
                
                // Special handling for COMPLETED status
                else if (SupplierTransactionStatus.COMPLETED.name().equals(status)) {
                    // Set approved date to current time for completed orders
                    try {
                        SupplierOutDTO currentSupplierOut = supplierOutService.getSupplierOutById(id);
                        Timestamp approvedDate = Timestamp.valueOf(LocalDateTime.now());
                        
                        // Update DTO
                        currentSupplierOut.setApprovedDate(approvedDate);
                        
                        // Save updated DTO
                        supplierOutService.updateSupplierOut(id, currentSupplierOut);
                        
                        // Process order completion (create invoice)
                processCompletedSupplierOut(id);
                        
                        // Redirect to invoice
                mv.setViewName("redirect:/warehouse/invoices/out");
                        addSuccessMessage(redirectAttributes, "Đơn xuất kho đã hoàn thành và chuyển sang hóa đơn");
                        return mv;
                    } catch (Exception e) {
                        log.warn("Could not set approved date: {}", e.getMessage());
                    }
                }
                
                addSuccessMessage(redirectAttributes, "Trạng thái đã được cập nhật thành " + getStatusDisplayName(status));
            } else {
                log.warn("Supplier out with ID {} not found for status update", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn xuất kho");
            }
        } catch (Exception e) {
            log.error("Error updating status for supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Validate status progression to ensure valid status transitions
     * @param currentStatus Current status
     * @param newStatus New status to validate
     * @return true if progression is valid
     */
    private boolean validateStatusProgression(SupplierTransactionStatus currentStatus, String newStatus) {
        try {
            SupplierTransactionStatus newStatusEnum = SupplierTransactionStatus.valueOf(newStatus);
            
            // Define valid progressions
            Map<SupplierTransactionStatus, List<SupplierTransactionStatus>> validProgressions = new HashMap<>();
            validProgressions.put(SupplierTransactionStatus.PREPARE_DELIVERY, 
                                 List.of(SupplierTransactionStatus.DELIVERING, SupplierTransactionStatus.REJECTED));
            validProgressions.put(SupplierTransactionStatus.DELIVERING, 
                                 List.of(SupplierTransactionStatus.DELIVERED, SupplierTransactionStatus.REJECTED));
            validProgressions.put(SupplierTransactionStatus.DELIVERED, 
                                 List.of(SupplierTransactionStatus.PENDING, SupplierTransactionStatus.REJECTED));
            validProgressions.put(SupplierTransactionStatus.PENDING, 
                                 List.of(SupplierTransactionStatus.PAID, SupplierTransactionStatus.REJECTED));
            validProgressions.put(SupplierTransactionStatus.PAID, 
                                 List.of(SupplierTransactionStatus.COMPLETED, SupplierTransactionStatus.REJECTED));
            
            // Check if current status has defined progressions
            if (validProgressions.containsKey(currentStatus)) {
                return validProgressions.get(currentStatus).contains(newStatusEnum);
            }
            
            // For statuses without defined progressions, allow any change
            return true;
        } catch (IllegalArgumentException e) {
            // Invalid status name
            return false;
        }
    }

    /**
     * Delete supplier out
     * @param id Supplier out ID
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier outs page
     */
    @PostMapping("/{id}/delete")
    public ModelAndView deleteSupplierOut(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) {
        log.info("Deleting supplier out with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_OUTS);
        
        try {
            supplierOutService.deleteSupplierOut(id);
            addSuccessMessage(redirectAttributes, "Đơn xuất kho đã được xóa thành công");
        } catch (Exception e) {
            log.error("Error deleting supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Không thể xóa đơn xuất kho: " + e.getMessage());
        }
        
        return mv;
    }

    /**
     * Add item to supplier out
     * @param id Supplier out ID
     * @param itemDTO Item DTO from form
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier out details page
     */
    @PostMapping("/{id}/items")
    public ModelAndView addItemToSupplierOut(
            @PathVariable Long id, 
            @ModelAttribute SupplierRequestItemDTO itemDTO,
            RedirectAttributes redirectAttributes) {
        log.info("Adding item to supplier out with ID: {}", id);
        
        ModelAndView mv = new ModelAndView(getRedirectToDetail(id));
        
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                // Add item to list
                if (supplierOut.getItems() == null) {
                    supplierOut.setItems(new ArrayList<>());
                }
                supplierOut.getItems().add(itemDTO);
                
                // Recalculate total amount
                supplierOutService.updateSupplierOut(id, supplierOut);
                log.debug("Added item to supplier out with ID: {}", id);
                addSuccessMessage(redirectAttributes, "Sản phẩm đã được thêm thành công");
            } else {
                log.warn("Supplier out with ID {} not found for adding item", id);
                addErrorMessage(redirectAttributes, "Không tìm thấy đơn xuất kho");
                mv.setViewName(REDIRECT_SUPPLIER_OUTS);
            }
        } catch (Exception e) {
            log.error("Error adding item to supplier out with ID {}: {}", id, e.getMessage(), e);
            addErrorMessage(redirectAttributes, "Lỗi khi thêm sản phẩm: " + e.getMessage());
        }
        
        return mv;
    }

    /**
     * Process completed supplier out by creating a supplier out invoice
     * @param id Supplier out ID
     */
    private void processCompletedSupplierOut(Long id) {
        try {
            SupplierOutDTO supplierOut = supplierOutService.getSupplierOutById(id);
            if (supplierOut != null) {
                // Create supplier out invoice
                supplierOutInvoiceService.createInvoiceFromSupplierOut(supplierOut);
                log.debug("Created supplier out invoice from supplier out with ID: {}", id);
            }
        } catch (Exception e) {
            log.error("Error processing completed supplier out with ID {}: {}", id, e.getMessage(), e);
            throw e; // Re-throw to handle in caller
        }
    }

    /**
     * Reject supplier out
     * @param id Supplier out id
     * @param reason Rejection reason
     * @param redirectAttributes RedirectAttributes
     * @return Redirect to supplier outs page
     */
    @PostMapping("/{id}/reject")
    public String rejectSupplierOut(
            @PathVariable Long id, 
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            SupplierOutDTO supplierOutDTO = supplierOutService.getSupplierOutById(id);
            if (supplierOutDTO == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn xuất kho có ID: " + id);
                return "redirect:/warehouse/stock-out";
            }
            
            // Set status to REJECTED
            supplierOutDTO.setStatus(SupplierTransactionStatus.REJECTED);
            
            // Add rejection reason to notes
            String notes = supplierOutDTO.getNotes();
            String rejectionNote = "Lý do từ chối: " + reason + " [" + 
                                  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "]";
            
            if (notes == null || notes.trim().isEmpty()) {
                supplierOutDTO.setNotes(rejectionNote);
            } else {
                supplierOutDTO.setNotes(notes + "\n\n" + rejectionNote);
            }
            
            // Update supplier out
            supplierOutService.updateSupplierOut(id, supplierOutDTO);
            
            redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn xuất kho #" + id);
        } catch (Exception e) {
            log.error("Error rejecting supplier out: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi từ chối đơn xuất kho: " + e.getMessage());
        }
        
        return "redirect:/warehouse/stock-out";
    }

    /**
     * Migration endpoint to fix any records with invalid status
     */
    @GetMapping("/migrate-status")
    @ResponseBody
    public ResponseEntity<?> migrateInvalidStatus() {
        try {
            // Get all supplier outs
            List<SupplierOutDTO> allOuts = supplierOutService.getAllSupplierOuts();
            
            int fixed = 0;
            List<Long> fixedIds = new ArrayList<>();
            Map<String, Integer> conversions = new HashMap<>();
            
            // Look for records with mismatched status values
            for (SupplierOutDTO out : allOuts) {
                boolean needsUpdate = false;
                String oldStatus = out.getStatus() != null ? out.getStatus().name() : "null";
                
                // Handle null status
                if (out.getStatus() == null) {
                    out.setStatus(SupplierTransactionStatus.PREPARING);
                    needsUpdate = true;
                    conversions.put("null->PREPARING", 
                                   conversions.getOrDefault("null->PREPARING", 0) + 1);
                }
                // Convert old WAITING_FOR_DELIVERY to new PREPARE_DELIVERY 
                else if (out.getStatus() == SupplierTransactionStatus.WAITING_FOR_DELIVERY) {
                    out.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                    needsUpdate = true;
                    conversions.put("WAITING_FOR_DELIVERY->PREPARE_DELIVERY", 
                                   conversions.getOrDefault("WAITING_FOR_DELIVERY->PREPARE_DELIVERY", 0) + 1);
                }
                // Convert old RECEIVED to new DELIVERED
                else if (out.getStatus() == SupplierTransactionStatus.RECEIVED) {
                    out.setStatus(SupplierTransactionStatus.DELIVERED);
                    needsUpdate = true;
                    conversions.put("RECEIVED->DELIVERED", 
                                   conversions.getOrDefault("RECEIVED->DELIVERED", 0) + 1);
                }
                
                if (needsUpdate) {
                    try {
                        supplierOutService.updateSupplierOut(out.getId(), out);
                        fixed++;
                        fixedIds.add(out.getId());
                        log.info("Fixed supplier out record with ID: {}, changed status from {} to {}", 
                                out.getId(), oldStatus, out.getStatus().name());
                    } catch (Exception e) {
                        log.error("Error fixing supplier out with ID {}: {}", out.getId(), e.getMessage(), e);
                    }
                }
            }
            
            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Migration completed");
            response.put("fixed", fixed);
            response.put("fixedIds", fixedIds);
            response.put("conversions", conversions);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error migrating invalid statuses: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to migrate invalid statuses: " + e.getMessage()));
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
     * Populate ModelAndView with supplier out data and pagination
     */
    private void populateModelAndView(ModelAndView mv, Page<SupplierOutDTO> supplierOutsPage, int page, int size,
                                      String status, String search, String type, String sortBy, String sortDir) {
        try {
            // Add supplier outs
            mv.addObject("supplierOuts", supplierOutsPage.getContent());

            // Add pagination data
            mv.addObject("currentPage", page);
            mv.addObject("totalPages", supplierOutsPage.getTotalPages());
            mv.addObject("totalItems", supplierOutsPage.getTotalElements());
            mv.addObject("pageSize", size);

            // Add current filters for preserving state
            mv.addObject("currentStatus", status);
            mv.addObject("currentSearch", search);
            mv.addObject("currentType", type);
            mv.addObject("currentSortBy", sortBy);
            mv.addObject("currentSortDir", sortDir);

            // Không load toàn bộ danh sách suppliers và products nếu không cần thiết
            // Chỉ load khi hiển thị màn hình hoặc cần cho dropdown

            // Thêm statusValues cho dropdown (không cần truy vấn cơ sở dữ liệu)
            mv.addObject("statusValues", SupplierTransactionStatus.values());

            // Add user info (replace with actual user service as needed)
            mv.addObject("isManager", true);  // Placeholder - replace with actual authorization check

            // Add current user for forms
            mv.addObject("currentUser", getCurrentUser());
        } catch (Exception e) {
            log.error("Error in populateModelAndView: {}", e.getMessage(), e);
            throw e; // Re-throw to be handled by caller
        }
    }
    
    /**
     * Add pagination details to ModelAndView
     * @param mv ModelAndView to add pagination to
     * @param baseUrl Base URL for pagination links
     * @param page Page object with pagination data
     */
    private void addPaginationToModelAndView(ModelAndView mv, String baseUrl, Page<?> page) {
        try {
            int totalPages = page.getTotalPages();
        if (totalPages > 0) {
                int currentPage = page.getNumber();
                int pageSize = page.getSize();
                long totalItems = page.getTotalElements();
                
                // Add basic pagination info
                mv.addObject("currentPage", currentPage);
                mv.addObject("totalPages", totalPages);
                mv.addObject("pageSize", pageSize);
                mv.addObject("totalItems", totalItems);
                
                // Calculate page numbers to show (show up to 5 pages, centered around current)
            List<Integer> pageNumbers = new ArrayList<>();
                int startPage = Math.max(0, currentPage - 2);
                int endPage = Math.min(totalPages - 1, currentPage + 2);
                
                // Ensure we show at least 5 pages if available
                if (endPage - startPage < 4) {
                    if (startPage == 0) {
                        endPage = Math.min(4, totalPages - 1);
                    } else if (endPage == totalPages - 1) {
                        startPage = Math.max(0, totalPages - 5);
                    }
                }
            
            for (int i = startPage; i <= endPage; i++) {
                pageNumbers.add(i);
            }
            
            mv.addObject("pageNumbers", pageNumbers);
                
                // Add URL base for pagination links
                mv.addObject("baseUrl", baseUrl);
            } else {
                // No pages available
                mv.addObject("currentPage", 0);
                mv.addObject("totalPages", 0);
                mv.addObject("pageSize", page.getSize());
                mv.addObject("totalItems", 0);
                mv.addObject("pageNumbers", new ArrayList<Integer>());
                mv.addObject("baseUrl", baseUrl);
            }
        } catch (Exception e) {
            log.error("Error in addPaginationToModelAndView: {}", e.getMessage(), e);
            // Add default values to avoid errors in view
            mv.addObject("currentPage", 0);
            mv.addObject("totalPages", 0);
            mv.addObject("pageSize", 10);
            mv.addObject("totalItems", 0);
            mv.addObject("pageNumbers", new ArrayList<Integer>());
            mv.addObject("baseUrl", baseUrl);
        }
    }
    
    /**
     * Handle error in ModelAndView by adding empty collections
     */
    private void handleModelAndViewError(ModelAndView mv, int size) {
        mv.addObject("supplierOuts", Collections.emptyList());
        // Không load suppliers và products khi xảy ra lỗi
        mv.addObject("suppliers", Collections.emptyList());
        mv.addObject("products", Collections.emptyList());
        mv.addObject("statusValues", SupplierTransactionStatus.values());
        mv.addObject("currentPage", 0);
        mv.addObject("totalPages", 0);
        mv.addObject("totalItems", 0);
        mv.addObject("pageSize", size);
        mv.addObject("pageNumbers", Collections.emptyList());
        mv.addObject("errorMessage", "Lỗi khi tải dữ liệu xuất kho");
        
        // Add debug information
        mv.addObject("debugInfo", "Xem log server để biết thêm chi tiết về lỗi.");
    }

    /**
     * Add success message to redirect attributes
     * @param redirectAttributes Redirect attributes
     * @param message Success message
     */
    private void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("message", message);
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
     * Get redirect URL to supplier out details page
     * @param id Supplier out ID
     * @return Redirect URL
     */
    private String getRedirectToDetail(Long id) {
        return "redirect:/warehouse/stock-out/" + id;
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

    private void addMvErrorMessage(ModelAndView mv, String message) {
        mv.addObject("errorMessage", message);
    }
    
    /**
     * Import stock out orders from Excel file
     * @param file Excel file containing stock out data
     * @param redirectAttributes Redirect attributes for messages
     * @return Redirect to stock out page
     */
    @PostMapping("/import-excel")
    public String importFromExcel(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file Excel!");
                return REDIRECT_SUPPLIER_OUTS;
            }

            // Validate file extension
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") && !fileName.toLowerCase().endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Chỉ hỗ trợ file Excel (.xlsx, .xls)!");
                return REDIRECT_SUPPLIER_OUTS;
            }

            // Process Excel file
            log.info("Starting to parse Excel file: {}", file.getOriginalFilename());
            List<Map<String, Object>> excelData = parseExcelFile(file);
            log.info("Parsed {} rows from Excel file", excelData.size());
            
            if (excelData.isEmpty()) {
                log.warn("Excel file contains no data");
                redirectAttributes.addFlashAttribute("errorMessage", "File Excel không có dữ liệu hoặc định dạng không đúng!");
                return REDIRECT_SUPPLIER_OUTS;
            }

            // Create stock out orders from Excel data
            int successCount = 0;
            int errorCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (int i = 0; i < excelData.size(); i++) {
                try {
                    Map<String, Object> rowData = excelData.get(i);
                    log.info("Processing Excel row {}: product={}, quantity={}, recipient={}", 
                        i + 2, rowData.get("productName"), rowData.get("quantity"), rowData.get("recipient"));
                    createStockOutFromExcelRow(rowData, i + 2); // +2 because Excel rows start from 1 and we skip header
                    successCount++;
                    log.info("Successfully processed row {}", i + 2);
                } catch (Exception e) {
                    errorCount++;
                    errorMessages.append("Dòng ").append(i + 2).append(": ").append(e.getMessage()).append("; ");
                    log.error("Error processing Excel row {}: {}", i + 2, e.getMessage(), e);
                }
            }

            // Add result message
            if (successCount > 0 && errorCount == 0) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("Import thành công %d đơn xuất kho từ Excel!", successCount));
            } else if (successCount > 0 && errorCount > 0) {
                redirectAttributes.addFlashAttribute("warningMessage", 
                    String.format("Import thành công %d đơn, %d đơn lỗi. Chi tiết lỗi: %s", 
                        successCount, errorCount, errorMessages.toString()));
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    String.format("Import thất bại! Chi tiết lỗi: %s", errorMessages.toString()));
            }

        } catch (Exception e) {
            log.error("Error importing Excel file: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý file Excel: " + e.getMessage());
        }

        return REDIRECT_SUPPLIER_OUTS;
    }

    /**
     * Parse Excel file and extract data
     * @param file Excel file
     * @return List of row data
     * @throws Exception if file parsing fails
     */
    private List<Map<String, Object>> parseExcelFile(org.springframework.web.multipart.MultipartFile file) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        try (java.io.InputStream inputStream = file.getInputStream()) {
            // Use Apache POI to read Excel file
            org.apache.poi.ss.usermodel.Workbook workbook;

            if (file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
                workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(inputStream);
            } else {
                workbook = new org.apache.poi.hssf.usermodel.HSSFWorkbook(inputStream);
            }

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            // Expected columns: Ngày xuất, Người nhận, Lý do xuất, Tên sản phẩm, Số lượng, Đơn giá, Ghi chú
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, Object> rowData = new HashMap<>();

                // Parse each column
                rowData.put("transactionDate", getCellValueAsString(row.getCell(0)));
                rowData.put("recipient", getCellValueAsString(row.getCell(1)));
                rowData.put("reason", getCellValueAsString(row.getCell(2)));
                rowData.put("productName", getCellValueAsString(row.getCell(3)));
                rowData.put("quantity", getCellValueAsString(row.getCell(4)));
                rowData.put("unitPrice", getCellValueAsString(row.getCell(5)));
                rowData.put("notes", getCellValueAsString(row.getCell(6)));
                rowData.put("invoiceNumber", getCellValueAsString(row.getCell(7)));
                rowData.put("taxAmount", getCellValueAsString(row.getCell(8)));
                rowData.put("shippingCost", getCellValueAsString(row.getCell(9)));
                rowData.put("paymentMethod", getCellValueAsString(row.getCell(10)));
                rowData.put("dueDate", getCellValueAsString(row.getCell(11)));
                rowData.put("paymentDate", getCellValueAsString(row.getCell(12)));
                rowData.put("expectedDeliveryDate", getCellValueAsString(row.getCell(13)));

                // Skip empty rows
                if (rowData.values().stream().allMatch(v -> v == null || v.toString().trim().isEmpty())) {
                    continue;
                }

                result.add(rowData);
            }

            workbook.close();
        }

        return result;
    }

    /**
     * Get cell value as string
     * @param cell Excel cell
     * @return String value
     */
    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Create stock out order from Excel row data
     * @param rowData Row data from Excel
     * @param rowNumber Row number for error reporting
     * @throws Exception if creation fails
     */
    private void createStockOutFromExcelRow(Map<String, Object> rowData, int rowNumber) throws Exception {
        // Validate required fields
        String productName = (String) rowData.get("productName");
        String quantityStr = (String) rowData.get("quantity");
        String unitPriceStr = (String) rowData.get("unitPrice");
        String recipient = (String) rowData.get("recipient");
        String reason = (String) rowData.get("reason");
        
        if (productName == null || productName.trim().isEmpty()) {
            throw new Exception("Tên sản phẩm không được để trống");
        }
        
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            throw new Exception("Số lượng không được để trống");
        }
        
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new Exception("Người nhận không được để trống");
        }
        
        // Parse numeric values
        int quantity;
        double unitPrice = 0;
        
        try {
            quantity = Integer.parseInt(quantityStr.trim());
            if (quantity <= 0) {
                throw new Exception("Số lượng phải lớn hơn 0");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Số lượng không đúng định dạng: " + quantityStr);
        }
        
        if (unitPriceStr != null && !unitPriceStr.trim().isEmpty()) {
            try {
                unitPrice = Double.parseDouble(unitPriceStr.trim());
            } catch (NumberFormatException e) {
                throw new Exception("Đơn giá không đúng định dạng: " + unitPriceStr);
            }
        }
        
        // Find product by name
        log.info("Searching for product: '{}'", productName.trim());
        List<ProductEntity> products = productRepository.findByProductStatusAndNameContainingIgnoreCase(
            org.project.enums.ProductStatus.ACTIVE, productName.trim(), PageRequest.of(0, 10)).getContent();
        log.info("Found {} products matching '{}'", products.size(), productName.trim());
        
        if (products.isEmpty()) {
            // Try to find all active products to see what's available
            List<ProductEntity> allActiveProducts = productRepository.findByProductStatus(org.project.enums.ProductStatus.ACTIVE, PageRequest.of(0, 100)).getContent();
            log.warn("No products found for '{}'. Available active products: {}", 
                productName.trim(), 
                allActiveProducts.stream().map(p -> p.getName()).collect(Collectors.joining(", ")));
            throw new Exception("Không tìm thấy sản phẩm: " + productName);
        }

        ProductEntity product = products.get(0); // Use first match
        log.info("Selected product: {} (ID: {})", product.getName(), product.getId());

        // Create SupplierOutDTO
        log.info("Creating SupplierOutDTO with recipient: {}, reason: {}", recipient.trim(), reason);
        SupplierOutDTO supplierOutDTO = new SupplierOutDTO();
        supplierOutDTO.setRecipient(recipient.trim());
        supplierOutDTO.setStockOutReason(reason != null ? reason.trim() : "OTHER");
        supplierOutDTO.setNotes((String) rowData.get("notes"));
        supplierOutDTO.setStatus(SupplierTransactionStatus.PREPARING);
        supplierOutDTO.setTransactionType(SupplierTransactionType.STOCK_OUT);
        supplierOutDTO.setInventoryManagerId(256L); // Default user ID
        
        // Set additional fields from Excel
        String invoiceNumber = (String) rowData.get("invoiceNumber");
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            supplierOutDTO.setInvoiceNumber(invoiceNumber.trim());
        }
        
        // Parse tax amount
        String taxAmountStr = (String) rowData.get("taxAmount");
        if (taxAmountStr != null && !taxAmountStr.trim().isEmpty()) {
            try {
                supplierOutDTO.setTaxAmount(BigDecimal.valueOf(Double.parseDouble(taxAmountStr.trim())));
            } catch (NumberFormatException e) {
                log.warn("Invalid tax amount format: {}", taxAmountStr);
            }
        }
        
        // Parse shipping cost
        String shippingCostStr = (String) rowData.get("shippingCost");
        if (shippingCostStr != null && !shippingCostStr.trim().isEmpty()) {
            try {
                supplierOutDTO.setShippingCost(BigDecimal.valueOf(Double.parseDouble(shippingCostStr.trim())));
            } catch (NumberFormatException e) {
                log.warn("Invalid shipping cost format: {}", shippingCostStr);
            }
        }
        
        // Set payment method
        String paymentMethod = (String) rowData.get("paymentMethod");
        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
            supplierOutDTO.setPaymentMethod(paymentMethod.trim());
        }
        
        // Parse due date
        String dueDateStr = (String) rowData.get("dueDate");
        if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
            try {
                LocalDate dueDate = LocalDate.parse(dueDateStr.trim());
                supplierOutDTO.setDueDate(Timestamp.valueOf(dueDate.atStartOfDay()));
            } catch (Exception e) {
                log.warn("Invalid due date format: {}", dueDateStr);
            }
        }
        
        // Parse payment date
        String paymentDateStr = (String) rowData.get("paymentDate");
        if (paymentDateStr != null && !paymentDateStr.trim().isEmpty()) {
            try {
                LocalDate paymentDate = LocalDate.parse(paymentDateStr.trim());
                supplierOutDTO.setPaymentDate(Timestamp.valueOf(paymentDate.atStartOfDay()));
            } catch (Exception e) {
                log.warn("Invalid payment date format: {}", paymentDateStr);
            }
        }
        
        // Parse expected delivery date
        String expectedDeliveryDateStr = (String) rowData.get("expectedDeliveryDate");
        if (expectedDeliveryDateStr != null && !expectedDeliveryDateStr.trim().isEmpty()) {
            try {
                LocalDate expectedDeliveryDate = LocalDate.parse(expectedDeliveryDateStr.trim());
                supplierOutDTO.setExpectedDeliveryDate(Timestamp.valueOf(expectedDeliveryDate.atStartOfDay()));
            } catch (Exception e) {
                log.warn("Invalid expected delivery date format: {}", expectedDeliveryDateStr);
            }
        }
        
        // Set default supplier ID (you may need to adjust this based on your business logic)
        // For now, we'll use the first available supplier
        List<SupplierEntity> suppliers = supplierRepository.findAll(PageRequest.of(0, 1)).getContent();
        log.info("Found {} suppliers in system", suppliers.size());
        if (!suppliers.isEmpty()) {
            supplierOutDTO.setSupplierId(suppliers.get(0).getId());
            log.info("Using supplier ID: {}", suppliers.get(0).getId());
        } else {
            log.error("No suppliers found in system");
            throw new Exception("Không tìm thấy supplier nào trong hệ thống");
        }

        // Set transaction date
        String transactionDateStr = (String) rowData.get("transactionDate");
        if (transactionDateStr != null && !transactionDateStr.trim().isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(transactionDateStr.trim());
                supplierOutDTO.setTransactionDate(Timestamp.valueOf(date.atStartOfDay()));
            } catch (Exception e) {
                // Use current date if parsing fails
                supplierOutDTO.setTransactionDate(new Timestamp(System.currentTimeMillis()));
            }
        } else {
            supplierOutDTO.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        }

        // Create item
        log.info("Creating item for product ID: {}, quantity: {}, unit price: {}", product.getId(), quantity, unitPrice);
        List<SupplierRequestItemDTO> items = new ArrayList<>();
        SupplierRequestItemDTO item = new SupplierRequestItemDTO();
        item.setProductId(product.getId());
        item.setQuantity(quantity);
        
        // Set unit price
        BigDecimal unitPriceBD = unitPrice > 0 ? BigDecimal.valueOf(unitPrice) : product.getPrice();
        item.setUnitPrice(unitPriceBD);
        
        // Calculate total price
        BigDecimal totalPrice = unitPriceBD.multiply(BigDecimal.valueOf(quantity));
        item.setTotalPrice(totalPrice);
        items.add(item);

        supplierOutDTO.setItems(items);
        log.info("Created item with total price: {}", totalPrice);

        // Calculate total amount
        BigDecimal totalAmount = items.stream()
            .map(SupplierRequestItemDTO::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        supplierOutDTO.setTotalAmount(totalAmount);
        log.info("Set total amount: {}", totalAmount);

        // Save to database
        log.info("Attempting to create stock out order from Excel row {}: product={}, quantity={}, recipient={}",
            rowNumber, productName, quantity, recipient);
        try {
            SupplierOutDTO createdOrder = supplierOutService.createTransaction(supplierOutDTO);
            log.info("Successfully created stock out order with ID: {}", createdOrder.getId());
        } catch (Exception e) {
            log.error("Failed to create stock out order: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get current user for template usage
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