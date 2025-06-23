package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.project.entity.ServiceEntity;
import org.project.repository.impl.custom.ServiceRepositoryCustom;
import org.project.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceRepositoryCustomImpl implements ServiceRepositoryCustom {

    private EntityManager entityManager;
    private PageUtils<ServiceEntity> pageUtils;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setCustomPage(PageUtils<ServiceEntity> pageUtils) {
        this.pageUtils = pageUtils;
    }

    @Override
    public Page<ServiceEntity> findAllOrderByProductEntityAverageRatingAndProductEntityReviewCount(Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<ServiceEntity> findAllByDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(Long departmentEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                left join s.departmentEntity d
                where d.id = :departmentEntityId
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                left join s.departmentEntity d
                where d.id = :departmentEntityId
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<ServiceEntity> findAllByDepartmentEntityIdAndNameContainingOrderByProductEntityAverageRatingAndProductEntityReviewCount(Long departmentEntityId, String keyword, Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                left join s.departmentEntity d
                where d.id = :departmentEntityId and p.name like lower(concat('%', :keyword, '%'))
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        getEntitiesTypedQuery.setParameter("keyword", keyword);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                left join s.departmentEntity d
                where d.id = :departmentEntityId and p.name like lower(concat('%', :keyword, '%'))
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        countTypedQuery.setParameter("keyword", keyword);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }
}
