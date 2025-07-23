package org.project.repository;

import org.project.entity.StaffEntity;
import org.project.enums.StaffStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffRepositoryCustom {
    Page<StaffEntity> searchStaffs(Pageable pageable, String field, String keyword);
    Page<StaffEntity> searchStaffs(Pageable pageable, String keyword);
    Page<StaffEntity> searchStaffsByStatusNot(StaffStatus status, Pageable pageable, String field, String keyword);
}