package org.project.admin.service.impl;

import org.project.admin.dto.request.UserRequest;
import org.project.admin.dto.request.UserSearchRequest;
import org.project.admin.dto.request.UserUpdateRequest;
import org.project.admin.dto.response.UserResponse;
import org.project.admin.entity.User;
import org.project.admin.enums.AuditAction;
import org.project.admin.enums.users.UserStatus;
import org.project.admin.mapper.UserMapper;
import org.project.admin.repository.UserRepository;
import org.project.admin.service.Log.UserLogService;
import org.project.admin.service.UserService;
import org.project.admin.specification.UserSpecification;
import org.project.admin.util.PageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service("adminUserService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserLogService userLogService;

    @Override
    public UserResponse createUser(UserRequest dto) {
        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmailAndDeletedFalse(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi người dùng khác");
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty() &&
            userRepository.existsByPhoneNumberAndDeletedFalse(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi người dùng khác");
        }

        User user = userMapper.toEntity(dto);
        if (dto.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        user.setIsVerified(false);
        User saved = userRepository.save(user);

        // Ghi log tạo mới
        userLogService.logUserAction(saved, AuditAction.CREATE);
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng"));

        // Kiểm tra email trùng với user khác (không phải chính user hiện tại)
        if (!user.getEmail().equals(dto.getEmail()) &&
            userRepository.existsByEmailAndDeletedFalseAndUserIdNot(dto.getEmail(), id)) {
            throw new IllegalArgumentException("Email đã được sử dụng bởi người dùng khác");
        }

        // Kiểm tra số điện thoại trùng với user khác (không phải chính user hiện tại)
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().trim().isEmpty() &&
            !user.getPhoneNumber().equals(dto.getPhoneNumber()) &&
            userRepository.existsByPhoneNumberAndDeletedFalseAndUserIdNot(dto.getPhoneNumber(), id)) {
            throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi người dùng khác");
        }

        // Lưu trạng thái cũ để log
        UserResponse oldUser = userMapper.toResponse(user);

        // Cập nhật thông tin cơ bản
        if (dto.getUserRole() != null) user.setUserRole(dto.getUserRole());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getUserStatus() != null) user.setUserStatus(dto.getUserStatus());
        if (dto.getTwoFactorEnabled() != null) user.setTwoFactorEnabled(dto.getTwoFactorEnabled());

        // Chỉ cập nhật password nếu có giá trị trong request
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        // Nếu password null hoặc empty thì giữ nguyên password cũ

        User saved = userRepository.save(user);

        // Ghi log update
        UserResponse newUser = userMapper.toResponse(saved);
        userLogService.logUserUpdateAction(oldUser, newUser, AuditAction.UPDATE);

        return newUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng"));


        user.setDeleted(true);
        user.setUserStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        userLogService.logUserAction(user, AuditAction.DELETE);
    }


    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy người dùng"));
        return userMapper.toResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserResponse> pageData = userRepository.findAllByDeletedFalse(pageable)
                .map(userMapper::toResponse);
        return new PageResponse<>(pageData);
    }

    @Override
    public PageResponse<UserResponse> search(UserSearchRequest request, int page, int size) {
        Specification<User> spec = UserSpecification.filter(request)
                .and((root, query, cb) -> cb.equal(root.get("deleted"), false));

        Page<User> userPage = userRepository.findAll(spec, PageRequest.of(page, size));
        Page<UserResponse> mappedPage = userPage.map(userMapper::toResponse);
        return new PageResponse<>(mappedPage);
    }

}
