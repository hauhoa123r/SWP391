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

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private SupplierInService supplierInService;
    
    @Autowired
    private ProductRepository productRepository;
    
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
        Page<MedicineDTO> medicinePage = medicineService.getAllMedicines(page, size, name, sortDirection, sortField);
        model.addAttribute("medicines", medicinePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", medicinePage.getTotalPages());
        model.addAttribute("newMedicine", new MedicineDTO());
        model.addAttribute("supplierIn", new SupplierInDTO());
        return "medicine";
    }

    @PostMapping("/create")
    public String createMedicine(@ModelAttribute MedicineDTO medicineDTO, Model model) {
        medicineService.createMedicine(medicineDTO);
        return "redirect:/medicines";
    }

    @PostMapping("/update/{id}")
    public String updateMedicine(@PathVariable Long id, @ModelAttribute MedicineDTO medicineDTO, Model model) {
        medicineDTO.setId(id);
        medicineService.updateMedicine(medicineDTO);
        return "redirect:/medicines";
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id, Model model) {
        medicineService.deleteById(id);
        return "redirect:/medicines";
    }

    @PostMapping("/create-supplier-in")
    public String createSupplierIn(@ModelAttribute SupplierInDTO supplierInDTO, Model model) {
        supplierInService.createSupplierIn(supplierInDTO);
        return "redirect:/medicines";
    }

    @PostMapping("/process-supplier-in/{supplierInId}")
    public String processSupplierIn(@PathVariable Long supplierInId, Model model) {
        SupplierInDTO supplierIn = supplierInService.getSupplierInById(supplierInId);
        if (supplierIn != null && supplierIn.getStatus() == SupplierTransactionStatus.CHECKED) {
            medicineService.processSupplierIn(supplierIn);
            supplierInService.updateSupplierInStatus(supplierInId, "COMPLETED");
        }
        return "redirect:/medicines";
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
        
        return "redirect:/medicines";
    }
}