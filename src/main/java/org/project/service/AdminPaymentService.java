package org.project.service;

import org.project.entity.PaymentEntity;
import org.project.model.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AdminPaymentService {

    List<PaymentEntity> getAllPayments();

    PageResponse<PaymentEntity> getAllPayments(Pageable pageable);

    PageResponse<PaymentEntity> getPaymentPage(Pageable pageable);

    PaymentEntity getPaymentById(Long id);

    PageResponse<PaymentEntity> searchPayments(Long orderId, BigDecimal amount, LocalDate fromDate, LocalDate toDate, Pageable pageable);

}
