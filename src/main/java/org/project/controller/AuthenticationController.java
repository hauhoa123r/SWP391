package org.project.controller;

import org.project.model.dto.UserRegisterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {
    @GetMapping("/login")
    public String login() {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String register(ModelMap modelMap) {
        modelMap.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "authentication/register";
    }
}
