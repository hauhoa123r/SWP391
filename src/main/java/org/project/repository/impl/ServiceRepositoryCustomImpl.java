package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.project.entity.ServiceEntity;
import org.project.enums.ProductStatus;
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
    public Page<ServiceEntity> findAllByProductEntityProductStatusOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                where p.productStatus = :productEntityProductStatus
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);
        getEntitiesTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                where p.productStatus = :productEntityProductStatus
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<ServiceEntity> findAllByProductEntityProductStatusAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, Long departmentEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                left join s.departmentEntity d
                where p.productStatus = :productEntityProductStatus and d.id = :departmentEntityId
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);
        getEntitiesTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                left join s.departmentEntity d
                where p.productStatus = :productEntityProductStatus and d.id = :departmentEntityId
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }

    @Override
    public Page<ServiceEntity> findAllByProductEntityProductStatusAndProductEntityNameContainingAndDepartmentEntityIdOrderByProductEntityAverageRatingAndProductEntityReviewCount(ProductStatus productEntityProductStatus, String productEntityName, Long departmentEntityId, Pageable pageable) {
        String getEntitiesJpql = """
                select s from ServiceEntity s
                left join s.productEntity p
                left join p.reviewEntities r
                left join s.departmentEntity d
                where p.productStatus = :productEntityProductStatus and d.id = :departmentEntityId and lower(p.name) like lower(concat('%', :productEntityName, '%'))
                group by s
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ServiceEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ServiceEntity.class);
        getEntitiesTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);
        getEntitiesTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        getEntitiesTypedQuery.setParameter("productEntityName", productEntityName);

        String countEntitiesJpql = """
                select count(s) from ServiceEntity s
                left join s.productEntity p
                left join s.departmentEntity d
                where p.productStatus = :productEntityProductStatus and d.id = :departmentEntityId and lower(p.name) like lower(concat('%', :productEntityName, '%'))
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("productEntityProductStatus", productEntityProductStatus);
        countTypedQuery.setParameter("departmentEntityId", departmentEntityId);
        countTypedQuery.setParameter("productEntityName", productEntityName);

        return pageUtils.getEntitiesByPage(getEntitiesTypedQuery, countTypedQuery, pageable);
    }
}
