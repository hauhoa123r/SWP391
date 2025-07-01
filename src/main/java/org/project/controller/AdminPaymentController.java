package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.PaymentEntity;

import org.project.service.AdminPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.project.model.response.PageResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Controller quản trị cho module thanh toán.
 * <p>
 * Cung cấp 2 endpoint chính:
 * <ul>
 *   <li><b>GET /admin/payments</b>: liệt kê tất cả payment có phân trang.</li>
 *   <li><b>GET /admin/payments/search</b>: tìm kiếm theo các tiêu chí (orderId, amount, khoảng thời gian).</li>
 * </ul>
 * Trả về view Thymeleaf <code>dashboard/payment-list</code> kèm dữ liệu cần thiết.
 * </p>
 */
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    // Chỉ giữ 1 service chuyên cho dashboard để tránh trùng lặp logic.
    private final AdminPaymentService adminPaymentService;

    @GetMapping
    /**
     * Hiển thị danh sách payment (mặc định không áp dụng bộ lọc).
     */
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Gọi service dashboard, nhận PageResponse kèm meta.
        PageResponse<PaymentEntity> res = adminPaymentService.getAllPayments(PageRequest.of(page, size, Sort.by("id").descending()));
        Page<PaymentEntity> pg = res.getContent();
        model.addAttribute("payments", pg.getContent());
        model.addAttribute("page", res);

        return "dashboard/payment";
    }

    @GetMapping("/search")
    /**
     * Tìm kiếm payment với bộ lọc tuỳ chọn.
     */
    public String search(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String customer,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String status,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        PageResponse<PaymentEntity> res = adminPaymentService.searchPayments(
                orderId,
                amount,
                customer,
                method,
                status,
                from,
                to,
                PageRequest.of(page, size, Sort.by("id").descending()));
        Page<PaymentEntity> pg = res.getContent();
        model.addAttribute("payments", pg.getContent());
        model.addAttribute("page", res);
        model.addAttribute("orderId", orderId);
        model.addAttribute("amount", amount);
        model.addAttribute("customer", customer);
        model.addAttribute("method", method);
        model.addAttribute("status", status);

        model.addAttribute("from", from);
        model.addAttribute("to", to);
        return "dashboard/payment";
    }

    @GetMapping("/{id}")
    /**
     * Hiển thị chi tiết một payment.
     */
    public String detail(@PathVariable Long id, Model model) {
        PaymentEntity payment = adminPaymentService.getPaymentById(id);
        model.addAttribute("payment", payment);
        return "dashboard/payment";
    }
}
