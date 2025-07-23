// ProductCategoryRepository.java
package org.project.repository;

import org.project.entity.ProductCategoryEntity;
import org.project.entity.ProductCategoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, ProductCategoryEntityId> {
    @Query("SELECT COUNT(pc) FROM ProductCategoryEntity pc WHERE pc.categoryEntity.id = :categoryId")
    int countByCategoryId(@Param("categoryId") Long categoryId);
}
