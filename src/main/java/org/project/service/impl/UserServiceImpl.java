package org.project.service.impl;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.exception.ErrorResponse;
import org.project.exception.ResourceNotFoundException;
import org.project.model.dto.UserLoginDTO;
import org.project.model.response.UserLoginResponse;
import org.project.repository.UserRepository;
import org.project.service.EmailService;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private EmailService emailService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserEntity> getAllUsers(int page, int size) {
        return userRepository.findByUserStatusNot(UserStatus.INACTIVE, PageRequest.of(page, size));
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public Page<UserEntity> searchByStatus(String status, int page, int size) {
        if (status == null) return Page.empty();
        String normalized = java.text.Normalizer.normalize(status, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("\\s+", "");

        UserStatus target;
        switch (normalized) {
            case "hoatdong":
            case "active":
                target = UserStatus.ACTIVE;
                break;
            case "ngunghoatdong":
            case "inactive":
            case "daxoatam":
            case "xoa":
                target = UserStatus.INACTIVE;
                break;
            default:
                try {
                    target = UserStatus.valueOf(status.toUpperCase());
                } catch (Exception e) {
                    return Page.empty();
                }
        }
        return userRepository.findByUserStatus(target, PageRequest.of(page, size));
    }

    @Override
    public void deactivateUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUserStatus(UserStatus.INACTIVE); // ✅ Đúng kiểu enum
        userRepository.save(user);
    }

    @Override
    public Page<UserEntity> getDeletedUsers(int page, int size) {
        return userRepository.findByUserStatus(UserStatus.INACTIVE, PageRequest.of(page, size));
    }

    @Override
    public void restoreUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public UserLoginResponse isLogin(UserLoginDTO userLoginDTO) {
        return null;
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
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID không được null");
        }
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && userRepository.existsById(id);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    @Override
    public UserEntity getUserDetails(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        if (id == null || updatedUser == null) {
            throw new IllegalArgumentException("Id and user must not be null");
        }

        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isEmpty()) return null;

        UserEntity existing = optional.get();

        if (updatedUser.getEmail() != null && userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
            throw new org.project.exception.DuplicateResourceException("email", "Email already exists");
        }

        if (updatedUser.getEmail() != null) existing.setEmail(updatedUser.getEmail());
        if (updatedUser.getPhoneNumber() != null) existing.setPhoneNumber(updatedUser.getPhoneNumber());
        if (updatedUser.getUserRole() != null) existing.setUserRole(updatedUser.getUserRole());
        if (updatedUser.getUserStatus() != null) existing.setUserStatus(updatedUser.getUserStatus());
        existing.setIsVerified(updatedUser.getIsVerified());
        existing.setTwoFactorEnabled(updatedUser.getTwoFactorEnabled());

        if (updatedUser.getPasswordHash() != null && !updatedUser.getPasswordHash().isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }

        return userRepository.save(existing);
    }

    @Override
    public Page<UserEntity> searchByEmail(String email, int page, int size) {
        return userRepository.findByEmailContaining(email, PageRequest.of(page, size));
    }

    @Override
    public Page<UserEntity> searchByPhoneNumber(String phoneNumber, int page, int size) {
        return userRepository.findByPhoneNumberContaining(phoneNumber, PageRequest.of(page, size));
    }

    @Override
    public Page<UserEntity> searchByRole(String role, int page, int size) {
        try {
            return userRepository.findByUserRole(UserRole.valueOf(role.toUpperCase()), PageRequest.of(page, size));
        } catch (IllegalArgumentException ex) {
            return Page.empty();
        }
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new org.project.exception.DuplicateResourceException("email", "Email already exists");
        }
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }
}
