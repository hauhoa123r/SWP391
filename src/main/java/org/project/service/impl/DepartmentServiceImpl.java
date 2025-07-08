package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.DepartmentConverter;
import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;
import org.project.model.response.DepartmentResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.impl.custom.DepartmentRepositoryCustom;
import org.project.service.DepartmentService;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final StaffRole DOCTOR_ROLE = StaffRole.DOCTOR;

    private DepartmentRepository departmentRepository;
    private DepartmentRepositoryCustom departmentRepositoryCustom;
    private DepartmentConverter departmentConverter;
    private PageUtils<DepartmentEntity> pageUtils;

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setDepartmentConverter(DepartmentConverter departmentConverter) {
        this.departmentConverter = departmentConverter;
    }

    @Autowired
    public void setDepartmentRepositoryCustom(DepartmentRepositoryCustom departmentRepositoryCustom) {
        this.departmentRepositoryCustom = departmentRepositoryCustom;
    }

    @Autowired
    public void setPageUtils(PageUtils<DepartmentEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public List<DepartmentResponse> getAll() {
        List<DepartmentEntity> departmentEntities = departmentRepository.findAll();
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }

    @Override
    public Page<DepartmentResponse> getAllHaveDoctorByHospital(Long hospitalId, int pageIndex, int pageSize) {
        Pageable pageable = pageUtils.getPageable(pageIndex, pageSize);
        Page<DepartmentEntity> departmentEntityPage = departmentRepositoryCustom.findAllByStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(DOCTOR_ROLE, hospitalId, pageable);
        pageUtils.validatePage(departmentEntityPage, DepartmentEntity.class);
        return departmentEntityPage.map(departmentConverter::toResponse);
    }

    @Override
    public Page<DepartmentResponse> getAllHaveDoctorByHospitalAndKeyword(Long hospitalId, String keyword, int pageIndex, int pageSize) {
        Pageable pageable = pageUtils.getPageable(pageIndex, pageSize);
        Page<DepartmentEntity> departmentEntityPage = departmentRepositoryCustom.findAllByNameContainingAndStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(keyword, DOCTOR_ROLE, hospitalId, pageable);
        pageUtils.validatePage(departmentEntityPage, DepartmentEntity.class);
        return departmentEntityPage.map(departmentConverter::toResponse);
    }

    @Override
    public List<DepartmentResponse> getAllHaveDoctor() {
        List<DepartmentEntity> departmentEntities = departmentRepositoryCustom.findAllByStaffEntitiesStaffRole(DOCTOR_ROLE);
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }

    @Override
    public boolean isDepartmentNameExist(String departmentName) {
        return departmentRepository.existsByName(departmentName);
    }
}
