package org.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/")
    public String hello(Model model) {
        System.out.println("Vào được controller rồi nhé");
        return "object/result";
    }

}
