package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.project.entity.UserEntity;
import org.project.repository.impl.custom.UserRepositoryCustom;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public UserEntity findByUsername(String username) {
        return (UserEntity) entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username").setParameter("username", username).getSingleResult();
    }

    @Override
    public void deleteByUsername(String username) {
    }

    @Override
    public void deleteByEmail(String email) {

    }

    @Override
    public void updateUser(UserEntity userEntity) {
    }

    @Override
    public void save(UserEntity userEntity) {
    }
}
