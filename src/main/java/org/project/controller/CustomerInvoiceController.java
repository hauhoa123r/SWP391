package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.OrderItemEntity;
import org.project.entity.OrderItemEntityId;
import org.project.entity.UserEntity;
import org.project.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer-invoice")
@RequiredArgsConstructor
public class CustomerInvoiceController {

    private final OrderItemService orderItemService;

    @GetMapping
    public String getCustomerInvoicePage(Model model) {
        List<OrderItemEntity> orderItems = orderItemService.findAll();
        
        // Calculate statistics
        int totalInvoices = orderItems.size();
        
        // Tính totalRevenue
        BigDecimal totalRevenue = orderItems.stream()
                .filter(item -> item != null && item.getProductEntity() != null)
                .map(item -> {
                    BigDecimal price = item.getProductEntity().getPrice();
                    int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    return price != null ? price.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Đếm số lượng khách hàng duy nhất
        long totalCustomers = orderItems.stream()
                .filter(item -> item != null && item.getOrderEntity() != null && 
                       item.getOrderEntity().getAppointmentEntity() != null &&
                       item.getOrderEntity().getAppointmentEntity().getPatientEntity() != null)
                .map(item -> item.getOrderEntity().getAppointmentEntity().getPatientEntity().getId())
                .distinct()
                .count();
        
        // Đếm số lượng đơn hàng chưa thanh toán
        long pendingPayments = orderItems.stream()
                .filter(item -> item != null && item.getOrderEntity() != null &&
                       (item.getOrderEntity().getPaymentEntities() == null || 
                        item.getOrderEntity().getPaymentEntities().isEmpty()))
                .count();
        
        // Danh sách khách hàng
        List<UserEntity> customers = new ArrayList<>();
        
        model.addAttribute("invoices", orderItems);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("pendingPayments", pendingPayments);
        model.addAttribute("customers", customers);
        
        return "customer-invoice";
    }

    @GetMapping("/{orderId}/{productId}")
    public String getCustomerInvoiceDetails(@PathVariable Long orderId, @PathVariable Long productId, Model model) {
        try {
            OrderItemEntityId id = new OrderItemEntityId();
            id.setOrderId(orderId);
            id.setProductId(productId);
            
            OrderItemEntity orderItem = orderItemService.findById(id);
            if (orderItem == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
            }
            model.addAttribute("orderItem", orderItem);
            return "customer-invoice-details";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading invoice: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/mark-paid/{invoiceNumber}")
    @ResponseBody
    public ResponseEntity<?> markAsPaid(@PathVariable String invoiceNumber) {
        try {
            OrderItemEntity orderItem = orderItemService.findByInvoiceNumber(invoiceNumber);
            
            if (orderItem == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invoice not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Kiểm tra đơn hàng có thể thanh toán
            if (orderItem.getOrderEntity() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid order item");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Đánh dấu đơn hàng đã thanh toán (cần thêm logic phù hợp)
            // Đây chỉ là ví dụ, bạn cần cập nhật theo cấu trúc thực tế của hệ thống
            
            orderItemService.save(orderItem);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/print/{invoiceNumber}")
    public String printInvoice(@PathVariable String invoiceNumber, Model model) {
        try {
            OrderItemEntity orderItem = orderItemService.findByInvoiceNumber(invoiceNumber);
            if (orderItem == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invoice not found");
            }
            model.addAttribute("orderItem", orderItem);
            return "customer-invoice-print";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading invoice: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/export")
    public String exportInvoices() {
        // Logic for exporting invoices to Excel
        // In a real application, this would return a file download
        return "redirect:/customer-invoice";
    }
} 