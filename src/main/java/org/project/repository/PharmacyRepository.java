package org.project.repository;

import org.project.entity.PharmacyProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<PharmacyProductEntity, Long> {

    @Query("SELECT p FROM PharmacyProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PharmacyProductEntity> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM PharmacyProductEntity p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<PharmacyProductEntity> findByPriceBetween(
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice
    );

    // Sửa lại cho đúng signature: tìm chính xác theo type
    List<PharmacyProductEntity> findByType(String type);

    // Advanced search
    @Query("SELECT p FROM PharmacyProductEntity p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<PharmacyProductEntity> advancedSearch(
        @Param("name") String name,
        @Param("type") String type,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );

    // Lấy chi tiết thuốc (có thể join fetch inventory nếu cần)
    @Query("SELECT p FROM PharmacyProductEntity p WHERE p.id = :id")
    Optional<PharmacyProductEntity> findByIdWithDetails(@Param("id") Long id);

    // Kiểm tra số lượng tồn kho (giả sử có PharmacyInventoryEntity liên kết)
    @Query("SELECT COALESCE(SUM(inv.currentStock), 0) FROM PharmacyInventoryEntity inv WHERE inv.pharmacyProductEntity.id = :productId")
    Integer checkInventoryQuantity(@Param("productId") Long productId);
}
