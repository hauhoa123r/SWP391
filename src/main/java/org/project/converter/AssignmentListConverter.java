package org.project.converter;

import org.project.entity.TestRequestEntity;
import org.project.enums.RequestStatus;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.AssignmentListDTO;
import org.project.repository.AssignmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class AssignmentListConverter {
    private final AssignmentRepository testRequestRepository;
    public AssignmentListConverter(AssignmentRepository testRequestRepository) {
        this.testRequestRepository = testRequestRepository;
    }

    public Page<AssignmentListDTO> getAllAssignmentBySearch(AssignmentListDTO search) throws IllegalAccessException {
        Page<TestRequestEntity> page = testRequestRepository.toGetEntityBySearch(search);

        if (page == null) {
            throw new ResourceNotFoundException("Cam not get assignment by search");
        }

        return page.map(entity -> {
            AssignmentListDTO dto = new AssignmentListDTO();
            dto.setId(entity.getId());
            dto.setDoctorName(entity.getDoctorEntity().getStaffEntity().getFullName());
            dto.setRequestAt(entity.getRequestTime());
            dto.setPatientName(entity.getAppointmentEntity().getPatientEntity().getFullName());
            dto.setDepartmentName(entity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
            dto.setStatus(entity.getRequestStatus().getValue());
            dto.setTestType(entity.getTestType().getTestTypeName());
            dto.setReason(entity.getReason() == null ? "N/A" : entity.getReason());
            return dto;
        });
    }

    public Page<AssignmentListDTO> getAllAssignmentListPageable(Pageable pageable) {
        Page<TestRequestEntity> page = testRequestRepository.findAll(pageable);
        return page.map(entity -> {
            AssignmentListDTO dto = new AssignmentListDTO();
            dto.setId(entity.getId());
            dto.setDoctorName(entity.getDoctorEntity().getStaffEntity().getFullName());
            dto.setRequestAt(entity.getRequestTime());
            dto.setPatientName(entity.getAppointmentEntity().getPatientEntity().getFullName());
            dto.setDepartmentName(entity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
            dto.setStatus(entity.getRequestStatus().getValue());
            dto.setTestType(entity.getTestType().getTestTypeName());
            dto.setReason(entity.getReason() == null ? "N/A" : entity.getReason());
            return dto;
        });
    }

    public Page<AssignmentListDTO> getAllRequestPending(Pageable pageable){
        Page<TestRequestEntity> page = testRequestRepository.findAllByRequestStatus(RequestStatus.pending, pageable);
        return page.map(entity -> {
            AssignmentListDTO dto = new AssignmentListDTO();
            dto.setId(entity.getId());
            dto.setRequestAt(entity.getRequestTime());
            dto.setPatientName(entity.getAppointmentEntity().getPatientEntity().getFullName());
            dto.setDepartmentName(entity.getAppointmentEntity().getDoctorEntity().getStaffEntity().getDepartmentEntity().getName());
            dto.setStatus(entity.getRequestStatus().getValue());
            dto.setTestType(entity.getTestType().getTestTypeName());
            dto.setReason(entity.getReason() == null ? "N/A" : entity.getReason());
            dto.setDoctorName(entity.getDoctorEntity().getStaffEntity().getFullName());
            return dto;
        });
    }

    public Page<AssignmentListDTO> getAllReceivePatient(AssignmentListDTO search) throws IllegalAccessException {
        search.setStatus(RequestStatus.pending.getValue());
        Page<TestRequestEntity> page = testRequestRepository.toGetReceivePatientByStatusPending(search);

        if (page == null) {
            throw new ResourceNotFoundException("Cam not get assignment by search");
        }

        return page.map(entity -> {
            AssignmentListDTO dto = new AssignmentListDTO();
            dto.setId(entity.getId());
            dto.setDoctorName(entity.getDoctorEntity().getStaffEntity().getFullName());
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
