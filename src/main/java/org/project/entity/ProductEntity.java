package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;

import java.math.BigDecimal;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products", schema = "swp391")
@FieldNameConstants
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Size(max = 255)
   
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;


    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Size(max = 255)

    @Column(name = "unit", nullable = false)
    private String unit;


    @Column(name = "stock_quantities", nullable = false)
    private Integer stockQuantities;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "productEntity")
    private Set<CartItemEntity> cartItemEntities;

    @OneToOne(mappedBy = "productEntity")
    private ServiceEntity serviceEntity;

    @OneToOne(mappedBy = "productEntity")
    private MedicalProductEntity medicalProductEntity;

    @OneToOne(mappedBy = "productEntity")
    private MedicineEntity medicineEntity;

    @OneToMany(mappedBy = "productEntity")
    private Set<OrderItemEntity> orderItemEntities;

    @OneToOne(mappedBy = "productEntity")
    private PricingPlanEntity pricingPlanEntity;

    @OneToMany(mappedBy = "productEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductAdditionalInfoEntity> productAdditionalInfoEntities;

    @ManyToMany(mappedBy = "productEntities")
    private Set<CategoryEntity> categoryEntities;

    @ManyToMany()
    @JoinTable(
            name = "product_reviews",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "product_review_id")
    )
    private Set<ReviewEntity> reviewEntities;

    @OneToMany(mappedBy = "productEntity")
    private Set<SupplierTransactionItemEntity> supplierTransactionItemEntities;
    @OneToMany(mappedBy = "productEntity")
    private Set<ProductTagEntity> productTagEntities;
    //@OneToOne(mappedBy = "productEntity")
    //private TestEntity testEntity;
    @ManyToMany(mappedBy = "products")
    private Set<UserEntity> userEntities;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'MEDICAL_PRODUCT'")
    @Column(name = "product_type", columnDefinition = "enum not null")
    private ProductType productType;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "product_status", columnDefinition = "enum not null")
    private ProductStatus productStatus;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'STANDARD'")
    @Column(name = "label", columnDefinition = "enum not null")
    private Label label;

    public Double getAverageRating() {
        return reviewEntities.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
    }

    public void setAverageRating(Double averageRating) {
        // This method is not needed as the average rating is calculated dynamically
        // from the reviewEntities. If you want to set a static value, you can do so,
        // but it won't reflect the actual average of the reviews.
    }

    public Long getReviewCount() {
        return (long) reviewEntities.size();
    }

    public void setReviewCount(Long reviewCount) {
        // This method is not needed as the review count is calculated dynamically
        // from the reviewEntities. If you want to set a static value, you can do so,
        // but it won't reflect the actual count of reviews.
    }
}