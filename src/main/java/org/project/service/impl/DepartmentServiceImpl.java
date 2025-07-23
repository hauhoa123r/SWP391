package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.converter.DepartmentConverter;
import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.DepartmentDTO;
import org.project.model.response.DepartmentResponse;
import org.project.repository.DepartmentRepository;
import org.project.repository.impl.custom.DepartmentRepositoryCustom;
import org.project.service.DepartmentService;
import org.project.utils.PageUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final StaffRole DOCTOR_ROLE = StaffRole.DOCTOR;

    private DepartmentRepository departmentRepository;
    private DepartmentRepositoryCustom departmentRepositoryCustom;
    private DepartmentConverter departmentConverter;
    private PageUtils<DepartmentEntity> pageUtils;
    private SpecificationUtils<DepartmentEntity> specificationUtils;

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

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<DepartmentEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Override
    public DepartmentResponse getDepartment(Long id) {
        DepartmentEntity departmentEntity = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DepartmentEntity.class, id));
        return departmentConverter.toResponse(departmentEntity);
    }

    @Override
    public List<DepartmentResponse> getDepartments() {
        List<DepartmentEntity> departmentEntities = departmentRepository.findAll();
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }

    @Override
    public Page<DepartmentResponse> getDepartments(int pageIndex, int pageSize, DepartmentDTO departmentDTO) {
        Sort sort = Sort.unsorted();
        if (departmentDTO.getSortFieldName() != null && !departmentDTO.getSortFieldName().isEmpty()) {
            sort = Sort.by(new Sort.Order(SortDirection.ASC == departmentDTO.getSortDirection() ? Sort.Direction.ASC : Sort.Direction.DESC, departmentDTO.getSortFieldName()));
        }
        Pageable pageable = pageUtils.getPageable(pageIndex, pageSize, sort);
        Page<DepartmentEntity> departmentEntityPage = departmentRepository.findAll(specificationUtils.reset()
                .getSearchSpecification(new SearchCriteria(DepartmentEntity.Fields.name, ComparisonOperator.CONTAINS, departmentDTO.getName(), JoinType.INNER)), pageable);
        pageUtils.validatePage(departmentEntityPage, DepartmentEntity.class);
        return departmentEntityPage.map(departmentConverter::toResponse);
    }

    @Override
    public Page<DepartmentResponse> getDepartmentsHaveDoctorByHospital(Long hospitalId, int pageIndex, int pageSize) {
        Pageable pageable = pageUtils.getPageable(pageIndex, pageSize);
        Page<DepartmentEntity> departmentEntityPage = departmentRepositoryCustom.findAllByStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(DOCTOR_ROLE, hospitalId, pageable);
        pageUtils.validatePage(departmentEntityPage, DepartmentEntity.class);
        return departmentEntityPage.map(departmentConverter::toResponse);
    }

    @Override
    public Page<DepartmentResponse> getDepartmentsHaveDoctorByHospitalAndKeyword(Long hospitalId, String keyword, int pageIndex, int pageSize) {
        Pageable pageable = pageUtils.getPageable(pageIndex, pageSize);
        Page<DepartmentEntity> departmentEntityPage = departmentRepositoryCustom.findAllByNameContainingAndStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(keyword, DOCTOR_ROLE, hospitalId, pageable);
        pageUtils.validatePage(departmentEntityPage, DepartmentEntity.class);
        return departmentEntityPage.map(departmentConverter::toResponse);
    }

    @Override
    public List<DepartmentResponse> getDepartmentsHaveDoctor() {
        List<DepartmentEntity> departmentEntities = departmentRepositoryCustom.findAllByStaffEntitiesStaffRole(DOCTOR_ROLE);
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }

    @Override
    public boolean isDepartmentNameExist(String departmentName) {
        return departmentRepository.existsByName(departmentName);
    }

    @Override
    public List<DepartmentResponse> getAll() {
        List<DepartmentEntity> departmentEntities = departmentRepository.findAll();
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }
}
