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

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private SupplierInService supplierInService;

    @GetMapping
    public String getAllMedicines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) SortDirection sortDirection,
            @RequestParam(required = false) String sortField,
            Model model) {
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
        return "templates_storage/medicine";
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
        if (supplierIn != null && "CHECKED".equals(supplierIn.getStatus())) {
            medicineService.processSupplierIn(supplierIn);
            supplierInService.updateSupplierInStatus(supplierInId, "COMPLETED");
        }
        return "redirect:/medicines";
    }
}