package org.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.project.service.ProductService;
import org.project.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.project.utils.specification.SpecificationUtils;
import org.project.entity.ProductEntity;
import org.project.service.MedicalProductService;
import org.project.repository.MedicalProductRepository;
import org.project.service.impl.MedicalProductServiceImpl;
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

}