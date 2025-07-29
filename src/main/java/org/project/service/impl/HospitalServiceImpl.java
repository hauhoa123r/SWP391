package org.project.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import org.project.config.WebConstant;
import org.project.converter.HospitalConverter;
import org.project.entity.HospitalEntity;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.ErrorResponse;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.dto.HospitalDTO;
import org.project.model.response.HospitalResponse;
import org.project.repository.HospitalRepository;
import org.project.service.HospitalService;
import org.project.service.StaffService;
import org.project.utils.MergeObjectUtils;
import org.project.utils.PageUtils;
import org.project.utils.specification.SpecificationUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private StaffService staffService;
    private HospitalRepository hospitalRepository;
    private HospitalConverter hospitalConverter;
    private PageUtils<HospitalEntity> pageUtils;
    private SpecificationUtils<HospitalEntity> specificationUtils;

    @Autowired
    public void setStaffService(StaffService staffService) {
        this.staffService = staffService;
    }

    @Autowired
    public void setHospitalRepository(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Autowired
    public void setHospitalConverter(HospitalConverter hospitalConverter) {
        this.hospitalConverter = hospitalConverter;
    }

    @Autowired
    public void setPageUtils(PageUtils<HospitalEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Autowired
    public void setSpecificationUtils(SpecificationUtils<HospitalEntity> specificationUtils) {
        this.specificationUtils = specificationUtils;
    }

    @Override
    public HospitalResponse getHospital(Long id) {
        HospitalEntity hospitalEntity = hospitalRepository.findByIdAndHospitalStatus(id, WebConstant.HOSPITAL_STATUS_ACTIVE);
        if (hospitalEntity == null) {
            throw new EntityNotFoundException(HospitalEntity.class, id);
        }
        return hospitalConverter.toResponse(hospitalEntity);
    }

    @Override
    public Page<HospitalResponse> getHospitals(int index, int size, HospitalDTO hospitalDTO) {
        Sort sort = Sort.unsorted();
        if (hospitalDTO.getSortFieldName() != null && !hospitalDTO.getSortFieldName().isEmpty()) {
            sort = Sort.by((SortDirection.ASC == hospitalDTO.getSortDirection() ? Sort.Direction.ASC : Sort.Direction.DESC), hospitalDTO.getSortFieldName());
        }
        Pageable pageable = pageUtils.getPageable(index, size, sort);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAll(specificationUtils.reset().getSearchSpecifications(new SearchCriteria("hospitalStatus", ComparisonOperator.EQUALS, WebConstant.HOSPITAL_STATUS_ACTIVE), new SearchCriteria(HospitalEntity.Fields.name, ComparisonOperator.CONTAINS, hospitalDTO.getName(), JoinType.INNER), new SearchCriteria(HospitalEntity.Fields.address, ComparisonOperator.CONTAINS, hospitalDTO.getAddress(), JoinType.INNER)), pageable);
        pageUtils.validatePage(hospitalEntityPage, HospitalEntity.class);
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public Page<HospitalResponse> getHospitals(int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAllByHospitalStatus(WebConstant.HOSPITAL_STATUS_ACTIVE, pageable);
        pageUtils.validatePage(hospitalEntityPage, HospitalEntity.class);
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public Page<HospitalResponse> searchHospitalsByKeyword(int index, int size, String keyword) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }
        Pageable pageable = PageRequest.of(index, size);
        Page<HospitalEntity> hospitalEntityPage = hospitalRepository.findAllByNameContainingIgnoreCaseAndHospitalStatus(keyword, WebConstant.HOSPITAL_STATUS_ACTIVE, pageable);
        if (hospitalEntityPage.isEmpty()) {
            throw new PageNotFoundException(HospitalEntity.class, index, size);
        }
        return hospitalEntityPage.map(hospitalConverter::toResponse);
    }

    @Override
    public void createHospital(HospitalDTO hospitalDTO) {
        // check if phone number or email already exists
        if (hospitalRepository.existsByPhoneNumber(hospitalDTO.getPhoneNumber())) {
            throw new ErrorResponse("Số điện thoại đã được sử dụng");
        }

        if (hospitalRepository.existsByEmail(hospitalDTO.getEmail())) {
            throw new ErrorResponse("Email đã được sử dụng");
        }

        HospitalEntity hospitalEntity = hospitalConverter.toEntity(hospitalDTO);
        hospitalEntity.setHospitalStatus(WebConstant.HOSPITAL_STATUS_ACTIVE);
        hospitalRepository.save(hospitalEntity);
    }

    @Override
    public void updateHospital(Long hospitalId, HospitalDTO hospitalDTO) {
        // Check if same email && phoneNumber
        if (hospitalRepository.existsByPhoneNumberAndIdNot(hospitalDTO.getPhoneNumber(), hospitalId)) {
            throw new ErrorResponse("Số điện thoại đã được sử dụng");
        }

        if (hospitalRepository.existsByEmailAndIdNot(hospitalDTO.getEmail(), hospitalId)) {
            throw new ErrorResponse("Email đã được sử dụng");
        }

        HospitalEntity target = hospitalRepository.findByIdAndHospitalStatus(hospitalId, WebConstant.HOSPITAL_STATUS_ACTIVE);
        if (target == null) {
            throw new EntityNotFoundException(HospitalEntity.class, hospitalId);
        }
        HospitalEntity source = hospitalConverter.toEntity(hospitalDTO);
        MergeObjectUtils.mergeNonNullFields(source, target);
        hospitalRepository.save(target);
    }

    @Override
    public void deleteHospital(Long hospitalId) {
        HospitalEntity hospitalEntity = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new EntityNotFoundException(HospitalEntity.class, hospitalId));
        hospitalEntity.getStaffEntities().forEach(staffEntity -> {
            staffService.deleteStaff(staffEntity.getId());
        });
        hospitalEntity.setHospitalStatus(WebConstant.HOSPITAL_STATUS_INACTIVE);
        hospitalRepository.save(hospitalEntity);
    }
}
