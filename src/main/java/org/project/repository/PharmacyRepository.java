package org.project.repository;

import java.util.List;

import org.project.entity.PharmacyProductEntity;
import org.project.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface PharmacyRepository extends JpaRepository<ProductEntity, Long> {
	//find By Type 
	List<ProductEntity> findByTypeContaining(String type);  
	//find by name 
	List<ProductEntity> findByNameContaining(String name); 
	//list top 10 product for home page 
	@Query(value = "SELECT * FROM products LIMIT 10", nativeQuery = true) 
	List<ProductEntity> findTop10Products();
}
