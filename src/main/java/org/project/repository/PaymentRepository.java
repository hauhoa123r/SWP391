package org.project.repository;

import org.project.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long>, JpaSpecificationExecutor<PaymentEntity> {
    
    /**
     * Tìm payment theo order ID
     * 
     * @param orderId ID của order
     * @return Optional<PaymentEntity>
     */
    Optional<PaymentEntity> findByOrderEntity_Id(Long orderId);
}
