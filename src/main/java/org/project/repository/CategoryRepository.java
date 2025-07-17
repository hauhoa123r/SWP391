package org.project.repository;

import java.util.List;

import org.project.entity.CategoryEntity;
import org.project.entity.ProductCategoryEntityId;
import org.project.model.response.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	//Query using dto 
	@Query("SELECT new org.project.model.response.CategoryResponse(c.id, c.name, c.description)"
			+ "FROM ProductCategoryEntity pc JOIN pc.categoryEntity c "
			+ "WHERE pc.productEntity.id = :productId")
	List<CategoryResponse> findCategoriesByProductId(@Param("productId") Long productId); 
}
