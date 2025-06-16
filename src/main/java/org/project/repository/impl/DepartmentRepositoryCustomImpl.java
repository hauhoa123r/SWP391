package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;
import org.project.repository.impl.custom.DepartmentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {
    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<DepartmentEntity> findAllDepartmentsByStaffRole(StaffRole staffRole) {
        String jpql = """
                    select de.name from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole
                    group by de.name
                """;
        TypedQuery<String> typedQuery = entityManager.createQuery(jpql, String.class);
        typedQuery.setParameter("staffRole", staffRole);
        return typedQuery.getResultList().stream().map(departmentName -> {
            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setName(departmentName);
            return departmentEntity;
        }).toList();
    }
}
