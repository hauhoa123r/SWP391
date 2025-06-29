package org.project.converter;

import org.project.entity.TestRequestEntity;
import org.project.model.dto.AssignmentListDTO;
import org.project.repository.TestRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AssignmentListConverter {
    private final TestRequestRepository testRequestRepository;
    public AssignmentListConverter(TestRequestRepository testRequestRepository) {
        this.testRequestRepository = testRequestRepository;
    }

    public Page<AssignmentListDTO> getAllAssignmentListPageable(Pageable pageable) {
        Page<TestRequestEntity> page = testRequestRepository.findAll(pageable);
        return page.map(entity -> {
            AssignmentListDTO dto = new AssignmentListDTO();
            dto.setRequestAt(entity.getRequestTime());
            dto.setPatientName(entity.getAppointmentEntity().getPatientEntity().getFullName());
            dto.setDepartmentName(entity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
            dto.setStatus(entity.getRequestStatus().getValue());
            dto.setTestType(entity.getTestType().getTestTypeName());
            dto.setReason(entity.getReason() == null ? "N/A" : entity.getReason());
            return dto;
        });
    }

}
