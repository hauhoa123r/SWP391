package org.project.repository;

import org.project.entity.ProductEntity;
import org.project.enums.ProductLabel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ProductEntity> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<ProductEntity> findByPriceBetween(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities JOIN p.categoryEntities c WHERE c.id = :categoryId")
    Page<ProductEntity> findByCategoryEntities_Id(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities WHERE p.label = :label")
    Page<ProductEntity> findByLabel(@Param("label") ProductLabel label, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities JOIN p.productTagEntities pt WHERE LOWER(pt.id.name) = LOWER(:tag)")
    Page<ProductEntity> findByTagName(@Param("tag") String tag, Pageable pageable);

    @Query(value = "SELECT p.* FROM products p JOIN product_tags pt ON p.product_id = pt.product_id LEFT JOIN product_reviews pr ON p.product_id = pr.product_id LEFT JOIN category c ON pr.category_id = c.category_id WHERE LOWER(pt.name) LIKE LOWER(CONCAT('%', :tagName, '%'))",
            countQuery = "SELECT COUNT(*) FROM products p JOIN product_tags pt ON p.product_id = pt.product_id WHERE LOWER(pt.name) LIKE LOWER(CONCAT('%', :tagName, '%'))",
            nativeQuery = true)
    Page<ProductEntity> findByTag(@Param("tagName") String tagName, Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProductEntity p LEFT JOIN FETCH p.categoryEntities")
    List<ProductEntity> findAllWithCategory();

    @Query("SELECT MIN(p.id) FROM ProductEntity p")
    Long findFirstProductId();

    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findById(@Param("id") Long id);
}