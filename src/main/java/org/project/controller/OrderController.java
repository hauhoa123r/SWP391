package org.project.controller;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.project.model.dto.CheckoutFormDTO;
import org.project.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //hard coding user's id for testing
    private final Long userId = 2l;

//    @PostMapping("/checkout")
//    public String createOrder(@ModelAttribute("checkoutForm") CheckoutFormDTO form,
//                              @RequestParam("userId") Long userId,
//                              RedirectAttributes redirectAttributes) {
//        try {
//            orderService.createOrder(form, userId);
//            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
//            return "redirect:/orders/summary"; // hoặc redirect đến trang chi tiết
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
//            return "redirect:/checkout";
//        }
//    }

    @PostMapping("/checkout")
    public String createOrder(@ModelAttribute("checkoutForm") CheckoutFormDTO form,
                              RedirectAttributes redirectAttributes) {
        try {
            orderService.createOrder(form, userId);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
            return "redirect:/orders/summary"; // hoặc redirect đến trang chi tiết
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

}
