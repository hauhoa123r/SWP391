package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import org.project.repository.impl.custom.PatientRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class PatientRepositoryCustomImpl implements PatientRepositoryCustom {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> getAllRelationships(Long userId) {
        String jpql = """
                select pe.relationship from PatientEntity pe
                where pe.userEntity.id = :userId
                """;

        return entityManager.createQuery(jpql, String.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Long getPatientIdByUserId(Long userId) {
        String jpql = """
                select pe.id from PatientEntity pe
                where pe.userEntity.id = :userId
                ORDER BY pe.id DESC
                """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
