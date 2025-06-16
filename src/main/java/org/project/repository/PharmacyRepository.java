package org.project.repository;

import java.util.List;
import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.projection.ProductViewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyRepository extends JpaRepository<ProductEntity, Long> {
	// find By Type
	List<ProductEntity> findByProductTypeContaining(ProductType type);

	// find by name
	List<ProductEntity> findByNameContaining(String name);

	// list top 10 product for home page
	@Query(value = "select * from (\r\n"
			+ "	SELECT * FROM swp391.medical_products mp join swp391.products p on mp.medical_product_id = p.product_id LIMIT 5\r\n"
			+ ") as A \r\n" + "UNION \r\n" + "Select * from (\r\n"
			+ "	SELECT * FROM swp391.medicines m join swp391.products p on m.medicine_id = p.product_id LIMIT 5\r\n"
			+ ") as B", nativeQuery = true)
	List<ProductEntity> findTop10Products();

	// Find products with full information including category, tag, additional info
	@Query(value="SELECT\r\n"
			+ "    p.product_id,\r\n"
			+ "    p.product_type as type,\r\n"
			+ "    p.name,\r\n"
			+ "    p.description,\r\n"
			+ "    p.price,\r\n"
			+ "    p.unit,\r\n"
			+ "    p.product_status,\r\n"
			+ "    p.stock_quantities,\r\n"
			+ "    p.image_url,\r\n"
			+ "    p.label,\r\n"
			+ "\r\n"
			+ "    pc.category_id,\r\n"
			+ "    pt.name AS tag_name,\r\n"
			+ "    pai.name AS additional_info_name,\r\n"
			+ "    pai.value AS additional_info_value\r\n"
			+ "\r\n"
			+ "FROM products p\r\n"
			+ "-- Join với product_categories\r\n"
			+ "LEFT JOIN product_categories pc ON p.product_id = pc.product_id\r\n"
			+ "-- Join với product_tags\r\n"
			+ "LEFT JOIN product_tags pt ON p.product_id = pt.product_id\r\n"
			+ "-- Join với product_additional_infos\r\n"
			+ "LEFT JOIN product_additional_infos pai ON p.product_id = pai.product_id\r\n"
			+ "WHERE p.product_type like \"%M%\"\r\n"
			+ "AND p.product_id = :product_id\r\n"
			+ "", nativeQuery = true) 
	List<ProductViewProjection> findAllProductsWithFullInfo(@Param("product_id") Long id); 
}
