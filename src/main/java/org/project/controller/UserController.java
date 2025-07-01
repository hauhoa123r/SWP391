package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.UserEntity;
import org.project.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;



import org.springframework.data.domain.Page;


@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Global model attributes for form selects
    @ModelAttribute("roles")
    public org.project.enums.UserRole[] roles() {
        return org.project.enums.UserRole.values();
    }

    @ModelAttribute("statuses")
    public org.project.enums.UserStatus[] statuses() {
        return org.project.enums.UserStatus.values();
    }

    // ================== Create User ==================
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new org.project.entity.UserEntity());
        model.addAttribute("formAction", "/users/create");
        return "dashboard/user-form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") org.project.entity.UserEntity user) {
        userService.createUser(user);
        return "redirect:/users/view";
    }

    // ================== Edit User ==================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        org.project.entity.UserEntity user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users/view";
        }
        model.addAttribute("user", user);
        model.addAttribute("formAction", "/users/update/" + id);
        return "dashboard/user-form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") org.project.entity.UserEntity user) {
        userService.updateUser(id, user);
        return "redirect:/users/view";
    }

    // ================== Delete User ==================
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users/view";
    }

    // Hiển thị tất cả người dùng
    @GetMapping({ "", "/view" })
    public String viewUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Page<UserEntity> userPage = userService.getAllUsers(page, size);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("pageSize", size);
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




}