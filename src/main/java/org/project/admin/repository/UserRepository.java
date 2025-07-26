package org.project.admin.repository;

import org.project.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("adminUserRepository")
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
//    Optional<User> findByUserRole(UserRole userRole);

    // Kiểm tra email đã tồn tại (chưa bị xóa)
    boolean existsByEmailAndDeletedFalse(String email);

    // Kiểm tra email trùng với user khác (không phải user hiện tại)
    boolean existsByEmailAndDeletedFalseAndUserIdNot(String email, Long userId);

    // Kiểm tra số điện thoại đã tồn tại (chưa bị xóa)
    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);

    // Kiểm tra số điện thoại trùng với user khác (không phải user hiện tại)
    boolean existsByPhoneNumberAndDeletedFalseAndUserIdNot(String phoneNumber, Long userId);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllByDeletedFalse(Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE user_id = :id", nativeQuery = true)
    Optional<User> findByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM users WHERE deleted = true", nativeQuery = true)
    Page<User> findAllDeleted(Pageable pageable);
}
