package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.converter.ConverterPharmacyProduct;
import org.project.entity.PharmacyProductEntity;
import org.project.model.response.PharmacyListResponse;
import org.project.repository.PharmacyRepository;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyServiceImpl implements PharmacyService {
    @Autowired
    private PharmacyRepository pharmacyRepositoryImpl;

    @Autowired
    private ConverterPharmacyProduct toConverterPharmacy;

    @Override
    public List<PharmacyListResponse> getAllPharmacies() {
    	//list contains entity with full info 
        List<PharmacyProductEntity> pharmacyProductEntity = pharmacyRepositoryImpl.findAll();
        //convert to list of entities with necessary infos 
        List<PharmacyListResponse> pharmacyListResponse = toConverterPharmacy
                .toConverterPharmacyProductList(pharmacyProductEntity);
        return pharmacyListResponse;
    }

	@Override
	public PharmacyProductEntity save(PharmacyProductEntity pharmacy) {
		// TODO Auto-generated method stub
		//check if pharmacy is null 
		if (pharmacy == null) {
			throw new NullPointerException(); 
		} 
		//save 
		PharmacyProductEntity entity = pharmacyRepositoryImpl.save(pharmacy); 
		//return 
		return entity; 
	}

	@Override
	public PharmacyListResponse findById(Long id) {
		// TODO Auto-generated method stub
		Optional<PharmacyProductEntity> pharmacy = pharmacyRepositoryImpl.findById(id); 
		//Check 
		if (pharmacy.isPresent()) {
			return toConverterPharmacy.toConverterPharmacyResponse(pharmacy.get()); 
		}
		else {
			throw new IllegalArgumentException("No pharmacy product found with Id "+id); 
		}
	}

	@Override
	public List<PharmacyListResponse> findByType(String type) {
		// TODO Auto-generated method stub
		//Get the list of type 
		List<PharmacyProductEntity> pharmacyList = new ArrayList<>(); 
		//search 
		pharmacyList = pharmacyRepositoryImpl.findByNameContaining(type); 
		//Check 
		if (pharmacyList != null) {
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList);   
		}
		return new ArrayList<>(); 
	}

	@Override
	public List<PharmacyListResponse> findByName(String name) {
		// TODO Auto-generated method stub
		//Get the list of entity has the name 
		List<PharmacyProductEntity> pharmacyList = new ArrayList<>();  
		//search 
		pharmacyList = pharmacyRepositoryImpl.findByNameContaining(name); 
		//Check 
		if (pharmacyList != null) {
			//Convert to response list
			return toConverterPharmacy.toConverterPharmacyProductList(pharmacyList); 
		}
		//If not found return empty list
		return new ArrayList<PharmacyListResponse>(); 
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		pharmacyRepositoryImpl.deleteById(id); 
		//find by id 
		Optional<PharmacyProductEntity> entity = pharmacyRepositoryImpl.findById(id); 
		//Check if entity is present 
		if (entity.isPresent()) {
			System.out.println("Delete unsucessfully"); 
		}
		System.out.println("Delete successfully"); 
	}
}
