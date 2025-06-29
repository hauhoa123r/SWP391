package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.UserEntity;
import org.project.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


import java.util.List;
import org.springframework.data.domain.Page;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

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
        return "frontend/user-list";
    }

    // Tìm kiếm theo email
    @GetMapping("/search/email")
    public String searchByEmail(@RequestParam String email, Model model) {
        addSearchResultToModel(model, userService.searchByEmail(email), "email", email);
        return "frontend/user-list";
    }

    // Tìm kiếm theo số điện thoại
    @GetMapping("/search/phone")
    public String searchByPhone(@RequestParam String phoneNumber, Model model) {
        addSearchResultToModel(model, userService.searchByPhoneNumber(phoneNumber), "phone", phoneNumber);
        return "frontend/user-list";
    }

    // Tìm kiếm theo vai trò
    @GetMapping("/search/role")
    public String searchByRole(@RequestParam String role, Model model) {
        addSearchResultToModel(model, userService.searchByRole(role), "role", role);
        return "frontend/user-list";
    }

    // Tìm kiếm theo trạng thái
    @GetMapping("/search/status")
    public String searchByStatus(@RequestParam String status, Model model) {
        addSearchResultToModel(model, userService.searchByStatus(status), "status", status);
        return "frontend/user-list";
    }

    private void addSearchResultToModel(Model model, List<UserEntity> users, String type, String keyword) {
        model.addAttribute("users", users);
        model.addAttribute("searchType", type);
        model.addAttribute("keyword", keyword);
    }
}
