package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.StaffConverter;
import org.project.entity.StaffEntity;
import org.project.enums.StaffRole;
import org.project.model.response.StaffResponse;
import org.project.repository.StaffRepository;
import org.project.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    private StaffRepository staffRepository;
    private StaffConverter staffConverter;
    private final StaffRole DOCTOR_ROLE = StaffRole.DOCTOR;

    @Autowired
    public void setStaffRepository(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Autowired
    public void setStaffConverter(StaffConverter staffConverter) {
        this.staffConverter = staffConverter;
    }

    @Override
    public Page<StaffResponse> getDoctorsByPage(int index, int size) {
        Pageable pageable = PageRequest.of(index, size);
        Page<StaffEntity> staffEntityPage = staffRepository.findAllByStaffRole(DOCTOR_ROLE, pageable);
        return staffEntityPage.map(staffConverter::toResponse);
    }

    @Override
    public Page<StaffResponse> getDoctorsByDepartmentNameAndPage(String departmentName, int index, int size) {
        Pageable pageable = PageRequest.of(index, size);
        Page<StaffEntity> staffEntityPage = staffRepository.findAllByStaffRoleAndDepartmentEntityName(DOCTOR_ROLE, departmentName, pageable);
        return staffEntityPage.map(staffConverter::toResponse);
    }

    @Override
    public List<StaffResponse> getColleagueDoctorByStaffId(String departmentName, Long staffId) {
        List<StaffEntity> staffEntities = staffRepository.findAllByStaffRoleAndDepartmentEntityNameAndIdIsNot(
                DOCTOR_ROLE, departmentName, staffId);
        return staffEntities.stream().map(staffConverter::toResponse).toList();
    }

    @Override
    public boolean isDoctorExist(Long staffId) {
        return staffRepository.existsByStaffRoleAndId(DOCTOR_ROLE, staffId);
    }

    @Override
    public StaffResponse getDoctorByStaffId(Long staffId) {
        return staffConverter.toResponse(staffRepository.findByStaffRoleAndId(DOCTOR_ROLE, staffId));
    }
}
