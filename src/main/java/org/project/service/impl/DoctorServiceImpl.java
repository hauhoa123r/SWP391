package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.converter.DoctorConverter;
import org.project.entity.DoctorEntity;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.DoctorDTO;
import org.project.model.response.DoctorResponse;
import org.project.model.response.StaffResponse;
import org.project.repository.DoctorRepository;
import org.project.repository.impl.custom.DoctorRepositoryCustom;
import org.project.service.DoctorService;
import org.project.utils.PageUtils;
import org.project.utils.specification.PageSpecificationUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private DoctorRepository doctorRepository;
    private DoctorRepositoryCustom doctorRepositoryCustom;
    private DoctorConverter doctorConverter;
    private PageUtils<DoctorEntity> pageUtils;
    private SpecificationUtils<DoctorEntity> specificationUtils;
    private PageSpecificationUtils<DoctorEntity> pageSpecificationUtils;

    @Autowired
    public void setDoctorConverter(DoctorConverter doctorConverter) {
        this.doctorConverter = doctorConverter;
    }

    @Autowired
    public void setDoctorRepository(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Autowired
    public void setDoctorRepositoryCustom(DoctorRepositoryCustom doctorRepositoryCustom) {
        this.doctorRepositoryCustom = doctorRepositoryCustom;
    }

    @Autowired
    public void setPageUtils(PageUtils<DoctorEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<DoctorEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Autowired
    public void setPageSpecificationUtils(PageSpecificationUtils<DoctorEntity> pageCountSpecificationUtils) {
        this.pageSpecificationUtils = pageCountSpecificationUtils;
    }

    @Override
    public Page<DoctorResponse> getAll(int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(pageable);
        pageUtils.validatePage(doctorEntityPage, DoctorResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public Page<DoctorResponse> getDoctors(DoctorDTO doctorDTO, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        List<SearchCriteria> searchCriterias = List.of(
                new SearchCriteria("staffEntity.fullName", ComparisonOperator.CONTAINS, doctorDTO.getStaffEntityFullName(), JoinType.LEFT),
                new SearchCriteria("staffEntity.departmentEntity.id", ComparisonOperator.EQUALS, doctorDTO.getStaffEntityDepartmentEntityId(), JoinType.LEFT),
                new SearchCriteria("staffEntity.reviewEntities.rating", ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, doctorDTO.getMinStarRating(), JoinType.LEFT)
        );
        List<SortCriteria> sortCriterias = List.of();
        switch (doctorDTO.getSortFieldName()) {
            case "staffEntity.fullName" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.NONE, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
            case "staffEntity.reviewEntities.rating" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.AVG, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
            case "staffEntity.reviewEntities.id" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.COUNT, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
        }

        Page<DoctorEntity> doctorEntityPage = pageSpecificationUtils.getPage(
                specificationUtils.reset()
                        .getSpecifications(searchCriterias, sortCriterias),
                pageable,
                DoctorEntity.class,
                true
        );
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public Page<DoctorResponse> getAllByDepartment(Long departmentId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(departmentId, pageable);
        pageUtils.validatePage(doctorEntityPage, StaffResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public Page<DoctorResponse> getAllByHospitalAndDepartment(Long hospitalId, Long departmentId, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
                departmentId, hospitalId, pageable);
        pageUtils.validatePage(doctorEntityPage, StaffResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public Page<DoctorResponse> searchAllByHospitalAndDepartmentAndKeyword(Long hospitalId, Long departmentId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdAndStaffEntityFullNameContainingOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
                departmentId, hospitalId, keyword, pageable);
        pageUtils.validatePage(doctorEntityPage, StaffResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public List<DoctorResponse> getColleagueDoctorsByDepartment(Long departmentId, Long doctorId) {
        List<DoctorEntity> doctorEntities = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndIdNotEqualsOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(
                departmentId, doctorId);
        if (doctorEntities.isEmpty()) {
            throw new EntityNotFoundException(DoctorEntity.class, doctorId);
        }

        return doctorEntities.stream()
                .map(doctorConverter::toResponse)
                .toList();
    }

    @Override
    public DoctorResponse getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .map(doctorConverter::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(DoctorEntity.class, doctorId));
    }

    @Override
    public List<DoctorResponse> getAllByCriteria(DoctorDTO doctorDTO, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        List<SearchCriteria> searchCriterias = List.of(
                new SearchCriteria("staffEntity.fullName", ComparisonOperator.CONTAINS, doctorDTO.getStaffEntityFullName(), JoinType.LEFT),
                new SearchCriteria("staffEntity.departmentEntity.id", ComparisonOperator.EQUALS, doctorDTO.getStaffEntityDepartmentEntityId(), JoinType.LEFT),
                new SearchCriteria("staffEntity.reviewEntities", ComparisonOperator.GREATER_THAN_OR_EQUAL_TO, doctorDTO.getMinStarRating(), JoinType.LEFT)
        );
        List<SortCriteria> sortCriterias = List.of();
        switch (doctorDTO.getSortFieldName()) {
            case "staffEntity.fullName" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.NONE, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
            case "staffEntity.reviewEntities.rating" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.AVG, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
            case "staffEntity.reviewEntities.id" -> sortCriterias = List.of(
                    new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.COUNT, doctorDTO.getSortDirection(), JoinType.LEFT)
            );
        }
        Page<DoctorEntity> doctorEntityPage = doctorRepository.findAll(
                specificationUtils.reset()
                        .getSpecifications(searchCriterias, sortCriterias),
                pageable
        );
        return doctorEntityPage.map(doctorConverter::toResponse).getContent();
    }
}
