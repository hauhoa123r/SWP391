package org.project.service;

import lombok.RequiredArgsConstructor;
import org.project.entity.PaymentEntity;
import org.project.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * PaymentService handles all business logic related to PaymentEntity.
 * <p>
 * It is used by both normal user flows and admin dashboard.  Methods return
 * Spring Data {@code Page<PaymentEntity>} so the caller can decide how to
 * render / transform the data.
 * </p>
 */
@Service
@RequiredArgsConstructor
/**
 * Service xử lý toàn bộ nghiệp vụ liên quan tới bảng <b>payments</b>.
 * Dùng chung cho cả phần frontend người dùng lẫn dashboard quản trị.
 * Các phương thức trả về {@code Page<PaymentEntity>} để caller tự quyết định
 * hiển thị hoặc ánh xạ sang DTO.
 */
public class PaymentService {

    private final PaymentRepository repo;

    /**
     * Fetch all payments with paging.
     *
     * @param pageable page info (page index, size, sort)
     * @return page of payments
     */
    /**
     * Lấy danh sách thanh toán có phân trang.
     *
     * @param pageable thông tin trang (số trang, kích thước, sắp xếp)
     * @return Page<PaymentEntity> chứa kết quả truy vấn
     */
    public Page<PaymentEntity> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /**
     * Search payments with optional filters. Any parameter can be {@code null}
     * and will be ignored.
     *
     * @param orderId  filter by order id
     * @param amount   filter by exact amount
     * @param fromDate filter paymentTime >= fromDate (inclusive)
     * @param toDate   filter paymentTime < toDate + 1 day (exclusive)
     * @param pageable paging info
     * @return page of filtered payments
     */
    /**
     * Tìm kiếm thanh toán với các tiêu chí tuỳ chọn.
     * Tham số nào null sẽ được bỏ qua.
     *
     * @param orderId  lọc theo mã đơn hàng
     * @param amount   lọc theo số tiền chính xác
     * @param fromDate ngày bắt đầu (>=)
     * @param toDate   ngày kết thúc (<=)
     * @param pageable phân trang
     * @return Page<PaymentEntity> kết quả sau khi áp dụng bộ lọc
     */
    public Page<PaymentEntity> search(Long orderId,
                                      BigDecimal amount,
                                      LocalDate fromDate,
                                      LocalDate toDate,
                                      Pageable pageable) {

        // Khởi tạo điều kiện mặc định "luôn đúng" (cb.conjunction()).
        Specification<PaymentEntity> spec = (root, query, cb) -> cb.conjunction();

        if (orderId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("orderEntity").get("id"), orderId));
        }
        if (amount != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("amount"), amount));
        }
        if (fromDate != null || toDate != null) {
            Timestamp from = fromDate != null ? Timestamp.valueOf(fromDate.atStartOfDay()) : Timestamp.valueOf(LocalDate.of(1970,1,1).atStartOfDay());
            Timestamp to   = toDate   != null ? Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()) : Timestamp.valueOf(LocalDate.of(3000,1,1).atStartOfDay());
            spec = spec.and((root, q, cb) -> cb.between(root.get("paymentTime"), from, to));
        }
        return repo.findAll(spec, pageable);
    }
}
