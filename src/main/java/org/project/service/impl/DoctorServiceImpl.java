package org.project.service.impl;

import jakarta.transaction.Transactional;
import org.project.converter.DoctorConverter;
import org.project.entity.DoctorEntity;
import org.project.exception.sql.EntityNotFoundException;
import org.project.model.response.DoctorResponse;
import org.project.model.response.StaffResponse;
import org.project.repository.DoctorRepository;
import org.project.repository.impl.custom.DoctorRepositoryCustom;
import org.project.service.DoctorService;
import org.project.utils.PageUtils;
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

    @Override
    public Page<DoctorResponse> getAll(int index, int size) {
        Pageable pageable = pageUtils.getPageable(index, size);
        Page<DoctorEntity> doctorEntityPage = doctorRepositoryCustom.findAllOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(pageable);
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
}
