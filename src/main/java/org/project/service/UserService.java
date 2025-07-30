package org.project.service;

import org.project.entity.UserEntity;
import org.project.model.dto.UserLoginDTO;
import org.project.model.response.UserLoginResponse;
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

    // ================== CRUD Operations ==================
    // Tạo người dùng mới (cũ)
    UserEntity createUser(UserEntity user);


    // Lấy người dùng theo ID
    UserEntity getUserById(Long id);

    // Lấy chi tiết người dùng với thông tin liên quan
    UserEntity getUserDetails(Long id) throws org.project.exception.ResourceNotFoundException;

    // Kiểm tra người dùng có tồn tại không
    boolean existsById(Long id);

    // Cập nhật thông tin người dùng
    UserEntity updateUser(Long id, UserEntity updatedUser);

    // Vô hiệu hóa người dùng
    void deactivateUser(Long id);

    // Xóa người dùng vĩnh viễn
    void deleteUser(Long id);

    // Lấy danh sách người dùng đã bị xóa tạm
    Page<UserEntity> getDeletedUsers(int page, int size);

    // Khôi phục người dùng đã bị xóa tạm
    void restoreUser(Long id);

    UserLoginResponse isLogin(UserLoginDTO userLoginDTO);

    void resetPassword(String email);

    Long getUserIdByUsername(String username);
}
