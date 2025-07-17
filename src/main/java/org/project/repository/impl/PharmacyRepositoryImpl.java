package org.project.repository.impl;

import java.util.List;

import org.project.converter.ConverterPharmacyProduct;
import org.project.model.dto.ProductViewDTO;
import org.project.repository.impl.custom.PharmacyRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PharmacyRepositoryImpl implements PharmacyRepositoryCustom {
	// Implement custom methods for PharmacyRepository here
	// For example, methods to find pharmacies by specific criteria, etc.
	// This class can provide the custom logic for the methods defined in
	// PharmacyRepositoryCustom interface.

	// Inject any necessary dependencies here, such as EntityManager or other
	// repositories
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private ConverterPharmacyProduct converterPharmacy;

	// Example of a custom method implementation
	@Override
	public List<ProductViewDTO> getPagedProducts(int limit, int offset) {
		/*
		 * String sql = """ SELECT p.product_id, p.name, GROUP_CONCAT(c.name SEPARATOR
		 * ', ') AS category_names, p.price, p.stock_quantities, p.label,
		 * p.produt_status FROM products p LEFT JOIN product_categories pc ON
		 * p.product_id = pc.product_id LEFT JOIN categories c ON pc.category_id =
		 * c.category_id GROUP BY p.product_id, p.name, p.price, p.stock_quantities,
		 * p.label ORDER BY p.product_id """;
		 */
		String sql = "SELECT DISTINCT \r\n" + "    p.product_id,\r\n" + "    p.product_type AS type,\r\n"
				+ "    p.name,\r\n" + "    p.description,\r\n" + "    p.price,\r\n" + "    p.unit,\r\n"
				+ "    p.product_status,\r\n" + "    p.stock_quantities,\r\n" + "    p.image_url,\r\n"
				+ "    p.label,\r\n" + "\r\n"
				+ "    GROUP_CONCAT(DISTINCT c.name SEPARATOR ', ') AS category_names, \r\n"
				+ "    GROUP_CONCAT(DISTINCT pt.name SEPARATOR ', ') AS tag_names,\r\n"
				+ "    GROUP_CONCAT(DISTINCT CONCAT(pai.name, ': ', pai.value) SEPARATOR ', ') AS additional_infos\r\n"
				+ "\r\n" + "FROM swp391.products p\r\n" + "\r\n"
				+ "LEFT JOIN swp391.product_categories pc ON p.product_id = pc.product_id\r\n"
				+ "LEFT JOIN swp391.categories c ON pc.category_id = c.category_id\r\n" + "\r\n"
				+ "LEFT JOIN swp391.product_tags pt ON p.product_id = pt.product_id\r\n" + "\r\n"
				+ "LEFT JOIN swp391.product_additional_infos pai ON p.product_id = pai.product_id\r\n" + "\r\n"
				+ "GROUP BY p.product_id, p.product_type, p.name, p.description, p.price, p.unit, \r\n"
				+ "         p.product_status, p.stock_quantities, p.image_url, p.label;";
		// Create a native query using the EntityManager
		Query query = entityManager.createNativeQuery(sql);
		// Set the pagination parameters
		query.setFirstResult(offset); // OFFSET
		// Set the maximum number of results to return
		query.setMaxResults(limit); // LIMIT
		// Execute the query and return the results
		List<Object[]> results = query.getResultList();
		// map the results to ProductViewDTO or any other DTO as needed
		return results.stream().map(converterPharmacy::toConverterProductViewDTO).toList();
	}

}
