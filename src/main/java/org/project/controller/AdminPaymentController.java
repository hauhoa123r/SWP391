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

@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        PageResponse<PaymentEntity> res = adminPaymentService.getAllPayments(
                PageRequest.of(page, size, Sort.by("id").descending()));
        Page<PaymentEntity> paymentPage = res.getContent();

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("pageSize", paymentPage.getSize());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("baseUrl", "/admin/payments");
        model.addAttribute("isSearch", false);

        return "dashboard/payment";
    }

    @GetMapping("/search")
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

        boolean noFilter = orderId == null && amount == null &&
                (customer == null || customer.isBlank()) &&
                (method == null || method.isBlank()) &&
                (status == null || status.isBlank()) &&
                from == null && to == null;

        if (noFilter) {
            return "redirect:/admin/payments";
        }

        PageResponse<PaymentEntity> res = adminPaymentService.searchPayments(
                orderId, amount, customer, method, status, from, to,
                PageRequest.of(page, size, Sort.by("id").descending()));

        Page<PaymentEntity> pg = res.getContent();

        model.addAttribute("payments", pg.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pg.getTotalPages());
        model.addAttribute("pageSize", pg.getSize());
        model.addAttribute("page", res);
        model.addAttribute("baseUrl", "/admin/payments/search");
        model.addAttribute("isSearch", true);

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
    public String detail(@PathVariable Long id, Model model) {
        try {
            PaymentEntity payment = adminPaymentService.getPaymentById(id);

            var order = payment.getOrderEntity();
            var appointment = order.getAppointmentEntity();
            var patient = appointment != null ? appointment.getPatientEntity() : null;

            model.addAttribute("payment", payment);
            model.addAttribute("order", order);
            model.addAttribute("appointment", appointment);
            model.addAttribute("patient", patient);

            model.addAttribute("baseUrl", "/admin/payments");
            model.addAttribute("isSearch", false);

            return "dashboard/payment-detail";

        } catch (RuntimeException ex) {
            // Trả về trang danh sách kèm thông báo lỗi (hoặc redirect tới trang lỗi custom)
            model.addAttribute("error", "Không tìm thấy thanh toán với ID #" + id);
            return "redirect:/admin/payments?error=notfound";
        }
    }
}
