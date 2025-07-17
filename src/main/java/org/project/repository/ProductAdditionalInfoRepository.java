package org.project.repository;

import java.util.List;

import org.project.entity.ProductAdditionalInfoEntity;
import org.project.model.response.ProductAdditionalInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAdditionalInfoRepository extends JpaRepository<ProductAdditionalInfoEntity, Long> {
	@Query("SELECT new org.project.model.response.ProductAdditionalInfoResponse"
			+ "(pai.id, pai.name, pai.value) FROM ProductAdditionalInfoEntity pai"
			+ " WHERE pai.productEntity.id = :productId") 
	List<ProductAdditionalInfoResponse> findAdditionalInfoByProductId(@Param("productId") Long productId); 
}
