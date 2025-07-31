package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.OrderEntity;
import org.project.entity.PaymentEntity;
import org.project.service.OrderService;
import org.project.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @GetMapping
    public String listOrders(Model model) {
        // TODO: Implement order listing with pagination
        model.addAttribute("orders", List.of()); // Placeholder
        return "admin/orders";
    }

    @GetMapping("/{orderId}")
    public String viewOrder(@PathVariable Long orderId, Model model) {
        try {
            // TODO: Implement get order by ID
            // OrderEntity order = orderService.getOrderById(orderId);
            
            PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
            
            if (payment != null) {
                model.addAttribute("payment", payment);
                model.addAttribute("orderId", orderId);
                
                // Xác định trạng thái thanh toán
                String paymentStatus = payment.getStatus();
                String statusMessage = "";
                String statusClass = "";
                
                switch (paymentStatus.toUpperCase()) {
                    case "PENDING":
                        statusMessage = "Chờ thanh toán";
                        statusClass = "text-warning";
                        break;
                    case "PAID":
                    case "SUCCESS":
                        statusMessage = "Đã thanh toán";
                        statusClass = "text-success";
                        break;
                    case "CANCELLED":
                        statusMessage = "Đã hủy";
                        statusClass = "text-danger";
                        break;
                    default:
                        statusMessage = paymentStatus;
                        statusClass = "text-info";
                }
                
                model.addAttribute("paymentStatus", statusMessage);
                model.addAttribute("statusClass", statusClass);
            }
            
            return "admin/order-detail";
            
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải thông tin đơn hàng: " + e.getMessage());
            return "admin/error";
        }
    }

    @PostMapping("/{orderId}/confirm-payment")
    public String confirmPayment(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
            if (payment != null) {
                paymentService.updatePaymentStatus(payment.getId(), "PAID");
                redirectAttributes.addFlashAttribute("success", "Đã xác nhận thanh toán thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin thanh toán!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + orderId;
    }

    @PostMapping("/{orderId}/cancel-payment")
    public String cancelPayment(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
            if (payment != null) {
                paymentService.updatePaymentStatus(payment.getId(), "CANCELLED");
                redirectAttributes.addFlashAttribute("success", "Đã hủy thanh toán!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin thanh toán!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hủy thanh toán: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + orderId;
    }
}