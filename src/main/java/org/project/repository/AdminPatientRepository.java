package org.project.repository;

import org.project.entity.PatientEntity;
import org.project.enums.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminPatientRepository extends JpaRepository<PatientEntity, Long>, JpaSpecificationExecutor<PatientEntity> {
    org.springframework.data.domain.Page<PatientEntity> findByFullNameContainingIgnoreCase(String name, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<PatientEntity> findByEmailContainingIgnoreCase(String email, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<PatientEntity> findByPhoneNumberContainingIgnoreCase(String phone, org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<PatientEntity> findByAddressContainingIgnoreCase(String address, org.springframework.data.domain.Pageable pageable);
    Page<PatientEntity> findByPatientStatus(PatientStatus status, Pageable pageable);


    org.springframework.data.domain.Page<PatientEntity> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String fullName,
            String email,
            String phone,
            org.springframework.data.domain.Pageable pageable);

}