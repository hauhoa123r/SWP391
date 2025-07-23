package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.MedicalProductEntity;
import org.project.entity.ProductEntity;
import org.project.entity.SupplierTransactionsEntity;
import org.project.enums.ProductType;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.project.repository.MedicalProductRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierTransactionRepository;
import org.project.service.MedicalProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling medical equipment operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/medical-equipment")
public class MedicalEquipmentController {

    private final MedicalProductService medicalProductService;
    private final MedicalProductRepository medicalProductRepository;
    private final ProductRepository productRepository;
    private final SupplierTransactionRepository supplierTransactionRepository;

    /**
     * Main page displaying all medical equipment
     * @param model Spring MVC Model
     * @return View name for medical equipment page
     */
    @GetMapping
    public String getAllMedicalEquipment(Model model) {
        log.info("Loading medical equipment page");
        
        try {
            // Get all medical products
            List<ProductEntity> equipments = productRepository.findByProductType("MEDICAL_PRODUCT");
            model.addAttribute("equipments", equipments);
            
            // Get stock in orders for medical equipment - use pageable version but retrieve all results
            List<SupplierTransactionsEntity> stockInOrders = supplierTransactionRepository
                .findByTransactionTypeAndStatus(SupplierTransactionType.STOCK_IN, SupplierTransactionStatus.RECEIVED, 
                    PageRequest.of(0, 1000)).getContent();
            model.addAttribute("stockInOrders", stockInOrders);
            
            // Get stock out orders for medical equipment - use pageable version but retrieve all results
            List<SupplierTransactionsEntity> stockOutOrders = supplierTransactionRepository
                .findByTransactionTypeAndStatus(SupplierTransactionType.STOCK_OUT, SupplierTransactionStatus.RECEIVED,
                    PageRequest.of(0, 1000)).getContent();
            model.addAttribute("stockOutOrders", stockOutOrders);
            
            // Count statistics
            model.addAttribute("productCount", equipments.size());
            model.addAttribute("stockInCount", stockInOrders.size());
            model.addAttribute("stockOutCount", stockOutOrders.size());
            
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
                return "redirect:/medical-equipment";
            } else {
                log.warn("Medical equipment with ID {} not found", id);
                model.addAttribute("errorMessage", "Không tìm thấy thiết bị y tế với ID: " + id);
                return "redirect:/medical-equipment";
            }
        } catch (Exception e) {
            log.error("Error loading medical equipment details for ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Lỗi khi tải thông tin thiết bị y tế: " + e.getMessage());
            return "redirect:/medical-equipment";
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
        
        return "redirect:/medical-equipment";
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
        
        return "redirect:/medical-equipment";
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
        
        return "redirect:/medical-equipment";
    }
}