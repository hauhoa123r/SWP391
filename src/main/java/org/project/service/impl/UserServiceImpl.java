package org.project.service.impl;

import org.project.entity.UserEntity;
import org.project.exception.ErrorResponse;
import org.project.repository.UserRepository;
import org.project.service.EmailService;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private EmailService emailService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    private String generateNewPassword() {
        // Password contains only alphanumeric characters
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 6; i++) { // Length
            int index = (int) (Math.random() * characters.length());
            newPassword.append(characters.charAt(index));
        }
        return newPassword.toString();
    }

    @Override
    public void resetPassword(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new ErrorResponse("Không tìm thấy người dùng với email: " + email);
        }

        String newPassword = generateNewPassword();
        userEntity.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        emailService.sendResetPasswordEmail(email, newPassword);
    }

    @Override
    public boolean isExistPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updatePhoneNumber(Long userId, String phoneNumber) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setPhoneNumber(phoneNumber);
        userRepository.save(userEntity);
    }

    @Override
    public void updateEmail(Long userId, String email) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setEmail(email);
        userRepository.save(userEntity);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(userEntity);
    }
}
