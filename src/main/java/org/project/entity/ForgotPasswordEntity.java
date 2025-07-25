package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "forgot_password")
public class ForgotPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fpid", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "otp", nullable = false)
    private Integer otp;

    @NotNull
    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;

    @NotNull
    @Column(name = "otp_verified", nullable = false)
    private Boolean otpVerified = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}