package org.project.repository;

import org.project.entity.Product;
import org.project.model.response.LowStockProductDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT new org.project.model.response.LowStockProductDTO(p.productId, p.name, p.stockQuantities) " +
           "FROM Product p WHERE p.stockQuantities <= :threshold")
    List<LowStockProductDTO> findProductsBelowStock(@Param("threshold") int threshold);
}
