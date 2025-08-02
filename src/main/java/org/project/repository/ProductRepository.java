package org.project.repository;

import jakarta.persistence.EntityManager;
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {


    @Query("SELECT 1")
    default EntityManager getEntityManager() {
        throw new UnsupportedOperationException("This method should be overridden by Spring Data JPA");
    }

    // ==================== Basic product operations ====================


    Optional<ProductEntity> findByProductStatusAndId(ProductStatus productStatus, Long id);


    Page<ProductEntity> findByProductStatus(ProductStatus status, Pageable pageable);


    Long findFirstByProductStatusOrderById(ProductStatus productStatus);

    // ==================== Product filtering by type ====================


    List<ProductEntity> findAllByProductTypeAndProductStatus(ProductType productType, ProductStatus productStatus);


    List<ProductEntity> findByProductStatusAndProductType(ProductStatus productStatus, ProductType productType);


    long countByProductType(ProductType productType);


    ProductEntity findByProductTypeAndId(ProductType productType, Long id);


    boolean existsByProductTypeAndId(ProductType productType, Long id);


    Page<ProductEntity> findByProductType(ProductType productType, Pageable pageable);


    Page<ProductEntity> findByProductTypeAndNameContainingIgnoreCase(
            ProductType productType, String name, Pageable pageable);

    // ==================== Stock management ====================

    List<ProductEntity> findByStockQuantitiesLessThanEqual(Integer threshold);

    // ==================== Search operations ====================

    Page<ProductEntity> findByProductStatusAndNameContainingIgnoreCase(ProductStatus productStatus, String name, Pageable pageable);

    Page<ProductEntity> findByProductStatusAndPriceBetween(ProductStatus productStatus, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // ==================== Category and label filtering ====================

    Page<ProductEntity> findByProductStatusAndCategoryEntities_Id(ProductStatus productStatus, Long categoryId, Pageable pageable);

    Page<ProductEntity> findByProductStatusAndLabel(ProductStatus productStatus, Label label, Pageable pageable);

    // ==================== Tag operations ====================

    Page<ProductEntity> findByProductStatusAndProductTagEntities_Id_NameIgnoreCase(ProductStatus productStatus, String tagName, Pageable pageable);

    // ==================== Special queries ====================

    List<ProductEntity> findDistinctByProductStatus(ProductStatus productStatus);

    List<ProductEntity> findByProductType(String string);

    // ==================== Dashboard queries ====================


    @Query("SELECT p FROM ProductEntity p " +
            "JOIN SupplierTransactionItemEntity i ON p.id = i.productEntity.id " +
            "JOIN i.supplierTransactionEntity t " +
            "WHERE t.transactionType = org.project.enums.SupplierTransactionType.STOCK_OUT " +
            "GROUP BY p.id " +
            "ORDER BY SUM(i.quantity) DESC")
    List<ProductEntity> findTopSellingProducts(Pageable pageable);

    ProductEntity findByIdAndProductStatus(Long id, ProductStatus productStatus);
}
