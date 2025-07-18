package org.project.controller;

import lombok.RequiredArgsConstructor; 

import java.util.List;

import org.project.config.ModelMapperConfig;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.model.dto.ProductCreateDTO;
import org.project.model.dto.ProductUpdateDTO;
import org.project.model.dto.ProductViewDTO;
import org.project.model.response.ProductAdditionalInfoResponse;
import org.project.repository.CategoryRepository;
import org.project.repository.ProductTagRepository;
import org.project.repository.impl.PharmacyRepositoryImpl;
import org.project.repository.impl.custom.PharmacyRepositoryCustom;
import org.project.service.PharmacyService;
import org.project.service.impl.PharmacyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class UserController {
	
	@Autowired 
	private PharmacyServiceImpl pharmacyServiceImpl; 
	
	
	@Autowired 
	private PharmacyRepositoryImpl pharmacyRepositoryCustom; 
	
	//constructor-based injection of pharmacyService and categoryRepo 
	private final PharmacyService pharmacyService; 
	private final CategoryRepository categoryRepo;
	private final ProductTagRepository productTagRepository;
	
	public UserController(PharmacyService pharmacyService, CategoryRepository categoryRepo, ProductTagRepository productTagRepository ) {
		this.pharmacyService = pharmacyService; 
		this.categoryRepo = categoryRepo;
		this.productTagRepository = productTagRepository;
	}
	
	@GetMapping("")
	public String hello(Model model) {
		return "redirect:/product-home";
	}

	// mapping for admin's dashboard
	@GetMapping("/admin/dashboard")
	public ModelAndView adminDashboard() {
		ModelAndView mv = new ModelAndView("dashboard/index");
		return mv;
	}

	// mapping for patient's dashboard
	@GetMapping("/patient/dashboard")
	public ModelAndView patientDashboard() {
		ModelAndView mv = new ModelAndView("dashboard/patient-dashboard");
		return mv;
	}

	// mapping for admin's view of products 
	@GetMapping("/admin/products")
	public ModelAndView adminProduct(@RequestParam(defaultValue = "1") Integer page) { 
		ModelAndView mv = new ModelAndView("dashboard/products");
		int pageSize = 7; // Number of products per page 
		int offset = (page - 1) * pageSize; // Calculate the offset for pagination 
		// Fetch paginated products from the custom repository 
		List<ProductViewDTO> products = pharmacyRepositoryCustom.getPagedProducts(pageSize, offset); 
		//Get total number of products for pagination 
		Long totalProducts = pharmacyServiceImpl.countProducts(); 
		//Calculate total pages 
		Long totalPages = (totalProducts + pageSize - 1) / pageSize; // Ceiling division

		//set startPage and endPage
		int startPage = Math.max(1, page - 1);
		int endPage = Math.min((int)(totalPages - 1), page + 1);
		// Add products and pagination info to the model 
		mv.addObject("products", products); 
		mv.addObject("currentPage", page); 
		mv.addObject("totalPages", totalPages); 
		mv.addObject("totalProducts", totalProducts);
		//add startPage and endPage for paging
		mv.addObject("startPage", startPage);
		mv.addObject("endPage", endPage);
		
		//add model for adding product 
		mv.addObject("productDTO", new ProductCreateDTO()); 
		//add categories 
		mv.addObject("categories", categoryRepo.findAll()); 
		//add product type 
		mv.addObject("types", ProductType.values()); 
		//add status 
		mv.addObject("statuses", ProductStatus.values()); 
		//add labels 
		mv.addObject("labels", Label.values()); 
		return mv;
	}


	//get mapping to the update form
	@GetMapping("admin/product/edit/{id}")
	public ModelAndView editProduct(@PathVariable Long id) {
		ModelAndView mv = new ModelAndView("dashboard/product-edit");
		//find detail by id
		ProductUpdateDTO productUpdateDTO = pharmacyService.getProductUpdateDetailById(id);
		//Check if no additional info found in product
		if (productUpdateDTO.getAdditionalInfos() == null || productUpdateDTO.getAdditionalInfos().isEmpty()) {
			productUpdateDTO.setAdditionalInfos(List.of(new ProductAdditionalInfoResponse()));
		}

		//add object
		mv.addObject("productUpdateDTO", productUpdateDTO);
		//get more data
		mv.addObject("categories", categoryRepo.findAll());
		//get types
		mv.addObject("types", ProductType.values());
		//get statuses
		mv.addObject("statuses", ProductStatus.values());
		//get labels
		mv.addObject("labels", Label.values());
		//get additional info
//		mv.addObject("additionalInfo", productUpdateDTO.getAdditionalInfos());
		//get tags
		mv.addObject("availableTags", productTagRepository.findAllDistinctTagNames());
		//add object
		mv.addObject("currentImageUrl", productUpdateDTO.getCurrentImageUrl());
		return mv;
	}


	//post-mapping for admin's product delete 
	@PostMapping("/admin/products/delete/{id}")
	public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    pharmacyServiceImpl.softDeleteById(id);  
	    redirectAttributes.addFlashAttribute("success", "Product deleted successfully.");
	    return "redirect:/admin/products";
	} 
	
	//post-mapping for admin's addding product 
	@PostMapping("/admin/products/create")
	public String createProduct(@Valid @ModelAttribute("productDTO") ProductCreateDTO dto, 
			BindingResult result, Model model) {
		//check if result has errors 
		if (result.hasErrors()) {
			int pageSize = 7; // Number of products per page 
			//set page to be 1 
			int page = 1; 
			//find offset 
			int offset = (page - 1) * pageSize; // Calculate the offset for pagination 
			// Fetch paginated products from the custom repository 
			List<ProductViewDTO> products = pharmacyRepositoryCustom.getPagedProducts(pageSize, offset); 
			//Get total number of products for pagination 
			Long totalProducts = pharmacyServiceImpl.countProducts(); 
			//Calculate total pages 
			Long totalPages = (totalProducts + pageSize - 1) / pageSize; // Ceiling division 
			// Add products and pagination info to the model 
			model.addAttribute("products", products);  
			model.addAttribute("currentPage", page); 
			model.addAttribute("totalPages", totalPages); 
			model.addAttribute("totalProducts", totalProducts); 
			//keep the categories 
			model.addAttribute("categories", categoryRepo.findAll()); 
			//keep the types 
			model.addAttribute("types", ProductType.values()); 
			//keep the status 
			model.addAttribute("statuses", ProductStatus.values()); 
			//keep labels 
			model.addAttribute("labels", Label.values()); 
			//return view 
			return "dashboard/products"; 
		}
		//save the product if no problem found 
		ProductEntity product = pharmacyService.saveFromDTO(dto); 
		//add categories 
		pharmacyService.addCategoriesToProduct(product, dto.getCategoryIds()); 
		//redirect 
		return "redirect:/admin/products?page=1"; 
	}


	//post-mapping for admin's addding product
	@PostMapping("/admin/products/save")
	public String saveProduct(@Valid @ModelAttribute("productUpdateDTO") ProductUpdateDTO dto,
								BindingResult result, Model model) {
		//check if result has errors
		if (result.hasErrors()) {
			System.out.println(result.getAllErrors());
			// ✅ Đảm bảo luôn có ít nhất 1 dòng Additional Info
			if (dto.getAdditionalInfos() == null || dto.getAdditionalInfos().isEmpty()) {
				dto.setAdditionalInfos(List.of(new ProductAdditionalInfoResponse()));
			}
			//keep the dto
			model.addAttribute("productUpdateDTO", dto);
			//keep the categories
			model.addAttribute("categories", categoryRepo.findAll());
			//keep the types
			model.addAttribute("types", ProductType.values());
			//keep the status
			model.addAttribute("statuses", ProductStatus.values());
			//keep labels
			model.addAttribute("labels", Label.values());
			//keep tags
			model.addAttribute("availableTags", productTagRepository.findAllDistinctTagNames());
			//check image
			if (dto.getImageFile() == null || dto.getImageFile().isEmpty()) {
				// giữ lại URL ảnh hiện tại (nếu có)
				model.addAttribute("currentImageUrl", dto.getCurrentImageUrl());
			}

			//return view
			return "dashboard/product-edit";
		}
		//save the product if no problem found
		ProductEntity product = pharmacyService.updateProductFromDTO(dto);
		//redirect
		return "redirect:/admin/products?page=1";
	}
	
	
	// mapping for admin's appointments
	@GetMapping("/admin/appointment")
	public ModelAndView adminAppointment() {
		ModelAndView mv = new ModelAndView("dashboard/appointment");
		return mv;
	}

	// mapping for admin's report
	@GetMapping("/admin/report")
	public ModelAndView adminPharmacy() {
		ModelAndView mv = new ModelAndView("dashboard/report");
		return mv;
	}

	// mapping for admin's doctors
	@GetMapping("/admin/doctor")
	public ModelAndView adminReview() {
		ModelAndView mv = new ModelAndView("dashboard/doctors");
		return mv;
	}
	//mapping for admin's patients view 
	@GetMapping("/admin/patient") 
	public ModelAndView adminPatient() {
		ModelAndView mv = new ModelAndView("dashboard/patient");
		return mv;
	} 
	//mapping for admin's categories view 
	@GetMapping("/admin/category") 
	public ModelAndView adminCategory() {
		ModelAndView mv = new ModelAndView("dashboard/category");
		return mv;
	} 
	//mapping for admin's payments 
	@GetMapping("/admin/payment") 
	public ModelAndView adminPayment() {
		ModelAndView mv = new ModelAndView("dashboard/payment");
		return mv;
	} 
}
