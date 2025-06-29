package org.project.service.impl;

import org.project.entity.UserEntity;
import org.project.enums.UserRole;
import org.project.enums.UserStatus;
import org.project.repository.UserRepository;
import org.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<UserEntity> searchByEmail(String email) {
        return userRepository.findByEmailContaining(email);
    }

    // Tìm kiếm người dùng theo số điện thoại
    @Override
    public List<UserEntity> searchByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumberContaining(phoneNumber);
    }

    // Tìm kiếm người dùng theo vai trò
    @Override
    public List<UserEntity> searchByRole(String role) {
        return userRepository.findByUserRole(UserRole.valueOf(role));
    }

    // Tìm kiếm người dùng theo trạng thái
    @Override
    public List<UserEntity> searchByStatus(String status) {
                return userRepository.findByUserStatus(UserStatus.valueOf(status));
    }

    
}
