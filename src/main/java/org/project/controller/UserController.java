package org.project.controller;

import org.project.security.AccountDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/setting")
    public String openSetting(@AuthenticationPrincipal AccountDetails accountDetails, Model model) {
        model.addAttribute("currentPhoneNumber", accountDetails.getUserEntity().getPhoneNumber());
        model.addAttribute("currentEmail", accountDetails.getUserEntity().getEmail());
        return "frontend/setting";
    }
}
