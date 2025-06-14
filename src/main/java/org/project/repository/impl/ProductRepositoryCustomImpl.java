package org.project.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.repository.impl.custom.ProductRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<ProductEntity> findAllByProductType(ProductType productType, Pageable pageable) {
        String getEntitiesJpql = """
                select p from ProductEntity p
                left join p.reviewEntities r
                where p.productType = :productType
                group by p
                order by avg(r.rating) desc, count(r) desc
                """;
        TypedQuery<ProductEntity> getEntitiesTypedQuery = entityManager.createQuery(getEntitiesJpql, ProductEntity.class);
        getEntitiesTypedQuery.setParameter("productType", productType);
        getEntitiesTypedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        getEntitiesTypedQuery.setMaxResults(pageable.getPageSize());
        List<ProductEntity> productEntities = getEntitiesTypedQuery.getResultList();

        String countEntitiesJpql = """
                select count(p) from ProductEntity p
                where p.productType = :productType
                """;
        TypedQuery<Long> countTypedQuery = entityManager.createQuery(countEntitiesJpql, Long.class);
        countTypedQuery.setParameter("productType", productType);
        Long total = countTypedQuery.getSingleResult();
        return new PageImpl<>(productEntities, pageable, total);
    }
}
