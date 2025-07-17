package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;



import org.springframework.data.domain.Page;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    // Root endpoint to handle GET requests to /users
    @GetMapping("")
    public String viewUsers() {
        return "redirect:/users/view";
    }

    private final UserService userService;

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
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserEntity());
        model.addAttribute("formAction", "/users/create");
        return "dashboard/user-form";
    }

    @PostMapping("/create") // Xử lý dữ liệu khi form tạo user được submit (POST đến /users/create)
    public String createUser(@ModelAttribute("user") UserEntity user) { // Nhận dữ liệu từ form và gán vào đối tượng UserEntity
        userService.createUser(user); // Gọi service để lưu user mới vào cơ sở dữ liệu
        return "redirect:/users/view"; // Sau khi tạo xong, chuyển hướng đến trang danh sách user
    }



    // ================== Edit User ==================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserEntity user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users/view";
        }
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/users/update/" + id);
        return "dashboard/user-form";
    }

    @PostMapping("/update/{id}")
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

    // ================== View User Details ==================
    @PostMapping("/delete/{id}")
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
    @GetMapping("/detail/{id}")
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

    // Hiển thị tất cả người dùng
    @GetMapping("/view")
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
    @GetMapping("/search")
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
    @PostMapping("/soft-delete/{id}")
    public String softDeleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            userService.deactivateUser(id);
            redirect.addFlashAttribute("success", "Người dùng đã được xóa tạm thời.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa tạm thời người dùng: " + e.getMessage());
        }
        return "redirect:/users/deleted";
    }
    @GetMapping("/deleted")
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
    @PostMapping("/restore/{id}")
    public String restoreUser(@PathVariable Long id, RedirectAttributes redirect) {
        userService.restoreUser(id);
        redirect.addFlashAttribute("success", "Người dùng đã được khôi phục.");
        return "redirect:/users/deleted";
    }
    @PostMapping("/delete-permanent/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) {
        userService.deleteUser(id);
        redirect.addFlashAttribute("success", "Người dùng đã bị xóa vĩnh viễn.");
        return "redirect:/users/deleted";
    }

}