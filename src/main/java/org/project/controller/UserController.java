package org.project.controller;

import jakarta.validation.Valid;
import org.project.entity.ProductEntity;
import org.project.entity.UserEntity;
import org.project.enums.*;
import org.project.model.dto.ProductCreateDTO;
import org.project.model.dto.ProductUpdateDTO;
import org.project.model.dto.ProductViewDTO;
import org.project.model.response.ProductAdditionalInfoResponse;
import org.project.repository.CategoryRepository;
import org.project.repository.ProductTagRepository;
import org.project.repository.impl.PharmacyRepositoryImpl;
import org.project.security.AccountDetails;
import org.project.service.PharmacyService;
import org.project.service.UserService;
import org.project.service.impl.PharmacyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    //constructor-based injection of pharmacyService and categoryRepo
    private final PharmacyService pharmacyService;
    private final CategoryRepository categoryRepo;
    private final ProductTagRepository productTagRepository;
    @Autowired
    private PharmacyServiceImpl pharmacyServiceImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private PharmacyRepositoryImpl pharmacyRepositoryCustom;

    public UserController(PharmacyService pharmacyService, CategoryRepository categoryRepo, ProductTagRepository productTagRepository) {
        this.pharmacyService = pharmacyService;
        this.categoryRepo = categoryRepo;
        this.productTagRepository = productTagRepository;
    }

    // Root endpoint to handle GET requests to /users
    @GetMapping("/users")
    public String viewUsers() {
        return "redirect:/users/view";
    }

    // Global model attributes for form selects
    @ModelAttribute("roles") //Gửi dữ liệu từ hàm roles() vào View
    public UserRole[] roles() {
        return UserRole.values();//trả về toàn bộ các giá trị enum.
    }

    @ModelAttribute("statuses")
    public UserStatus[] statuses() { //phương thức trả về mảng các giá trị enum UserRole, dùng để hiển thị danh sách role trong giao diện
        return UserStatus.values();//Trả về một mảng chứa tất cả giá trị của enum
    }

    // ================== Create User ==================
    @GetMapping("/users/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserEntity());
        model.addAttribute("formAction", "/users/create");
        return "dashboard/user-form";
    }

    @PostMapping("/users/create") // Xử lý dữ liệu khi form tạo user được submit (POST đến /users/create)
    public String createUser(@ModelAttribute("user") UserEntity user) { // Nhận dữ liệu từ form và gán vào đối tượng UserEntity
        userService.createUser(user); // Gọi service để lưu user mới vào cơ sở dữ liệu
        return "redirect:/users/view"; // Sau khi tạo xong, chuyển hướng đến trang danh sách user
    }


    // mapping for admin's dashboard
    @GetMapping("/users/admin/dashboard")
    public ModelAndView adminDashboard() {
        ModelAndView mv = new ModelAndView("dashboard/index");
        return mv;
    }

    // ================== Edit User ==================
    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserEntity user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users/view";
        }
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/users/update/" + id);
        return "dashboard/user-form";
    }

    // mapping for patient's dashboard
    @GetMapping("/users/patient/dashboard")
    public ModelAndView patientDashboard() {
        ModelAndView mv = new ModelAndView("dashboard/patient-dashboard");
        return mv;
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") UserEntity user, Model model) {
        try {
            userService.updateUser(id, user);
            model.addAttribute("success", "Cập nhật thông tin người dùng thành công");
            return "redirect:/users/view";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi cập nhật thông tin người dùng: " + e.getMessage());
            return "redirect:/users/edit/" + id;
        }
    }

    // mapping for admin's view of products
    @GetMapping("/users/admin/products")
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
        int endPage = Math.min((int) (totalPages - 1), page + 1);
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
    @GetMapping("/usersadmin/product/edit/{id}")
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

    // ================== View User Details ==================
    @PostMapping("/users/delete/{id}")
    public String deactivateUser(@PathVariable Long id, Model model) {
        try {
            UserEntity user = userService.getUserById(id);
            if (user == null) {
                model.addAttribute("error", "Không tìm thấy người dùng với ID: " + id);
                return "redirect:/users/view";
            }

            // Update user status to INACTIVE
            user.setUserStatus(UserStatus.INACTIVE);
            userService.updateUser(id, user);

            model.addAttribute("success", "Người dùng đã được vô hiệu hóa thành công");
            return "redirect:/users/view";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi vô hiệu hóa người dùng: " + e.getMessage());
            return "redirect:/users/view";
        }
    }

    // ================== View User Details ==================
    @GetMapping("/users/detail/{id}")
    public String viewUserDetails(@PathVariable Long id, Model model) {
        try {
            if (!userService.existsById(id)) {
                model.addAttribute("error", "Không tìm thấy người dùng với ID: " + id);
                return "redirect:/users/view";
            }

            UserEntity user = userService.getUserDetails(id);
            model.addAttribute("user", user);
            model.addAttribute("success", "Đã tải thông tin người dùng thành công");
            return "dashboard/user-info";
        } catch (org.project.exception.ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/users/view";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi tải thông tin người dùng");
            return "redirect:/users/view";
        }
    }

    //post-mapping for admin's product delete
    @PostMapping("/users/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pharmacyServiceImpl.softDeleteById(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted successfully.");
        return "redirect:/admin/products";
    }

    //post-mapping for admin's addding product
    @PostMapping("/users/admin/products/create")
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
    @PostMapping("/users/admin/products/save")
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
    @GetMapping("/users/admin/appointment")
    public ModelAndView adminAppointment() {
        ModelAndView mv = new ModelAndView("dashboard/appointment");
        return mv;
    }

    // mapping for admin's report
    @GetMapping("/users/admin/report")
    public ModelAndView adminPharmacy() {
        ModelAndView mv = new ModelAndView("dashboard/report");
        return mv;
    }

    // Hiển thị tất cả người dùng
    @GetMapping("/users/view")
    public String viewUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) String type,
                            @RequestParam(required = false) String keyword,
                            Model model) {
        Page<UserEntity> users;
        if (type != null && keyword != null) {
            switch (type.toLowerCase()) {
                case "email":
                    users = userService.searchByEmail(keyword, page, size);
                    break;
                case "phone":
                    users = userService.searchByPhoneNumber(keyword, page, size);
                    break;
                case "role":
                    users = userService.searchByRole(keyword, page, size);
                    break;
                case "status":
                    users = userService.searchByStatus(keyword, page, size);
                    break;
                default:
                    users = userService.getAllUsers(page, size);
            }
        } else {
            users = userService.getAllUsers(page, size);
        }

        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchType", type);
        model.addAttribute("keyword", keyword);

        // Add roles and statuses for form selects
        model.addAttribute("roles", roles());
        model.addAttribute("statuses", statuses());

        // Add CSRF token
        model.addAttribute("_csrf", "csrf_token");

        return "dashboard/user-list";
    }

    // Tìm kiếm chung theo type và keyword (ví dụ: /users/search?type=email&keyword=abc)
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam String type,
                              @RequestParam String keyword,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Page<UserEntity> resultPage;
        switch (type) {
            case "email":
                resultPage = userService.searchByEmail(keyword, page, size);
                break;
            case "phone":


                resultPage = userService.searchByPhoneNumber(keyword, page, size);
                break;
            case "role":


                resultPage = userService.searchByRole(keyword, page, size);
                break;
            case "status":


                resultPage = userService.searchByStatus(keyword, page, size);
                break;
            default:
                resultPage = Page.empty();
        }
        addSearchResultToModel(model, resultPage, type, keyword);
        model.addAttribute("currentPage", resultPage.getNumber());
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("pageSize", size);
        return "dashboard/user-list";
    }

    private void addSearchResultToModel(Model model, Page<UserEntity> pageResult, String type, String keyword) {
        model.addAttribute("users", pageResult.getContent());
        model.addAttribute("searchType", type);
        model.addAttribute("keyword", keyword);
    }

    @PostMapping("/users/soft-delete/{id}")
    public String softDeleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.deactivateUser(id);
            redirect.addFlashAttribute("success", "Người dùng đã được xóa tạm thời.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa tạm thời người dùng: " + e.getMessage());
        }
        return "redirect:/users/deleted";
    }

    @GetMapping("/users/deleted")
    public String viewDeletedUsers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Page<UserEntity> deletedUsers = userService.getDeletedUsers(page, size);
        model.addAttribute("deletedUsers", deletedUsers.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", deletedUsers.getTotalPages());
        model.addAttribute("pageSize", size);
        return "dashboard/user-deleted-list";
    }

    @PostMapping("/users/restore/{id}")
    public String restoreUser(@PathVariable Long id, RedirectAttributes redirect) {
        userService.restoreUser(id);
        redirect.addFlashAttribute("success", "Người dùng đã được khôi phục.");
        return "redirect:/users/deleted";
    }

    @PostMapping("/users/delete-permanent/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        userService.deleteUser(id);
        redirect.addFlashAttribute("success", "Người dùng đã bị xóa vĩnh viễn.");
        return "redirect:/users/deleted";
    }

    // mapping for admin's doctors
    @GetMapping("/users/admin/doctor")
    public ModelAndView adminReview() {
        ModelAndView mv = new ModelAndView("dashboard/doctors");
        return mv;
    }

    //mapping for admin's patients view
    @GetMapping("/users/admin/patient")
    public ModelAndView adminPatient() {
        ModelAndView mv = new ModelAndView("dashboard/patient");
        return mv;
    }

    //mapping for admin's categories view
    @GetMapping("/users/admin/category")
    public ModelAndView adminCategory() {
        ModelAndView mv = new ModelAndView("dashboard/category");
        return mv;
    }

    //mapping for admin's payments
    @GetMapping("/users/admin/payment")
    public ModelAndView adminPayment() {
        ModelAndView mv = new ModelAndView("dashboard/payment");
        return mv;
    }

    @GetMapping("/setting")
    public String openSetting(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("currentPhoneNumber", accountDetails.getUserEntity().getPhoneNumber());
        model.addAttribute("currentEmail", accountDetails.getUserEntity().getEmail());
        return "frontend/setting";
    }
}
