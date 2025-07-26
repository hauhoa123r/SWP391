package org.project.controller;


import org.project.exception.CouponException;
import org.project.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/apply")
    public String applyCoupon(@RequestParam("code") String code,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
//            // Get userId from session (update this based on your app's auth setup)
//            Long userId = (Long) session.getAttribute("userId");
//            if (userId == null) {
//                redirectAttributes.addFlashAttribute("couponError", "User not logged in.");
//                return "redirect:/login";
//            }
            Long userId=2l;

            couponService.applyCoupon(code, userId, session);
            redirectAttributes.addFlashAttribute("couponSuccess", "Coupon applied successfully!");
        } catch (CouponException e) {
            redirectAttributes.addFlashAttribute("couponError", e.getMessage());
        }

        return "redirect:/cart";
    }
    @GetMapping("/remove")
    public String removeCoupon(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("appliedCoupon");
        session.removeAttribute("discountedTotal");
        redirectAttributes.addFlashAttribute("couponSuccess", "Coupon removed.");
        return "redirect:/cart";
    }
}
