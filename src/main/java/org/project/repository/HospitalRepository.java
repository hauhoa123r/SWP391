package org.project.repository;

import org.project.entity.HospitalEntity;
import org.project.enums.HospitalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HospitalRepository extends JpaRepository<HospitalEntity, Long>, JpaSpecificationExecutor<HospitalEntity> {
    HospitalEntity findByNameContaining(String hospitalName);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    HospitalEntity findByIdAndHospitalStatus(Long id, HospitalStatus hospitalStatus);

    Page<HospitalEntity> findAllByNameContainingIgnoreCaseAndHospitalStatus(String name, HospitalStatus hospitalStatus, Pageable pageable);

    Page<HospitalEntity> findAllByHospitalStatus(HospitalStatus hospitalStatus, Pageable pageable);

    List<HospitalEntity> findAllByHospitalStatus(HospitalStatus hospitalStatus);

    boolean existsByIdAndHospitalStatus(Long id, HospitalStatus hospitalStatus);
}
