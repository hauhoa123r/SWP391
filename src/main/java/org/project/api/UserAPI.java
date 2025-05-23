package org.project.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/submit")
public class UserAPI {

    @PostMapping
    public String handleSubmit(@RequestParam String id,
                               @RequestParam String name,
                               @RequestParam int age,
                               Model model) {
        model.addAttribute("msg", "Đã nhận dữ liệu: " + id + ", " + name + ", " + age);
        return "object/result";
    }
}