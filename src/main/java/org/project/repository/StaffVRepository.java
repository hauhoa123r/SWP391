package org.project.repository;

import org.project.entity.StaffEntity;
import org.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffVRepository extends JpaRepository<StaffEntity,Long> {
    StaffEntity findByUserEntity(UserEntity userEntity);

    StaffEntity findByUserEntityId(Long userEntityId);
}