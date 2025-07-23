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
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.project.enums.ProductType;
import org.project.enums.SupplierTransactionType;

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
    
    private static final String REDIRECT_SUPPLIER_OUTS = "redirect:/supplier-outs";
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
                SupplierTransactionStatus.PAID              // Đã thanh toán
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
            
            // Get paginated supplier outs with filters - service handles the filtering by allowed statuses
            Page<SupplierOutDTO> supplierOutsPage = supplierOutService.getFilteredSupplierOuts(page, size, status, search, type);
            
            // Debug log to see what's coming from the database
            log.info("Raw supplier outs from database: count={}", supplierOutsPage.getNumberOfElements());
            for (SupplierOutDTO dto : supplierOutsPage.getContent()) {
                log.info("SupplierOut: id={}, status={}, supplier={}", 
                    dto.getId(), dto.getStatus(), dto.getSupplierName());
            }
            
            // Populate model with data
            populateModelAndView(mv, supplierOutsPage, page, size, status, search, type, sortBy, sortDir);
            
            // Add allowed statuses
            mv.addObject("allowedStatuses", allowedStatuses);
            
            // Add pagination links
            addPaginationToModelAndView(mv, "supplier-outs", supplierOutsPage);
            
            // Add debug info
            mv.addObject("debugInfo", "Trang hiển thị " + supplierOutsPage.getContent().size() + 
                " trong tổng số " + supplierOutsPage.getTotalElements() + " đơn xuất kho.");
            
            log.debug("Stock out page prepared with {} supplier outs", supplierOutsPage.getContent().size());
            log.debug("===== END getAllSupplierOuts: SUCCESS =====");
        } catch (Exception e) {
            log.error("Error preparing stock out page data: {}", e.getMessage(), e);
            handleModelAndViewError(mv, size);
            log.debug("===== END getAllSupplierOuts: ERROR =====");
        }
        
        return mv;
    }
    
    /**
     * Helper method to create test data if needed
     */
    private void createTestData() {
        try {
            // Only create test data with allowed statuses
            List<SupplierTransactionStatus> allowedStatuses = List.of(
                SupplierTransactionStatus.PREPARE_DELIVERY,  // Chuẩn bị giao hàng
                SupplierTransactionStatus.DELIVERING,       // Đang giao hàng
                SupplierTransactionStatus.DELIVERED,        // Đã giao hàng
                SupplierTransactionStatus.PENDING           // Chờ thanh toán
            );
            
            // Create a supplier out with PREPARE_DELIVERY status
            SupplierOutDTO testDto = new SupplierOutDTO();
            testDto.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
            testDto.setSupplierName("Test Supplier");
            testDto.setNotes("Test supplier out created for debugging");
            testDto.setTotalAmount(new BigDecimal("1000.00"));
            testDto.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));
            testDto.setDueDate(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
            testDto.setInvoiceNumber("SO-TEST-" + System.currentTimeMillis());
            
            // Create empty items list
            testDto.setItems(new ArrayList<>());
            
            // Add test item
            SupplierRequestItemDTO item = new SupplierRequestItemDTO();
            item.setProductId(1L); // Assuming product ID 1 exists
            item.setQuantity(10);
            item.setUnitPrice(new BigDecimal("100.00"));
            testDto.getItems().add(item);
            
            // Create the supplier out
            SupplierOutDTO created = supplierOutService.createSupplierOut(testDto);
            log.info("Created test supplier out with ID: {}, Status: {}", created.getId(), created.getStatus());
            
            // Create a few more with different statuses (except the first one we already created)
            for (SupplierTransactionStatus status : List.of(
                    SupplierTransactionStatus.DELIVERING,
                    SupplierTransactionStatus.DELIVERED,
                    SupplierTransactionStatus.PENDING)) {
                
                SupplierOutDTO newDto = new SupplierOutDTO();
                newDto.setStatus(status);
                newDto.setSupplierName("Test Supplier for " + status.getDisplayName());
                newDto.setNotes("Test supplier out with status " + status.getDisplayName());
                newDto.setTotalAmount(new BigDecimal("1000.00"));
                newDto.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));
                newDto.setDueDate(Timestamp.valueOf(LocalDateTime.now().plusDays(5)));
                newDto.setInvoiceNumber("SO-TEST-" + status + "-" + System.currentTimeMillis());
                newDto.setItems(new ArrayList<>());
                
                SupplierRequestItemDTO newItem = new SupplierRequestItemDTO();
                newItem.setProductId(1L);
                newItem.setQuantity(10);
                newItem.setUnitPrice(new BigDecimal("100.00"));
                newDto.getItems().add(newItem);
                
                SupplierOutDTO createdDto = supplierOutService.createSupplierOut(newDto);
                log.info("Created test supplier out with ID: {}, Status: {}", createdDto.getId(), createdDto.getStatus());
            }
            
            log.info("Test data creation completed successfully with all allowed statuses");
        } catch (Exception e) {
            log.error("Error creating test data: {}", e.getMessage(), e);
        }
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
     * @param redirectAttributes Redirect attributes for flash messages
     * @return Redirect to supplier outs page
     */
    @PostMapping
    public ModelAndView createSupplierOut(@ModelAttribute SupplierOutDTO supplierOutDTO, 
                                   RedirectAttributes redirectAttributes) {
        log.info("Creating new supplier out");
        
        ModelAndView mv = new ModelAndView(REDIRECT_SUPPLIER_OUTS);
        
        try {
            // Set default values for new supplier out
            if (supplierOutDTO.getStatus() == null) {
                // Default status to PREPARE_DELIVERY (Chuẩn bị giao hàng)
                supplierOutDTO.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
            }
            
            // Xử lý ngày tháng, đặt mặc định nếu trống
            // Format expected: yyyy-MM-dd HH:mm:ss
            if (supplierOutDTO.getTransactionDate() == null) {
                supplierOutDTO.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));
            }
            
            if (supplierOutDTO.getDueDate() == null) {
                // Set due date to 5 days from now
                LocalDate dueLocalDate = LocalDate.now().plusDays(5);
                supplierOutDTO.setDueDate(Timestamp.valueOf(dueLocalDate.atStartOfDay()));
            }
            
            // Nếu trường expectedDeliveryDate là null hoặc rỗng, đặt mặc định là ngày mai
            if (supplierOutDTO.getExpectedDeliveryDate() == null) {
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                supplierOutDTO.setExpectedDeliveryDate(Timestamp.valueOf(tomorrow.atStartOfDay()));
                log.debug("Setting default expectedDeliveryDate: tomorrow");
            }
            
            // Set transaction type
            supplierOutDTO.setTransactionType(SupplierTransactionType.STOCK_OUT);
            
            // Auto-generate invoice number with "SO" prefix
            String invoiceNumber = "SO-" + System.currentTimeMillis();
            supplierOutDTO.setInvoiceNumber(invoiceNumber);
            
            log.debug("Creating supplier out with: status={}, transactionDate={}, dueDate={}, expectedDeliveryDate={}, invoiceNumber={}",
                     supplierOutDTO.getStatus(),
                     supplierOutDTO.getTransactionDate(),
                     supplierOutDTO.getDueDate(),
                     supplierOutDTO.getExpectedDeliveryDate(),
                     supplierOutDTO.getInvoiceNumber());
                     
            SupplierOutDTO created = supplierOutService.createSupplierOut(supplierOutDTO);
            log.debug("Created supplier out with ID: {}", created.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "Tạo đơn xuất kho thành công!");
        } catch (Exception e) {
            log.error("Error creating supplier out: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tạo đơn xuất kho: " + e.getMessage());
        }
        
        return mv;
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
                mv.setViewName("redirect:/supplier-out-invoices");
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
                supplierOutInvoiceService.saveInvoice(supplierOut);
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
                return "redirect:/supplier-outs";
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
        
        return "redirect:/supplier-outs";
    }

    /**
     * Test endpoint to create a test supplier out
     */
    @GetMapping("/create-test")
    @ResponseBody
    public ResponseEntity<?> createTestSupplierOut() {
        try {
            // Create test data
            createTestData();
            
            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test supplier outs created successfully");
            response.put("count", 4); // 1 PREPARE_DELIVERY + 3 others
            response.put("statuses", List.of(
                SupplierTransactionStatus.PREPARE_DELIVERY.name(),
                SupplierTransactionStatus.DELIVERING.name(),
                SupplierTransactionStatus.DELIVERED.name(),
                SupplierTransactionStatus.PENDING.name()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating test supplier outs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create test supplier outs: " + e.getMessage()));
        }
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
                    out.setStatus(SupplierTransactionStatus.PREPARE_DELIVERY);
                    needsUpdate = true;
                    conversions.put("null->PREPARE_DELIVERY", 
                                   conversions.getOrDefault("null->PREPARE_DELIVERY", 0) + 1);
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
     * Get suppliers for autocomplete
     * @param query Search query
     * @return Response entity with suppliers
     */
    @GetMapping("/api/suppliers")
    @ResponseBody
    public ResponseEntity<?> getSuppliers(@RequestParam(required = false) String query) {
        try {
            List<SupplierEntity> suppliers;
            if (query != null && !query.trim().isEmpty()) {
                // Use a more generic approach since findByNameContainingIgnoreCase may not exist
                suppliers = supplierRepository.findAll().stream()
                    .filter(s -> s.getName() != null && s.getName().toLowerCase().contains(query.toLowerCase()))
                    .toList();
            } else {
                suppliers = supplierRepository.findAll();
            }
            
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            log.error("Error fetching suppliers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch suppliers: " + e.getMessage()));
        }
    }

    /**
     * API endpoint for product search
     * @param query Search query
     * @param productType Product type filter (optional)
     * @return Response entity with products
     */
    @GetMapping("/api/products")
    @ResponseBody
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String productType) {
        
        log.info("API getProducts called with query: '{}', productType: '{}'", query, productType);
        
        try {
            List<Map<String, Object>> productDTOs = new ArrayList<>();
            List<ProductEntity> products;
            
            // Apply both search query and product type filter if provided
            Stream<ProductEntity> productStream = productRepository.findAll().stream();
            
            log.debug("Total products in DB before filtering: {}", productRepository.count());
            
            // Apply name search if query is provided
            if (query != null && !query.trim().isEmpty()) {
                productStream = productStream.filter(p -> 
                    p.getName() != null && p.getName().toLowerCase().contains(query.toLowerCase())
                );
                log.debug("Applied query filter: '{}'", query);
            }
            
            // Apply product type filter if provided
            if (productType != null && !productType.trim().isEmpty()) {
                try {
                    ProductType type = ProductType.valueOf(productType.toUpperCase());
                    productStream = productStream.filter(p -> 
                        p.getProductType() != null && p.getProductType().equals(type)
                    );
                    log.debug("Applied product type filter: '{}'", type);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid product type: {}", productType);
                    // Invalid product type - ignore the filter
                }
            }
            
            // Limit and collect results
            products = productStream.limit(20).toList();
            
            log.debug("Filtered products count: {}", products.size());
            
            // Convert to DTOs with needed fields
            for (ProductEntity product : products) {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", product.getId());
                dto.put("name", product.getName());
                dto.put("price", product.getPrice());
                dto.put("stockQuantities", product.getStockQuantities());
                dto.put("type", product.getProductType() != null ? product.getProductType().name() : "UNKNOWN");
                dto.put("description", product.getDescription());
                
                productDTOs.add(dto);
                log.debug("Added product to results: id={}, name={}, type={}", 
                         product.getId(), product.getName(), 
                         product.getProductType() != null ? product.getProductType().name() : "UNKNOWN");
            }
            
            log.info("Found {} products matching query: '{}', type: '{}'", 
                    productDTOs.size(), query, productType);
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            log.error("Error fetching products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch products: " + e.getMessage()));
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
        
        // Add reference data for dropdowns
        mv.addObject("suppliers", supplierRepository.findAll());
        mv.addObject("products", productRepository.findAll());
        mv.addObject("statusValues", SupplierTransactionStatus.values());
        
            // Add user info (replace with actual user service as needed)
            mv.addObject("isManager", true);  // Placeholder - replace with actual authorization check
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
        return "redirect:/supplier-outs/" + id;
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
}