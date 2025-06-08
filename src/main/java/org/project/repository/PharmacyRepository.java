package org.project.repository;

import java.util.List;

import org.project.entity.PharmacyProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface PharmacyRepository extends JpaRepository<PharmacyProductEntity, Long> {
	//find By Type 
	List<PharmacyProductEntity> findByTypeContaining(String type);  
	//find by name 
	List<PharmacyProductEntity> findByNameContaining(String name); 
	//list top 10 product for home page 
	@Query(value = "SELECT * FROM pharmacy_products LIMIT 10", nativeQuery = true) 
	List<PharmacyProductEntity> findTop10Products();
}
