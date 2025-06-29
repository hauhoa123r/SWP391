package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.PaymentEntity;
import org.project.model.response.PageResponse;
import org.project.repository.AdminPaymentRepository;
import org.project.service.AdminPaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public PageResponse<PaymentEntity> searchPayments(Long orderId, BigDecimal amount, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Specification<PaymentEntity> spec = // Khởi tạo điều kiện mặc định "luôn đúng" (cb.conjunction()).
                (root, query, cb) -> cb.conjunction();

        if (orderId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("orderEntity").get("id"), orderId));
        }
        if (amount != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("amount"), amount));
        }
        if (fromDate != null || toDate != null) {
            Timestamp from = fromDate != null ? Timestamp.valueOf(fromDate.atStartOfDay()) : Timestamp.valueOf(LocalDate.of(1970,1,1).atStartOfDay());
            Timestamp to   = toDate != null ? Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()) : Timestamp.valueOf(LocalDate.of(3000,1,1).atStartOfDay());
            spec = spec.and((root, q, cb) -> cb.between(root.get("paymentTime"), from, to));
        }
        Page<PaymentEntity> page = adminPaymentRepository.findAll(spec, pageable);
        return new PageResponse<>(page);
    }
}
