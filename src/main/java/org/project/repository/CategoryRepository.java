package org.project.repository;
import org.project.entity.ProductCategoryEntity;
import org.project.entity.CategoryEntity;
import org.project.entity.ProductCategoryEntityId;
import org.project.model.response.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
	//Query using dto
	@Query("SELECT new org.project.model.response.CategoryResponse(c.id, c.name, c.description)"
			+ "FROM ProductCategoryEntity pc JOIN pc.categoryEntity c "
			+ "WHERE pc.productEntity.id = :productId")
	List<CategoryResponse> findCategoriesByProductId(@Param("productId") Long productId);

    @Query("SELECT c FROM CategoryEntity c")
    List<CategoryEntity> findAllCategory();

}
