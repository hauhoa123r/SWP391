package org.project.repository;

import org.project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT COALESCE(SUM(o.realAmount), 0) FROM Order o " +
           "WHERE DATE(o.orderStatus) BETWEEN :from AND :to")
    BigDecimal getTotalRevenueBetween(LocalDate from, LocalDate to);
}
