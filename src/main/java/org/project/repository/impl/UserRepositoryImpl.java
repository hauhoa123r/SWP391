package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.project.entity.UserEntity;
import org.project.model.dto.UserLoginDTO;
import org.project.repository.UserRepository;
import org.project.repository.impl.custom.UserRepositoryCustom;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;


    @Override
    public UserEntity findByEmail(String email) {
        StringBuilder sql = new StringBuilder("SELECT u FROM UserEntity u WHERE u.email = :email");
        return (UserEntity)entityManager.createQuery(sql.toString()).setParameter("email", email).getSingleResult();
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
