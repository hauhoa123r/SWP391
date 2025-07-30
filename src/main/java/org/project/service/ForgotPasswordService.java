//package org.project.service;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.project.entity.ForgotPassword;
//import org.project.entity.UserEntity;
//import org.project.repository.ForgotPasswordRepository;
//import org.project.repository.UserRepository;
//import org.project.utils.ChangePassword;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.Optional;
//import java.util.Random;
//
//@Service
//@RequiredArgsConstructor
//public class ForgotPasswordService {
//
//    private final UserRepository userRepository;
//    private final ForgotPasswordRepository forgotPasswordRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final MailService mailService;
//
//    public ResponseEntity<String> sendOtp(String email) {
//        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Email không tồn tại trong hệ thống.");
//        }
//
//        UserEntity user = userOpt.get();
//
//        Optional<ForgotPassword> existingOtp = forgotPasswordRepository.findByUser(user);
//        if (existingOtp.isPresent()) {
//            Date expiration = existingOtp.get().getExpirationTime();
//            if (expiration.after(new Date())) {
//                long secondsLeft = (expiration.getTime() - System.currentTimeMillis()) / 1000;
//                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                        .body("Mã OTP đã được gửi trước đó. Vui lòng thử lại sau " + secondsLeft + " giây.");
//            }
//
//            user.setForgotPassword(null);
//            userRepository.save(user);
//            userRepository.flush();
//            forgotPasswordRepository.deleteById(existingOtp.get().getFpid());
//        }
//
//        Integer otp = generateOtp();
//        ForgotPassword forgotPassword = ForgotPassword.builder()
//                .otp(otp)
//                .expirationTime(new Date(System.currentTimeMillis() + 70_000))
//                .user(user)
//                .otpVerified(false)
//                .build();
//        forgotPasswordRepository.save(forgotPassword);
//
//        try {
//            mailService.senEmail("Đổi Mật Khẩu", "Mã OTP của bạn là: " + otp, user.getEmail());
//        } catch (Exception e) {
//            user.setForgotPassword(null);
//            userRepository.save(user);
//            userRepository.flush();
//            forgotPasswordRepository.deleteById(forgotPassword.getFpid());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Không thể gửi email. Vui lòng thử lại sau.");
//        }
//
//        return ResponseEntity.ok("Mã OTP đã được gửi tới email của bạn.");
//    }
//
//    public ResponseEntity<String> verifyOtp(Integer otp, String email) {
//        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Email không hợp lệ.");
//        }
//
//        UserEntity user = userOpt.get();
//
//        Optional<ForgotPassword> fpOpt = forgotPasswordRepository.findByOtpAndUser(otp, user);
//        if (fpOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Mã OTP không chính xác.");
//        }
//
//        ForgotPassword fp = fpOpt.get();
//
//        if (fp.getExpirationTime().before(new Date())) {
//            user.setForgotPassword(null);
//            userRepository.save(user);
//            forgotPasswordRepository.deleteById(fp.getFpid());
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
//                    .body("Mã OTP đã hết hạn.");
//        }
//
//        fp.setOtpVerified(true);
//        forgotPasswordRepository.save(fp);
//        return ResponseEntity.ok("Xác minh OTP thành công.");
//    }
//
//    @Transactional
//    public ResponseEntity<String> changePassword(String email, ChangePassword changePassword) {
//        if (!changePassword.password().equals(changePassword.confirmPassword())) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
//                    .body("Xác nhận mật khẩu không khớp.");
//        }
//
//        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Email không tồn tại.");
//        }
//
//        UserEntity user = userOpt.get();
//
//        Optional<ForgotPassword> fpOpt = forgotPasswordRepository.findByUser(user);
//        if (fpOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Mã OTP chưa được xác minh.");
//        }
//
//        ForgotPassword fp = fpOpt.get();
//
//        if (!fp.isOtpVerified()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Vui lòng xác minh mã OTP trước khi đổi mật khẩu.");
//        }
//
//        userRepository.updatePassword(email, passwordEncoder.encode(changePassword.password()));
//        user.setForgotPassword(null);
//        userRepository.save(user);
//
//        return ResponseEntity.ok("Đổi mật khẩu thành công.");
//    }
//
//
//
//    private Integer generateOtp() {
//        return 100_000 + new Random().nextInt(900_000);
//    }
//
//}
