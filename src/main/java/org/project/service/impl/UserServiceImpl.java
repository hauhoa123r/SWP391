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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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

    
}
