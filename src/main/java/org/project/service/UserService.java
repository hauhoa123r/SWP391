package org.project.service;

import org.project.entity.UserEntity;


import org.springframework.data.domain.Page;


public interface UserService {
    // Phân trang người dùng
    Page<UserEntity> getAllUsers(int page, int size);

    // Tìm kiếm người dùng theo email
    Page<UserEntity> searchByEmail(String email, int page, int size);

    // Tìm kiếm người dùng theo số điện thoại
    Page<UserEntity> searchByPhoneNumber(String phoneNumber, int page, int size);

    // Tìm kiếm người dùng theo vai trò
    Page<UserEntity> searchByRole(String role, int page, int size);

    // Tìm kiếm người dùng theo trạng thái
    Page<UserEntity> searchByStatus(String status, int page, int size);
}

