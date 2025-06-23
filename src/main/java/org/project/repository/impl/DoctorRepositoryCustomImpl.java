package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.project.entity.DoctorEntity;
import org.project.repository.impl.custom.DoctorRepositoryCustom;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DoctorRepositoryCustomImpl implements DoctorRepositoryCustom {

    private EntityManager entityManager;
    private PageUtils<DoctorEntity> pageUtils;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setCustomPage(PageUtils<DoctorEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public Page<DoctorEntity> findAllOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Pageable pageable) {
        String getEntitiesJpql = """
                select d from DoctorEntity d
                left join d.staffEntity s
                left join s.reviewEntities r
                group by d.id
                order by avg(r.rating) desc, count(r.id) desc
                """;
        TypedQuery<DoctorEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DoctorEntity.class);

        String countEntitiesJpql = """
                select count(d) from DoctorEntity d
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                select d from DoctorEntity d
                left join d.staffEntity s
                left join s.reviewEntities r
                where s.departmentEntity.id = :departmentEntityId
                group by d.id
                order by avg(r.rating) desc, count(r.id) desc
                """;
        TypedQuery<DoctorEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DoctorEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        String countEntitiesJpql = """
                select count(d) from DoctorEntity d
                left join d.staffEntity s
                where s.departmentEntity.id = :departmentEntityId
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId, Long hospitalEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                select d from DoctorEntity d
                left join d.staffEntity s
                left join s.reviewEntities r
                where s.departmentEntity.id = :departmentEntityId and s.hospitalEntity.id = :hospitalEntityId
                group by d.id
                order by avg(r.rating) desc, count(r.id) desc
                """;
        TypedQuery<DoctorEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DoctorEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        getEntitiesTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);

        String countEntitiesJpql = """
                select count(d) from DoctorEntity d
                left join d.staffEntity s
                where s.departmentEntity.id = :departmentEntityId and s.hospitalEntity.id = :hospitalEntityId
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        countTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndStaffEntityHospitalEntityIdAndStaffEntityFullNameContainingOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId, Long hospitalEntityId, String keyword, Pageable pageable) {
        String getEntitiesJpql = """
                select d from DoctorEntity d
                left join d.staffEntity s
                left join s.reviewEntities r
                where s.departmentEntity.id = :departmentEntityId and s.hospitalEntity.id = :hospitalEntityId
                and lower(s.fullName) like lower(concat('%', :keyword, '%'))
                group by d.id
                order by avg(r.rating) desc, count(r.id) desc
                """;
        TypedQuery<DoctorEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DoctorEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        getEntitiesTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);
        getEntitiesTypedQuery.setParameter("keyword", keyword);

        String countEntitiesJpql = """
                select count(d) from DoctorEntity d
                left join d.staffEntity s
                where s.departmentEntity.id = :departmentEntityId and s.hospitalEntity.id = :hospitalEntityId
                and lower(s.fullName) like lower(concat('%', :keyword, '%'))
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        countTypedQuery.setParameter("hospitalEntityId", hospitalEntityId);
        countTypedQuery.setParameter("keyword", keyword);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public List<DoctorEntity> findAllByStaffEntityDepartmentEntityIdAndIdNotEqualsOrderByStaffEntityAverageRatingAndStaffEntityReviewCount(Long departmentEntityId, Long id) {
        String getEntitiesJpql = """
                select d from DoctorEntity d
                left join d.staffEntity s
                left join s.reviewEntities r
                where s.departmentEntity.id = :departmentEntityId and d.id <> :id
                group by d.id
                order by avg(r.rating) desc, count(r.id) desc
                """;
        TypedQuery<DoctorEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, DoctorEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        getEntitiesTypedQuery.setParameter("id", id);

        return getEntitiesTypedQuery.getResultList();
    }
}
