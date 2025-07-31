package org.project.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for defining and customizing Spring beans.
 * This class ensures the proper initialization of service beans after
 * merging product and pharmacy functionality.
 */
@Configuration
@ComponentScan(basePackages = "org.project")
public class BeanConfig {

}