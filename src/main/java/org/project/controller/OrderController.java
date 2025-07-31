package org.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.entity.CartItemEntity;
import org.project.entity.PatientEntity;
import org.project.model.dto.CheckoutFormDTO;
import org.project.repository.PatientRepository;
import org.project.service.CartService;
import org.project.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PatientRepository patientRepository;
    private final CartService cartService;

    //hard coding user's id for testing
    private final Long userId = 4l;

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

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        CheckoutFormDTO checkoutFormDTO = new CheckoutFormDTO();

        // Nếu có user đăng nhập:
        Long userId = 10L; // hard-code tạm thời
        PatientEntity patient = patientRepository.findByUserEntity_Id(userId);

        String fullName = patient.getFullName();
        String[] parts = fullName.trim().split("\\s+"); // tách bởi khoảng trắng

        String lastName = parts.length > 0 ? parts[0] : "";
        String firstName = parts.length > 1
                ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))
                : "";

        if (patient != null) {
            checkoutFormDTO.setFirstName(firstName);
            checkoutFormDTO.setLastName(lastName);
            checkoutFormDTO.setEmail(patient.getEmail());
            checkoutFormDTO.setPhoneNumber(patient.getPhoneNumber());
            checkoutFormDTO.setAddress(patient.getAddress());
            checkoutFormDTO.setBirthdate(patient.getBirthdate().toString());
        }

        model.addAttribute("checkoutFormDTO", checkoutFormDTO);
        List<CartItemEntity> cartItems = cartService.getCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.calculateTotal(userId));
        model.addAttribute("size", cartItems.size());
        return "checkout"; // đúng với tên file HTML
    }

    @PostMapping("/checkout")
    public String createOrder(@ModelAttribute("checkoutForm") CheckoutFormDTO form,
                              RedirectAttributes redirectAttributes) {
        try {
            orderService.createOrder(form, userId);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
            return "redirect:/orders/summary"; // hoặc redirect đến trang chi tiết
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    @GetMapping("/summary")
    public String orderSummary(Model model) {
        CheckoutFormDTO checkoutFormDTO = new CheckoutFormDTO();

        // Nếu có user đăng nhập:
        Long userId = 4L; // hard-code tạm thời
        PatientEntity patient = patientRepository.findByUserEntity_Id(userId);

        String fullName = patient.getFullName();
        String[] parts = fullName.trim().split("\\s+"); // tách bởi khoảng trắng

        String lastName = parts.length > 0 ? parts[0] : "";
        String firstName = parts.length > 1
                ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))
                : "";

        if (patient != null) {
            checkoutFormDTO.setFirstName(firstName);
            checkoutFormDTO.setLastName(lastName);
            checkoutFormDTO.setEmail(patient.getEmail());
            checkoutFormDTO.setPhoneNumber(patient.getPhoneNumber());
            checkoutFormDTO.setAddress(patient.getAddress());
            checkoutFormDTO.setBirthdate(patient.getBirthdate().toString());
        }

        model.addAttribute("checkoutFormDTO", checkoutFormDTO);
        List<CartItemEntity> cartItems = cartService.getCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.calculateTotal(userId));
        model.addAttribute("size", cartItems.size());
        return "frontend/order-received"; // đúng với tên file HTML
    }




}
