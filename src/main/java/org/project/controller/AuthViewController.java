//package org.project.controller;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.project.dto.RegisterDTO;
//import org.project.dto.response.Response;
//import org.project.repository.UserRepository;
//import org.project.service.ForgotPasswordService;
//import org.project.service.UserSecurityService;
//import org.project.service.UserSecutiryServiceImpl;
////import org.project.service.UserService;
//import org.project.utils.ChangePassword;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.sql.Date;
//
//@Controller
//@RequestMapping("/auth-view")
//@RequiredArgsConstructor
//public class AuthViewController {
//
//
//    private final ForgotPasswordService forgotPasswordService;
//
//    private final UserSecurityService userService;
//
//    private final UserRepository userRepository;
//
//    private final UserSecutiryServiceImpl userSecurityServiceImpl;
//    @GetMapping("/login")
//    public String showLoginPage(@RequestParam(value = "redirectTo", required = false) String redirectTo, Model model) {
//        model.addAttribute("redirectTo", redirectTo);
//        return "frontend/login";
//    }
//
//
//    @PostMapping("/login")
//    public String loginSubmit(@RequestParam String email,
//                              @RequestParam String password,
//                              @RequestParam(required = false) String redirectTo,
//                              Model model,
//                              HttpServletResponse response) {
//        try {
//            String view = userSecurityServiceImpl.login(email, password, redirectTo, response);
//            return view;
//
//        } catch (Exception e) {
//            model.addAttribute("error", "Email hoặc mật khẩu sai!");
//            model.addAttribute("redirectTo", redirectTo);
//            return "frontend/login";
//        }
//    }
//    @GetMapping("/logout")
//    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
//
//        Cookie cookie = new Cookie("token", null);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setMaxAge(0); // xóa ngay lập tức
//        response.addCookie(cookie);
//        redirectAttributes.addFlashAttribute("logoutSuccess", "Đăng xuất thành công!");
//        return "redirect:/";
//    }
//
//    @GetMapping("/register")
//    public String showRegisterForm(Model model) {
//        model.addAttribute("registerDTO", new RegisterDTO());
//        return "frontend/register";
//    }
//
//    @PostMapping("/register")
//    public String handleRegister(
//            @ModelAttribute("registerDTO") @Valid RegisterDTO dto,
//            BindingResult result,
//            Model model) {
//
//        if (!result.hasFieldErrors("email") && userRepository.existsByEmail(dto.getEmail())) {
//            result.rejectValue("email", null, "Email đã tồn tại.");
//        }
//
//        if (!result.hasFieldErrors("birthdate")) {
//            try {
//                if (Date.valueOf(dto.getBirthdate()).after(new java.util.Date())) {
//                    result.rejectValue("birthdate", null, "Ngày sinh không được lớn hơn ngày hiện tại.");
//                }
//            } catch (Exception e) {
//                result.rejectValue("birthdate", null, "Định dạng ngày sinh không hợp lệ.");
//            }
//        }
//
//        if (result.hasErrors()) {
//            return "frontend/register";
//        }
//
//        Response response = userService.register(dto);
//
//        if (response.getStatusCode() != 201) {
//            model.addAttribute("error", response.getMessage());
//            return "frontend/register";
//        }
//
//        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
//        return "frontend/login";
//    }
//
//
//
//    @GetMapping("/forgot-password")
//    public String showForgotPage() {
//        return "frontend/forgot-password";
//    }
//
//    @PostMapping("/forgot-password")
//    public String handleEmail(@RequestParam String email, Model model) {
//        ResponseEntity<String> response = forgotPasswordService.sendOtp(email);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return "redirect:/auth-view/verify-otp?email=" + email;
//        } else {
//            model.addAttribute("error", response.getBody());
//            return "frontend/forgot-password";
//        }
//    }
//
//    @GetMapping("/verify-otp")
//    public String showOtpPage(@RequestParam String email, Model model) {
//        model.addAttribute("email", email);
//        return "frontend/verify-otp";
//    }
//
//    @PostMapping("/verify-otp")
//    public String verifyOtp(@RequestParam String email,
//                            @RequestParam Integer otp,
//                            Model model) {
//        ResponseEntity<String> response = forgotPasswordService.verifyOtp(otp, email);
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return "redirect:/auth-view/change-password?email=" + email;
//        } else {
//            model.addAttribute("error", response.getBody());
//            model.addAttribute("email", email);
//            return "frontend/verify-otp";
//        }
//    }
//
//    @GetMapping("/change-password")
//    public String showChangePassword(@RequestParam String email, Model model) {
//        model.addAttribute("email", email);
//        return "frontend/change-password";
//    }
//
//    @PostMapping("/change-password")
//    public String changePassword(@RequestParam String email,
//                                 @RequestParam String password,
//                                 @RequestParam String confirmPassword,
//                                 Model model) {
//        ChangePassword changePassword = new ChangePassword(password, confirmPassword);
//        ResponseEntity<String> response = forgotPasswordService.changePassword(email, changePassword);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            model.addAttribute("message", response.getBody());
//        } else {
//            model.addAttribute("error", response.getBody());
//        }
//
//        model.addAttribute("email", email);
//        return "frontend/change-password";
//    }
//}
