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
    public List<DepartmentEntity> findAllByStaffEntitiesStaffRole(StaffRole staffEntitiesStaffRole) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffEntitiesStaffRole
                    and de.departmentStatus = 'ACTIVE'
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffEntitiesStaffRole", staffEntitiesStaffRole);
        return getEntitiesTypedQuery.getResultList();
    }

    @Override
    public Page<DepartmentEntity> findAllByStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(StaffRole staffEntitiesStaffRole, Long hospitalEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffEntitiesStaffRole and se.hospitalEntity.id = :hospitalEntityId and de.departmentStatus = 'ACTIVE'
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffEntitiesStaffRole", staffEntitiesStaffRole);
        getEntitiesTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);

        String countJpql = """
                    select count(distinct de.id) from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffEntitiesStaffRole and se.hospitalEntity.id = :hospitalEntityId and de.departmentStatus = 'ACTIVE'
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countJpql, Long.class);
        countTypedQuery.setParameter("staffEntitiesStaffRole", staffEntitiesStaffRole);
        countTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<DepartmentEntity> findAllByNameContainingAndStaffEntitiesStaffRoleAndStaffEntitiesHospitalEntityId(String name, StaffRole staffEntitiesStaffRole, Long hospitalEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                    select de from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffEntitiesStaffRole and se.hospitalEntity.id = :hospitalEntityId and de.name like :name and de.departmentStatus = 'ACTIVE'
                    group by de.id
                """;
        TypedQuery<DepartmentEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DepartmentEntity.class);
        getEntitiesTypedQuery.setParameter("staffEntitiesStaffRole", staffEntitiesStaffRole);
        getEntitiesTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);
        getEntitiesTypedQuery.setParameter("name", "%" + name + "%");

        String countJpql = """
                    select count(distinct de.id) from DepartmentEntity de
                    join de.staffEntities se
                    where se.staffRole = :staffEntitiesStaffRole and se.hospitalEntity.id = :hospitalEntityId and de.name like :name and de.departmentStatus = 'ACTIVE'
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countJpql, Long.class);
        countTypedQuery.setParameter("staffEntitiesStaffRole", staffEntitiesStaffRole);
        countTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);
        countTypedQuery.setParameter("name", "%" + name + "%");

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }
}
