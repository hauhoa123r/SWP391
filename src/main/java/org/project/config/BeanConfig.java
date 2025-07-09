package org.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.project.service.ProductService;
import org.project.service.impl.ProductServiceImpl;
import org.project.repository.ProductRepository;
import org.project.converter.ConverterPharmacyProduct;

/**
 * Configuration class for defining and customizing Spring beans.
 * This class ensures the proper initialization of service beans after
 * merging product and pharmacy functionality.
 */
@Configuration
@ComponentScan(basePackages = "org.project")
public class BeanConfig {

    /**
     * Creates and configures the primary ProductService bean.
     * This method ensures that after merging PharmacyService functionality into ProductService,
     * there is a single unified implementation available for dependency injection.
     *
     * @param productRepository Repository for product data access
     * @param converter Converter for transforming product entities to DTOs
     * @return Configured ProductService bean
     */
    @Bean
    @Primary
    public ProductService productService(ProductRepository productRepository, ConverterPharmacyProduct converter) {
        return new ProductServiceImpl(productRepository, converter);
    }
} 