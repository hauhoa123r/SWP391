package org.project.repository;

import org.project.entity.ProductCategoryEntity;
import org.project.entity.ProductEntity;
import org.project.entity.ProductTag;
import org.project.enums.ProductLabel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ProductEntity> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM ProductEntity p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<ProductEntity> findByPriceBetween(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT p FROM ProductEntity p WHERE p.category = :category")
    List<ProductEntity> findAllByCategory(Long category);

    @Query("SELECT p FROM ProductEntity p WHERE p.label = :label")
    List<ProductEntity> findByLabel(@Param("label") ProductLabel label);

    @Query("SELECT p FROM ProductEntity p WHERE EXISTS (SELECT t FROM ProductTag t WHERE t.productEntity = p AND LOWER(t.name) = LOWER(:tagName))")
    List<ProductEntity> findByTag(@Param("tagName") String tagName);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:label IS NULL OR p.label = :label) AND " +
            "(:tagName IS NULL OR EXISTS (SELECT t FROM ProductTag t WHERE t.productEntity = p AND t.name = :tagName)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<ProductEntity> advancedSearch(
            @Param("name") String name,
            @Param("label") ProductLabel label,
            @Param("tagName") String tagName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.pharmacyInventoryEntities WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COALESCE(SUM(inv.currentStock), 0) FROM PharmacyInventoryEntity inv WHERE inv.productEntity.id = :productId")
    Integer checkInventoryQuantity(@Param("productId") Long productId);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.category")
    List<ProductEntity> findAllWithCategory();


}
