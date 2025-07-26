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

    @Query(value = "SELECT * FROM patients WHERE patient_id = :id", nativeQuery = true)
    Optional<Patient> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM patients WHERE deleted = true", nativeQuery = true)
    Page<Patient> findAllDeleted(Pageable pageable);
}
