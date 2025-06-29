package org.project.repository;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByIdAndUserStatus(Long id, UserStatus userStatus);

    List<UserEntity> findByEmailContaining(String email);

    List<UserEntity> findByPhoneNumberContaining(String phoneNumber);

    List<UserEntity> findByUserRole(UserRole userRole);

    List<UserEntity> findByUserStatus(UserStatus userStatus);
}
