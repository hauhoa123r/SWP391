package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.PaymentEntity;
import org.project.model.response.PageResponse;
import org.project.repository.AdminPaymentRepository;
import org.project.service.AdminPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * Triển khai chi tiết cho {@link org.project.service.AdminPaymentService}.
 * <p>
 * Lớp này chủ yếu phục vụ dashboard quản trị, trả về {@link org.project.model.response.PageResponse}
 * để bao gồm meta phân trang (totalElements, totalPages, currentPage).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AdminPaymentServiceImpl implements AdminPaymentService {

    private final AdminPaymentRepository adminPaymentRepository;

    @Override
    /**
     * Lấy tất cả bản ghi thanh toán (không phân trang).
     */
    public List<PaymentEntity> getAllPayments() {
        return adminPaymentRepository.findAll();
    }

    @Override
    /**
     * Lấy toàn bộ payment có phân trang; tái sử dụng logic getPaymentPage.
     */
    public PageResponse<PaymentEntity> getAllPayments(Pageable pageable) {
        return getPaymentPage(pageable);
    }

    @Override
    /**
     * Trả về PageResponse bọc dữ liệu phân trang.
     */
    public PageResponse<PaymentEntity> getPaymentPage(Pageable pageable) {
        Page<PaymentEntity> page = adminPaymentRepository.findAll(pageable);
        return new PageResponse<>(page);
    }

    @Override
    /**
     * Tìm payment theo id, ném RuntimeException nếu không tồn tại.
     */
    public PaymentEntity getPaymentById(Long id) {
        return adminPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    /**
     * Tìm kiếm với bộ lọc tương tự PaymentService nhưng trả về PageResponse.
     */
    /**
     * Search payments with dynamic filters and pagination.
     *
     * @param orderId        order id to match (nullable)
     * @param amount         exact amount to match (nullable)
     * @param customerEmail  substring of patient email (case-insensitive, nullable)
     * @param method         payment method (e.g. CASH, VNPAY) (nullable)
     * @param status         payment status (e.g. PAID, REFUND) (nullable)
     * @param fromDate       start date (inclusive) for paymentTime (nullable)
     * @param toDate         end date (inclusive) for paymentTime (nullable)
     * @param pageable       spring pageable (page, size, sort)
     */
    public PageResponse<PaymentEntity> searchPayments(Long orderId, BigDecimal amount,
                                                     String customerEmail,
                                                     String method,
                                                     String status,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     Pageable pageable) {
        // Start with an empty Specification (equivalent to "true" condition)
        Specification<PaymentEntity> spec = Specification.where(null);

        // Áp dụng MỌI bộ lọc được cung cấp (AND)
        if (orderId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("orderEntity").get("id"), orderId));
        }
        if (amount != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("amount"), amount));
        }
        if (customerEmail != null && !customerEmail.isBlank()) {
            spec = spec.and((root, q, cb) -> {
                Join<?, ?> patientJoin = root.join("orderEntity")
                                              .join("appointmentEntity")
                                              .join("patientEntity");
                return cb.like(cb.lower(patientJoin.get("email")), "%" + customerEmail.toLowerCase() + "%");
            });
        }
        if (method != null && !method.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(cb.lower(root.get("method")), method.toLowerCase()));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
        }
        // Filter by paymentTime
        if (fromDate != null || toDate != null) {
            // Nếu chỉ chọn 1 ngày (chỉ from hoặc chỉ to) => lấy trọn ngày đó
            if (fromDate != null && toDate == null) {
                toDate = fromDate; // cùng 1 ngày
            }
            if (toDate != null && fromDate == null) {
                fromDate = toDate;
            }
            Timestamp fromTs = Timestamp.valueOf(fromDate.atStartOfDay());
            Timestamp toTs   = Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()); // exclusive end next day 00:00
            spec = spec.and((root, q, cb) -> cb.between(root.get("paymentTime"), fromTs, toTs));
        }
        Page<PaymentEntity> page = adminPaymentRepository.findAll(spec, pageable);
        return new PageResponse<>(page);
    }
}
