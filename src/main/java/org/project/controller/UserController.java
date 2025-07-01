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