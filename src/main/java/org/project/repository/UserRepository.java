package org.project.repository;

import org.project.entity.UserEntity;
import org.project.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

<<<<<<< HEAD
public interface UserRepository extends JpaRepository<UserEntity, Long>{

    UserEntity findByEmailAndPasswordHash(String email, String passwordHash);

=======
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByIdAndUserStatus(Long id, UserStatus userStatus);
>>>>>>> origin/main
}
