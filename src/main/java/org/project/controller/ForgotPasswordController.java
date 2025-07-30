//package org.project.controller;
//
//
//import lombok.RequiredArgsConstructor;
//import org.project.service.ForgotPasswordService;
//import org.project.utils.ChangePassword;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//
//@RestController
//@RequestMapping("/forgotPassword")
//@RequiredArgsConstructor
//public class ForgotPasswordController {
//
//    private final ForgotPasswordService forgotPasswordService;
//
//
//    @PostMapping("/verify/{email}")
//    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
//        return forgotPasswordService.sendOtp(email);
//    }
//
//    @PostMapping("/verifyOtp/{otp}/{email}")
//    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
//        return forgotPasswordService.verifyOtp(otp, email);
//    }
//
//    @PostMapping("/changePassword/{email}")
//    public ResponseEntity<String> changePassword(@PathVariable String email, @RequestBody ChangePassword changePassword) {
//        return forgotPasswordService.changePassword(email, changePassword);
//    }
//}
