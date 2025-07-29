package org.project.service;

public interface UserService {
    void resetPassword(String email);

    boolean isExistPhoneNumber(String phoneNumber);

    boolean isExistEmail(String email);

    void updatePhoneNumber(Long userId, String phoneNumber);

    void updateEmail(Long userId, String email);

    void updatePassword(Long userId, String password);
}
