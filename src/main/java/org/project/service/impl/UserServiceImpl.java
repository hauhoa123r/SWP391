package org.project.service.impl;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.repository.UserRepository;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    // Lấy tất cả người dùng (phân trang)
    @Override
    public Page<UserEntity> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }


        // Tìm kiếm người dùng theo email
    @Override
    public Page<UserEntity> searchByEmail(String email, int page, int size) {
        return userRepository.findByEmailContaining(email, PageRequest.of(page, size));
    }

    // Tìm kiếm người dùng theo số điện thoại
    @Override
    public Page<UserEntity> searchByPhoneNumber(String phoneNumber, int page, int size) {
        return userRepository.findByPhoneNumberContaining(phoneNumber, PageRequest.of(page, size));
    }

    // Tìm kiếm người dùng theo vai trò (không phân biệt hoa thường, trả về rỗng nếu giá trị không hợp lệ)
    @Override
    public Page<UserEntity> searchByRole(String role, int page, int size) {
        try {
            return userRepository.findByUserRole(UserRole.valueOf(role.toUpperCase()), PageRequest.of(page, size));
        } catch (IllegalArgumentException ex) {
            return Page.empty();
        }
    }

    // Tìm kiếm người dùng theo trạng thái (hỗ trợ tiếng Việt/Anh, không phân biệt hoa thường)
    @Override
    public Page<UserEntity> searchByStatus(String status, int page, int size) {
        if (status == null) return Page.empty();
        String normalized = java.text.Normalizer.normalize(status, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // remove diacritics
                .toLowerCase()
                .replaceAll("\\s+", "");

        UserStatus target;
        switch (normalized) {
            case "active":
            case "hoatdong":
            case "co":
            case "danghoatdong":
                target = UserStatus.ACTIVE;
                break;
            case "inactive":
            case "khong":
            case "ko":
            case "ngung":
            case "tamngung":
            case "khonghoatdong":
                target = UserStatus.INACTIVE;
                break;
            default:
                // last attempt: try direct enum mapping
                try {
                    target = UserStatus.valueOf(status.toUpperCase());
                } catch (Exception e) {
                    return Page.empty();
                }
        }
        return userRepository.findByUserStatus(target, PageRequest.of(page, size));
    }

    // ================== CRUD Operations ==================
    @Override
    public UserEntity createUser(UserEntity user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new org.project.exception.DuplicateResourceException("email", "Email already exists");
        }
        // hash password if provided
        if (user.getPasswordHash() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUserById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    @Override
    public UserEntity updateUser(Long id, UserEntity updatedUser) {
        if (id == null || updatedUser == null) {
            throw new IllegalArgumentException("Id and user must not be null");
        }
        if (updatedUser.getEmail() != null && userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
            throw new org.project.exception.DuplicateResourceException("email", "Email already exists");
        }
        if (id == null || updatedUser == null) {
            throw new IllegalArgumentException("Id and user must not be null");
        }
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        UserEntity existing = optional.get();
        // Copy allowed fields (simple example)
        if(updatedUser.getEmail()!=null) existing.setEmail(updatedUser.getEmail());
        if(updatedUser.getPhoneNumber()!=null) existing.setPhoneNumber(updatedUser.getPhoneNumber());
        if(updatedUser.getUserRole()!=null) existing.setUserRole(updatedUser.getUserRole());
        if(updatedUser.getUserStatus()!=null) existing.setUserStatus(updatedUser.getUserStatus());
        existing.setIsVerified(updatedUser.getIsVerified());
        existing.setTwoFactorEnabled(updatedUser.getTwoFactorEnabled());
        // update password if provided
        if(updatedUser.getPasswordHash()!=null && !updatedUser.getPasswordHash().isBlank()){
            existing.setPasswordHash(passwordEncoder.encode(updatedUser.getPasswordHash()));
        }
        return userRepository.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    
}
