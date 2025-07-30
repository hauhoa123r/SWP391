package org.project.repository;

import org.project.entity.TestRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TestRequestRepository extends JpaRepository<TestRequestEntity, Long> {
    List<TestRequestEntity> findByAppointmentEntityId(Long id);

    Set<TestRequestEntity> findByAppointmentEntity_Id(Long appointmentEntityId);
}