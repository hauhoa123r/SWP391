package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.project.entity.DepartmentEntity;
import org.project.enums.StaffRole;
import org.project.repository.impl.custom.DepartmentRepositoryCustom;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentRepositoryCustomImpl implements DepartmentRepositoryCustom {
    private EntityManager entityManager;
    private PageUtils<DepartmentEntity> pageUtils;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setPageUtils(PageUtils<DepartmentEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public List<DepartmentEntity> findAllByStaffEntitiesStaffRole(StaffRole staffRole) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffRole", staffRole);
        return getEntitiesTypedQuery.getResultList();
    }

    @Override
    public Page<DepartmentEntity> findAllByStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(StaffRole staffRole, Long hospitalId, Pageable pageable) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole and se.hospitalEntity.id = :hospitalId
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffRole", staffRole);
        getEntitiesTypedQuery.setParameter("hospitalId", hospitalId);

        String countJpql = """
                    select count(distinct de.id) from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole and se.hospitalEntity.id = :hospitalId
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countJpql, Long.class);
        countTypedQuery.setParameter("staffRole", staffRole);
        countTypedQuery.setParameter("hospitalId", hospitalId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<DepartmentEntity> findAllByNameContainingAndStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(String keyword, StaffRole staffRole, Long hospitalId, Pageable pageable) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole and se.hospitalEntity.id = :hospitalId and de.name like :keyword
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffRole", staffRole);
        getEntitiesTypedQuery.setParameter("hospitalId", hospitalId);
        getEntitiesTypedQuery.setParameter("keyword", "%" + keyword + "%");

        String countJpql = """
                    select count(distinct de.id) from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffRole and se.hospitalEntity.id = :hospitalId and de.name like :keyword
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countJpql, Long.class);
        countTypedQuery.setParameter("staffRole", staffRole);
        countTypedQuery.setParameter("hospitalId", hospitalId);
        countTypedQuery.setParameter("keyword", "%" + keyword + "%");

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }
}
