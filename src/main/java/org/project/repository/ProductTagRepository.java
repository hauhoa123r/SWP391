package org.project.repository;

import java.util.List;
import java.util.Optional;

import org.project.entity.ProductTagEntity;
import org.project.entity.ProductTagEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTagEntity, ProductTagEntityId> {
	@Query("SELECT pt.id.name FROM ProductTagEntity pt "
			+ "WHERE pt.id.productId = :productId") 
	List<String> findTagNamesByProductId(@Param("productId") Long productId);

	@Query("SELECT DISTINCT pt.id.name FROM ProductTagEntity pt")
	List<String> findAllDistinctTagNames();

	@Query("SELECT t FROM ProductTagEntity t WHERE t.id.productId = :productId AND t.id.name = :name")
	Optional<ProductTagEntity> findTagByIdCustom(@Param("productId") Long productId, @Param("name") String name);
    @Query("SELECT DISTINCT pt.id.name FROM ProductTagEntity pt")
    List<String> findDistinctTagNames();
}
