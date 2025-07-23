package org.project.repository;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByIdAndUserStatus(Long id, UserStatus userStatus);

    UserEntity findByEmailAndPasswordHash(String email, String passwordHash);

    List<UserEntity> findByEmailContaining(String email);
    Page<UserEntity> findByEmailContaining(String email, Pageable pageable);

    List<UserEntity> findByPhoneNumberContaining(String phoneNumber);
    Page<UserEntity> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);

    List<UserEntity> findByUserRole(UserRole userRole);
    Page<UserEntity> findByUserRole(UserRole userRole, Pageable pageable);

    List<UserEntity> findByUserStatus(UserStatus userStatus);
    Page<UserEntity> findByUserStatus(UserStatus status, Pageable pageable);
    Page<UserEntity> findByUserStatusNot(UserStatus status, Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.passwordHash = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);


}
