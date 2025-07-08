package org.project.repository;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByIdAndUserStatus(Long id, UserStatus userStatus);

    List<UserEntity> findByEmailContaining(String email);
    Page<UserEntity> findByEmailContaining(String email, Pageable pageable);

    List<UserEntity> findByPhoneNumberContaining(String phoneNumber);
    Page<UserEntity> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);

    List<UserEntity> findByUserRole(UserRole userRole);
    Page<UserEntity> findByUserRole(UserRole userRole, Pageable pageable);

    List<UserEntity> findByUserStatus(UserStatus userStatus);
    Page<UserEntity> findByUserStatus(UserStatus userStatus, Pageable pageable);
}
