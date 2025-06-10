package org.project.repository;

import java.util.List; 
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface PharmacyRepository extends JpaRepository<ProductEntity, Long> {
	//find By Type 
	List<ProductEntity> findByProductTypeContaining(ProductType type);  
	//find by name 
	List<ProductEntity> findByNameContaining(String name); 
	//list top 10 product for home page 
	@Query(value = "select * from (\r\n"
			+ "	SELECT * FROM swp391.medical_products mp join swp391.products p on mp.medical_product_id = p.product_id LIMIT 5\r\n"
			+ ") as A \r\n"
			+ "UNION \r\n"
			+ "Select * from (\r\n"
			+ "	SELECT * FROM swp391.medicines m join swp391.products p on m.medicine_id = p.product_id LIMIT 5\r\n"
			+ ") as B", nativeQuery = true) 
	List<ProductEntity> findTop10Products();
}
