package org.project.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.entity.CategoryEntity;
import org.project.entity.ProductEntity;
import org.project.entity.ProductTagEntity;
import org.project.entity.ReviewEntity;
import org.project.enums.ProductLabel;
import org.project.enums.ProductStatus;
import org.project.model.response.PharmacyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConverterPharmacyProductTest {

    @Autowired
    private ConverterPharmacyProduct converter;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        converter = new ConverterPharmacyProduct();
        product = new ProductEntity();
        product.setId(1L);
        product.setName("Paracetamol");
        product.setDescription("Pain reliever");
        product.setPrice(new BigDecimal("10.00"));
        product.setProductStatus(ProductStatus.ACTIVE);
        product.setStockQuantities(100);
        product.setImageUrl("http://example.com/image.jpg");

        CategoryEntity category = new CategoryEntity();
        category.setName("Medicine");
        product.setCategoryEntities(Collections.singleton(category));


        ReviewEntity review = new ReviewEntity();
        review.setRating((int) 4.5);
        product.setReviewEntities((java.util.Set<ReviewEntity>) Arrays.asList(review));
    }

    @Test
    void convert_validProductEntity_returnsCorrectDto() {
        PharmacyResponse result = converter.toDto(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Paracetamol", result.getName());
        assertEquals("Pain reliever", result.getDescription());
        assertEquals(new BigDecimal("10.00"), result.getPrice());
        assertEquals(4.5, result.getRating());
        assertEquals(ProductStatus.ACTIVE.name(), result.getStatus());
        assertEquals(100, result.getStockQuantity());
        assertEquals("Medicine", result.getCategory());
        assertEquals(Collections.singletonList("Painkiller"), result.getTags());
        assertEquals("http://example.com/image.jpg", result.getImageUrl());
    }

    @Test
    void convert_noReviews_returnsZeroRating() {
        product.setReviewEntities(Collections.emptySet());

        PharmacyResponse result = converter.toDto(product);

        assertEquals(0.0, result.getRating());
    }

    @Test
    void convert_noCategory_returnsNullCategory() {
        product.setCategoryEntities(Collections.emptySet());

        PharmacyResponse result = converter.toDto(product);

        assertNull(result.getCategory());
    }

    @Test
    void convert_noTags_returnsNullTags() {
        product.setProductTagEntities(Collections.emptySet());

        PharmacyResponse result = converter.toDto(product);

        assertNull(result.getTags());
    }
}