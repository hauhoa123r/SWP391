package org.project.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.enums.OrderType;
import org.project.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutFormDTO {

    // ---------- USER INFO ----------
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String phone;

    // ---------- SHIPPING ADDRESS ----------
    @NotNull
    private Long shippingAddressId;

    private String note; // Order notes

    // ---------- ORDER DETAILS ----------
    private List<CartItemDTO> items;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private BigDecimal shippingFee;

    @NotNull
    private BigDecimal realAmount; // = total - coupon + shipping

    // ---------- COUPON ----------
    private String couponCode;
    private Long couponId; // Set ở Controller sau khi kiểm tra code

    // ---------- PAYMENT ----------
    @NotNull
    private PaymentMethod paymentMethod; // CASH, CARD, MOMO

    // ---------- ORDER META ----------
    @NotBlank
    private OrderType orderType; // DIRECT / APPOINTMENT

    private Long appointmentId; // optional, nếu orderType = APPOINTMENT

}