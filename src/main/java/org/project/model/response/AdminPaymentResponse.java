package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.project.enums.PaymentStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO trả về cho tầng controller (REST hoặc Thymeleaf) khi hiển thị thông tin payment trong trang quản trị.
 * Chỉ bao gồm các trường cần thiết cho UI, tránh expose toàn bộ entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPaymentResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private Timestamp paymentTime;
    private PaymentStatus paymentStatus;
    // Có thể thêm method, status, v.v nếu database có cột.

}
