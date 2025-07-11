package org.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ForgotPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpid;

    @Column(nullable = false)
    private Integer otp; // Changed to String to store hashed OTP

    @Column(nullable = false)
    private Date expirationTime;

    @Column(name = "otp_verified", nullable = false)
    private boolean otpVerified;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}