package org.project.admin.repository;

import org.project.admin.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("adminPatientRepository")
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    // Kiểm tra email đã tồn tại trong hệ thống bệnh nhân (chưa bị xóa)
    boolean existsByEmailAndDeletedFalse(String email);

    // Kiểm tra email trùng với patient khác (không phải patient hiện tại)
    boolean existsByEmailAndDeletedFalseAndPatientIdNot(String email, Long patientId);

    // Kiểm tra số điện thoại đã tồn tại trong hệ thống bệnh nhân (chưa bị xóa)
    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);

    // Kiểm tra số điện thoại trùng với patient khác (không phải patient hiện tại)
    boolean existsByPhoneNumberAndDeletedFalseAndPatientIdNot(String phoneNumber, Long patientId);

    @Query(value = "SELECT * FROM patients WHERE patient_id = :id", nativeQuery = true)
    Optional<Patient> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM patients WHERE deleted = true", nativeQuery = true)
    Page<Patient> findAllDeleted(Pageable pageable);
}
