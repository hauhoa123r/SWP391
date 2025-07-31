package org.project.controller;

import java.util.Optional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.entity.CartItemEntity;
import org.project.entity.CouponEntity;
import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.PaymentMethod;
import org.project.model.dto.CartItemDTO;
import org.project.model.dto.CheckoutFormDTO;

import org.project.model.response.PatientResponse;
import org.project.repository.CouponRepository;
import org.project.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutFormController {

    private final UserService userService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final PatientService patientService;
    private final CartService  cartService;
    private final CouponRepository couponRepository;

    private final Long userId = 2l;
    @GetMapping
    public String showCheckoutForm(Model model) {
        CheckoutFormDTO checkoutForm = new CheckoutFormDTO();

        PatientResponse patientResponse = new PatientResponse();

        // Lấy user hiện tại và tự động điền thông tin
        UserEntity user = userService.getUserById(userId);
        //Check if user's role
        if (user.getUserRole().equals(UserRole.PATIENT)) {
            //get detail by id
            patientResponse = patientService.getPatientById(patientService.getPatientIdByUserId(userId));
        }
        checkoutForm.setFirstName(patientResponse.getFullName().split(" ")[0]);
        checkoutForm.setLastName(patientResponse.getFullName().split(" ")[1]);
        checkoutForm.setEmail(patientResponse.getEmail());
        checkoutForm.setPhoneNumber(patientResponse.getPhoneNumber());

        // Lấy danh sách cart items từ session hoặc service
        List<CartItemEntity> cartItems = cartService.getCart(user.getId());
        List<CartItemDTO> cartItemDTOS = cartItems.stream().map(cartService::convertToCartItemDTO).collect(Collectors.toList());
        checkoutForm.setItems(cartItemDTOS);

        BigDecimal total = cartItemDTOS.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        checkoutForm.setTotalAmount(total);
        checkoutForm.setShippingFee(new BigDecimal("20000"));
        checkoutForm.setRealAmount(total.add(checkoutForm.getShippingFee()));
        
        // Set default payment method
        checkoutForm.setPaymentMethod(PaymentMethod.CASH);

        model.addAttribute("checkoutForm", checkoutForm);
        return "checkout";
    }

    @PostMapping
    public String submitCheckout(@Valid @ModelAttribute CheckoutFormDTO checkoutForm,
                                 BindingResult result,
                                 Model model,
                                 @RequestParam(required = false) String paymentMethod,
                                 java.security.Principal principal) {
        if (result.hasErrors()) {
            return "checkout";
        }

        // Set payment method from form
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            try {
                checkoutForm.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
            } catch (IllegalArgumentException e) {
                // Default to CASH if invalid payment method
                checkoutForm.setPaymentMethod(PaymentMethod.CASH);
            }
        } else {
            checkoutForm.setPaymentMethod(PaymentMethod.CASH);
        }

        // Validate coupon nếu có
        if (checkoutForm.getCouponCode() != null && !checkoutForm.getCouponCode().isBlank()) {
            Optional<CouponEntity> optionalCoupon = couponRepository.findByCode(checkoutForm.getCouponCode());
            if (optionalCoupon.isPresent()) {
                CouponEntity coupon = optionalCoupon.get();
                checkoutForm.setCouponId(coupon.getId());
                // Recalculate amount
                BigDecimal discounted = checkoutForm.getTotalAmount().subtract(coupon.getValue());
                checkoutForm.setRealAmount(discounted.add(checkoutForm.getShippingFee()));
            } else {
                model.addAttribute("invalidCoupon", true);
                return "checkout";
            }
        }

        // Save order
        Long orderId = orderService.placeOrder(checkoutForm, principal.getName());
        return "redirect:/orders/summary?orderId=" + orderId;
    }
}