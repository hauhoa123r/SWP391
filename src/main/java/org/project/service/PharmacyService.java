package org.project.service;


import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.model.dto.ProductCreateDTO;
import org.project.model.dto.ProductDetailDTO;
import org.project.model.dto.ProductUpdateDTO;
import org.project.model.response.PharmacyListResponse;
import org.project.projection.ProductViewProjection;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface PharmacyService {
	//Create 
	ProductEntity save(ProductEntity pharmacy); 
	//View all
    List<PharmacyListResponse> getAllPharmacies();
    //view by id 
    PharmacyListResponse findById(Long id); 
    //View all by type 
    List<PharmacyListResponse> findByProductType(ProductType type); 
    //view by name 
    List<PharmacyListResponse> findByName(String name); 
    //Delete by id 
    void deleteById(Long id); 
    //soft delete by id 
    void softDeleteById(Long id); 
    //select top 10 products for home page 
    List<PharmacyListResponse> findTop10Products(); 
    //Select all products with full information including category, tag, additional info 
    List<ProductViewProjection> findAllProductsWithFullInfo(Long id); 
    //Select 4 products based on categoryName 
    List<PharmacyListResponse> findRandomProductsByType(String productType); 
    // Get the number of products 
    Long countProducts(); 
    //find product with full details (improved) 
    ProductDetailDTO getProductDetailById(Long productId);  
    //save product from dto 
    ProductEntity saveFromDTO(ProductCreateDTO dto); 
    //add categories to product 
    void addCategoriesToProduct(ProductEntity product, Set<Long> categoryIds); 
    //Get product update DTO by id 
    ProductUpdateDTO getProductUpdateDetailById(Long productId); 
    //update product from updateDTO 
    ProductEntity updateProductFromDTO(ProductUpdateDTO dto);

}
