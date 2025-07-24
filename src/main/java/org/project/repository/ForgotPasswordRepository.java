package org.project.repository;

import org.project.entity.ForgotPassword;
import org.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Integer> {

    @Query("SELECT fp FROM ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, UserEntity user);

    Optional<ForgotPassword> findByUser(UserEntity user);

    @Modifying
    @Query("UPDATE ForgotPassword f SET f.otpVerified = true WHERE f.fpid = :id")
    void updateOtpVerified(@Param("id") Integer id);

}
