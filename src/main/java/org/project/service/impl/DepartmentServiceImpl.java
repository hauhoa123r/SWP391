package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
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
import org.project.service.ProductService;
import org.project.service.StaffService;
import org.project.utils.MergeObjectUtils;
import org.project.utils.PageUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final StaffRole DOCTOR_ROLE = StaffRole.DOCTOR;

    private StaffService staffService;
    private ProductService productService;
    private DepartmentRepository departmentRepository;
    private DepartmentRepositoryCustom departmentRepositoryCustom;
    private DepartmentConverter departmentConverter;
    private PageUtils<DepartmentEntity> pageUtils;
    private SpecificationUtils<DepartmentEntity> specificationUtils;

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

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
        DepartmentEntity departmentEntity = departmentRepository.findByIdAndDepartmentStatus(id, WebConstant.DEPARTMENT_STATUS_ACTIVE);
        if (departmentEntity == null) {
            throw new EntityNotFoundException(DepartmentEntity.class, id);
        }
        return departmentConverter.toResponse(departmentEntity);
    }

    @Override
    public List<DepartmentResponse> getDepartments() {
        List<DepartmentEntity> departmentEntities = departmentRepository.findAllByDepartmentStatus(WebConstant.DEPARTMENT_STATUS_ACTIVE);
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
                .getSearchSpecifications(
                        new SearchCriteria(DepartmentEntity.Fields.name, ComparisonOperator.CONTAINS, departmentDTO.getName(), JoinType.INNER),
                        new SearchCriteria("slogan", ComparisonOperator.CONTAINS, departmentDTO.getSlogan(), JoinType.INNER),
                        new SearchCriteria("departmentStatus", ComparisonOperator.EQUALS, WebConstant.DEPARTMENT_STATUS_ACTIVE)), pageable);
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
        return departmentRepository.existsByNameAndDepartmentStatus(departmentName, WebConstant.DEPARTMENT_STATUS_ACTIVE);
    }

    @Override
    public List<DepartmentResponse> getAll() {
        List<DepartmentEntity> departmentEntities = departmentRepository.findAllByDepartmentStatus(WebConstant.DEPARTMENT_STATUS_ACTIVE);
        return departmentEntities.stream().map(departmentConverter::toResponse).toList();
    }

    @Override
    public void createDepartment(DepartmentDTO departmentDTO) {
        DepartmentEntity departmentEntity = departmentConverter.toEntity(departmentDTO);
        departmentEntity.setDepartmentStatus(WebConstant.DEPARTMENT_STATUS_ACTIVE);
        departmentRepository.save(departmentEntity);
    }

    @Override
    public void updateDepartment(Long id, DepartmentDTO departmentDTO) {
        DepartmentEntity target = departmentRepository.findByIdAndDepartmentStatus(id, WebConstant.DEPARTMENT_STATUS_ACTIVE);
        if (target == null) {
            throw new EntityNotFoundException(DepartmentEntity.class, id);
        }
        DepartmentEntity source = departmentConverter.toEntity(departmentDTO);
        MergeObjectUtils.mergeNonNullFields(source, target);
        departmentRepository.save(target);
    }

    @Override
    public void deleteDepartment(Long id) {
        DepartmentEntity departmentEntity = departmentRepository.findByIdAndDepartmentStatus(id, WebConstant.DEPARTMENT_STATUS_ACTIVE);
        if (departmentEntity == null) {
            throw new EntityNotFoundException(DepartmentEntity.class, id);
        }
        departmentEntity.getServiceEntities().forEach(serviceEntity -> {
            productService.deleteProduct(serviceEntity.getProductEntity().getId());
        });
        departmentEntity.getStaffEntities().forEach(staffEntity -> {
            staffService.deleteStaff(staffEntity.getId());
        });
        departmentEntity.setDepartmentStatus(WebConstant.DEPARTMENT_STATUS_INACTIVE);
    }
}
