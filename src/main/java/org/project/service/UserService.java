package org.project.service;

import org.project.entity.UserEntity;

import java.util.List;
import org.springframework.data.domain.Page;


public interface UserService {
    // Phân trang người dùng
    Page<UserEntity> getAllUsers(int page, int size);

    // Tìm kiếm người dùng theo email
    List<UserEntity> searchByEmail(String email);

    // Tìm kiếm người dùng theo số điện thoại
    List<UserEntity> searchByPhoneNumber(String phoneNumber);

    // Tìm kiếm người dùng theo vai trò
    List<UserEntity> searchByRole(String role);

    // Tìm kiếm người dùng theo trạng thái
    List<UserEntity> searchByStatus(String status);
}

