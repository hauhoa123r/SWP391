package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.converter.DoctorConverter;
import org.project.entity.*;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.DoctorDTO;
import org.project.model.response.DoctorResponse;
import org.project.model.response.StaffResponse;
import org.project.repository.DoctorRepository;
import org.project.repository.impl.custom.DoctorRepositoryCustom;
import org.project.service.DoctorService;
import org.project.utils.FieldNameUtils;
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
import java.util.Map;
import java.util.Optional;

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
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.fullName), ComparisonOperator.CONTAINS, doctorDTO.getStaffEntityFullName(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.departmentEntity, DepartmentEntity.Fields.id), ComparisonOperator.EQUALS, doctorDTO.getStaffEntityDepartmentEntityId(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, doctorDTO.getMinStarRating(), JoinType.LEFT));
        List<SortCriteria> sortCriterias = List.of();
        Map<String, SortCriteria> sortFieldMap = Map.of(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.fullName), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.NONE, doctorDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.AVG, doctorDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.id), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.COUNT, doctorDTO.getSortDirection(), JoinType.LEFT));
        if (sortFieldMap.containsKey(Optional.of(doctorDTO).map(DoctorDTO::getSortFieldName).orElse(""))) {
            sortCriterias = List.of(sortFieldMap.get(doctorDTO.getSortFieldName()));
        }

        Page<DoctorEntity> doctorEntityPage = pageSpecificationUtils.getPage(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable, DoctorEntity.class, true);
        pageUtils.validatePage(doctorEntityPage, DoctorResponse.class);
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
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(departmentId, hospitalId, pageable);
        pageUtils.validatePage(doctorEntityPage, StaffResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public Page<DoctorResponse> searchAllByHospitalAndDepartmentAndKeyword(Long hospitalId, Long departmentId, String keyword, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdAndStaffEntityFullNameContainingOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(departmentId, hospitalId, keyword, pageable);
        pageUtils.validatePage(doctorEntityPage, StaffResponse.class);
        return doctorEntityPage.map(doctorConverter::toResponse);
    }

    @Override
    public List<DoctorResponse> getColleagueDoctorsByDepartment(Long departmentId, Long doctorId) {
        List<DoctorEntity> doctorEntities = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdAndIdNotEqualsOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(departmentId, doctorId);
        if (doctorEntities.isEmpty()) {
            throw new EntityNotFoundException(DoctorEntity.class, doctorId);
        }

        return doctorEntities.stream().map(doctorConverter::toResponse).toList();
    }

    @Override
    public DoctorResponse getDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId).map(doctorConverter::toResponse).orElseThrow(() -> new EntityNotFoundException(DoctorEntity.class, doctorId));
    }

    @Override
    public List<DoctorResponse> getAllByCriteria(DoctorDTO doctorDTO, int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.fullName), ComparisonOperator.CONTAINS, doctorDTO.getStaffEntityFullName(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.departmentEntity, DepartmentEntity.Fields.id), ComparisonOperator.EQUALS, doctorDTO.getStaffEntityDepartmentEntityId(), JoinType.LEFT), new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), ComparisonOperator.AVG_GREATER_THAN_OR_EQUAL_TO, doctorDTO.getMinStarRating(), JoinType.LEFT));
        List<SortCriteria> sortCriterias = List.of();
        Map<String, SortCriteria> sortFieldMap = Map.of(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.fullName), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.NONE, doctorDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.AVG, doctorDTO.getSortDirection(), JoinType.LEFT), FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.id), new SortCriteria(doctorDTO.getSortFieldName(), AggregationFunction.COUNT, doctorDTO.getSortDirection(), JoinType.LEFT));
        if (sortFieldMap.containsKey(Optional.of(doctorDTO).map(DoctorDTO::getSortFieldName).orElse(""))) {
            sortCriterias = List.of(sortFieldMap.get(doctorDTO.getSortFieldName()));
        }
        Page<DoctorEntity> doctorEntityPage = doctorRepository.findAll(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable);
        return doctorEntityPage.map(doctorConverter::toResponse).getContent();
    }

    @Override
    public List<DoctorResponse> getTop6DoctorsByDepartment(Long departmentId) {
        Pageable pageable = pageUtils.getPageable(0, 6);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllByStaffEntityDepartmentEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(departmentId, pageable);
        pageUtils.validatePage(doctorEntityPage, DoctorEntity.class);
        return doctorEntityPage.stream().map(doctorConverter::toResponse).toList();
    }

    @Override
    public List<DoctorResponse> getTop6DoctorsByHospital(Long hospitalId) {
        Pageable pageable = pageUtils.getPageable(0, 6);
        List<SearchCriteria> searchCriterias = List.of(new SearchCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.hospitalEntity, HospitalEntity.Fields.id), ComparisonOperator.EQUALS, hospitalId, JoinType.LEFT));
        List<SortCriteria> sortCriterias = List.of(new SortCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.rating), AggregationFunction.AVG, SortDirection.DESC, JoinType.LEFT), new SortCriteria(FieldNameUtils.joinFields(DoctorEntity.Fields.staffEntity, StaffEntity.Fields.reviewEntities, ReviewEntity.Fields.id), AggregationFunction.COUNT, SortDirection.DESC, JoinType.LEFT));

        Page<DoctorEntity> doctorEntityPage = pageSpecificationUtils.getPage(specificationUtils.reset().getSpecifications(searchCriterias, sortCriterias), pageable, DoctorEntity.class, true);
        return doctorEntityPage.stream().map(doctorConverter::toResponse).toList();
    }

    public List<DoctorResponse> getTopDoctors(int top) {
        Pageable pageable = pageUtils.getPageable(0, top);
        Page<DoctorEntity> doctorEntityPage = doctorRepository.findTopOrderByStaffEntityAverageRatingDescAndStaffEntityReviewCountDesc(pageable);
        return doctorEntityPage.stream().map(doctorConverter::toResponse).toList();
    }

    @Override
    public DoctorResponse getTop1Doctor() {
        List<DoctorResponse> topDoctors = getTopDoctors(1);
        if (topDoctors.isEmpty()) {
            return null;
        }
        return topDoctors.get(0);
    }

    @Override
    public List<DoctorResponse> getTop6Doctors() {
        return getTopDoctors(6);
    }
}
