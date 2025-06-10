package org.project.service;


import org.project.entity.ProductEntity;
import org.project.enums.ProductType;
import org.project.model.response.PharmacyListResponse;
import org.springframework.stereotype.Service;

import java.util.List;


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
    //select top 10 products for home page 
    List<PharmacyListResponse> findTop10Products(); 
}
