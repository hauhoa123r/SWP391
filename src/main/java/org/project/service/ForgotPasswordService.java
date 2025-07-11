package org.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.project.entity.ForgotPassword;
import org.project.entity.UserEntity;
import org.project.repository.ForgotPasswordRepository;
import org.project.repository.UserRepository;
import org.project.utils.ChangePassword;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public ResponseEntity<String> sendOtp(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email.");
        }

        UserEntity user = userOpt.get();

        Optional<ForgotPassword> existingOtp = forgotPasswordRepository.findByUser(user);
        if (existingOtp.isPresent()) {
            Date expiration = existingOtp.get().getExpirationTime();
            if (expiration.after(new Date())) {
                long secondsLeft = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("An OTP has already been sent. Please try again in " + secondsLeft + " seconds.");
            }

            user.setForgotPassword(null);
            userRepository.save(user);
            userRepository.flush();
            forgotPasswordRepository.deleteById(existingOtp.get().getFpid());
        }

        Integer otp = generateOtp();
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70_000))
                .user(user)
                .otpVerified(false)
                .build();
        forgotPasswordRepository.save(forgotPassword);

        try {
            mailService.senEmail("Change Password", "This is your OTP: " + otp, user.getEmail());
        } catch (Exception e) {
            user.setForgotPassword(null);
            userRepository.save(user);
            userRepository.flush();
            forgotPasswordRepository.deleteById(forgotPassword.getFpid());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to send email.");
        }

        return ResponseEntity.ok("OTP has been sent to your email.");
    }

    public ResponseEntity<String> verifyOtp(Integer otp, String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email.");
        }

        UserEntity user = userOpt.get();

        Optional<ForgotPassword> fpOpt = forgotPasswordRepository.findByOtpAndUser(otp, user);
        if (fpOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP.");
        }

        ForgotPassword fp = fpOpt.get();

        if (fp.getExpirationTime().before(new Date())) {
            user.setForgotPassword(null);
            userRepository.save(user);
            forgotPasswordRepository.deleteById(fp.getFpid());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("OTP has expired.");
        }

        fp.setOtpVerified(true);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("OTP verified successfully!");
    }

    @Transactional
    public ResponseEntity<String> changePassword(String email, ChangePassword changePassword) {
        if (!changePassword.password().equals(changePassword.confirmPassword())) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Password confirmation does not match.");
        }

        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email does not exist.");
        }

        UserEntity user = userOpt.get();

        Optional<ForgotPassword> fpOpt = forgotPasswordRepository.findByUser(user);
        if (fpOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP not verified.");
        }

        ForgotPassword fp = fpOpt.get();

        if (!fp.isOtpVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please verify OTP before changing the password.");
        }

        userRepository.updatePassword(email, passwordEncoder.encode(changePassword.password()));
        user.setForgotPassword(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password changed successfully.");
    }


    private Integer generateOtp() {
        return 100_000 + new Random().nextInt(900_000);
    }

}
