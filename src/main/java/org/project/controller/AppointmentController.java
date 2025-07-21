package org.project.controller;

import jakarta.servlet.http.HttpSession;
import org.project.model.response.UserResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @GetMapping
    public String getAppointmentPage(HttpSession session, Model model) {
        // TODO: Simulate that user is logged in with id = 1
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        session.setAttribute("user", userResponse);
        model.addAttribute("user", userResponse);
        return "/frontend/appointment";
    }
}
