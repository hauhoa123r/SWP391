package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.PaymentEntity;
import org.project.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * API để admin xác nhận thanh toán cash on delivery
     * 
     * @param paymentId ID của payment
     * @return ResponseEntity với thông tin kết quả
     */
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long paymentId) {
        try {
            PaymentEntity payment = paymentService.updatePaymentStatus(paymentId, "PAID");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thanh toán đã được xác nhận thành công");
            response.put("paymentId", payment.getId());
            response.put("status", payment.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi xác nhận thanh toán: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API để admin hủy thanh toán
     * 
     * @param paymentId ID của payment
     * @return ResponseEntity với thông tin kết quả
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId) {
        try {
            PaymentEntity payment = paymentService.updatePaymentStatus(paymentId, "CANCELLED");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thanh toán đã được hủy");
            response.put("paymentId", payment.getId());
            response.put("status", payment.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi hủy thanh toán: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * API để lấy thông tin payment theo order ID
     * 
     * @param orderId ID của order
     * @return ResponseEntity với thông tin payment
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Long orderId) {
        try {
            PaymentEntity payment = paymentService.getPaymentByOrderId(orderId);
            
            if (payment == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy payment cho order này");
                
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", payment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi khi lấy thông tin payment: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}