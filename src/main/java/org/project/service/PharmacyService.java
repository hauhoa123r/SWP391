package org.project.service;


import org.project.entity.PharmacyProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PharmacyService {
	//Create 
	PharmacyProductEntity save(PharmacyProductEntity pharmacy); 
	//View all
    List<PharmacyListResponse> getAllPharmacies();
    //view by id 
    PharmacyListResponse findById(Long id); 
    //View all by type 
    List<PharmacyListResponse> findByType(String type); 
    //view by name 
    List<PharmacyListResponse> findByName(String name); 
    //Delete by id 
    void deleteById(Long id); 
}
