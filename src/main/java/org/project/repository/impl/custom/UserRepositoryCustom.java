package org.project.repository.impl.custom;

import org.project.entity.UserEntity;

public interface UserRepositoryCustom{
    UserEntity findByUsername(String username);

    void deleteByUsername(String username);
    void deleteByEmail(String email);

    void updateUser(UserEntity userEntity);

    void save(UserEntity userEntity);

}

