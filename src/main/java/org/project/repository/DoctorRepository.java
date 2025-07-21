package org.project.repository;

import org.project.entity.DoctorEntity;
import org.project.model.response.DoctorResponse;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>, JpaSpecificationExecutor<DoctorEntity> {
    List<DoctorResponse> getTopByStaffEntityDepartmentEntityId(Long staffEntityDepartmentEntityId, Limit limit);
}
