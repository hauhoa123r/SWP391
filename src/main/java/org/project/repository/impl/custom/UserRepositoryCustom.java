package org.project.repository.impl.custom;

import org.project.entity.UserEntity;

public interface UserRepositoryCustom{
    UserEntity findByEmail(String email);

    void deleteByEmail(String email);

    void updateUser(UserEntity userEntity);

    void save(UserEntity userEntity);

}

