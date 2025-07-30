package org.project.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Builder
@Data
public class OrderSummaryDTO {
    private Long orderId;
    private String customerName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate birthdate;
    private String note;

    private BigDecimal totalAmount;
    private BigDecimal realAmount;
    private String couponCode;
    private List<OrderItemDTO> items;
}
