package org.project.repository;

import org.project.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentTime BETWEEN :fromDate AND :toDate")
    BigDecimal sumPaymentsBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    List<Payment> findByPaymentTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
