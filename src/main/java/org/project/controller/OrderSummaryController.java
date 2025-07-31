//package org.project.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.project.entity.PaymentEntity;
//import org.project.service.PaymentService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.math.BigDecimal;
//
//@Controller
//@RequestMapping("/orders")
//@RequiredArgsConstructor
//public class OrderSummaryController {
//
//    private final PaymentService paymentService;
//
//    @GetMapping("/summary")
//    public String showOrderSummary(@RequestParam Long orderId, Model model) {
//        try {
//            // Lấy thông tin payment từ orderId
//            PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
//
//            if (payment != null) {
//                model.addAttribute("orderId", orderId);
//                model.addAttribute("totalAmount", payment.getAmount());
//                model.addAttribute("paymentMethod", payment.getMethod());
//                model.addAttribute("paymentStatus", payment.getStatus());
//                model.addAttribute("payment", payment);
//
//                // Xác định trạng thái thanh toán
//                String statusMessage = "";
//                String statusClass = "";
//
//                switch (payment.getStatus().toUpperCase()) {
//                    case "PENDING":
//                        statusMessage = "Chờ thanh toán";
//                        statusClass = "text-warning";
//                        break;
//                    case "PAID":
//                    case "SUCCESS":
//                        statusMessage = "Đã thanh toán";
//                        statusClass = "text-success";
//                        break;
//                    case "CANCELLED":
//                        statusMessage = "Đã hủy";
//                        statusClass = "text-danger";
//                        break;
//                    default:
//                        statusMessage = payment.getStatus();
//                        statusClass = "text-info";
//                }
//
//                model.addAttribute("paymentStatus", statusMessage);
//                model.addAttribute("statusClass", statusClass);
//            } else {
//                // Fallback nếu không tìm thấy payment
//                model.addAttribute("orderId", orderId);
//                model.addAttribute("totalAmount", new BigDecimal("100000"));
//                model.addAttribute("paymentMethod", "CASH");
//                model.addAttribute("paymentStatus", "Chờ thanh toán");
//            }
//
//            return "order-received"; // Sử dụng template có sẵn
//        } catch (Exception e) {
//            model.addAttribute("error", "Lỗi khi tải thông tin đơn hàng: " + e.getMessage());
//            return "order-received"; // Sử dụng template có sẵn
//        }
//    }
//}