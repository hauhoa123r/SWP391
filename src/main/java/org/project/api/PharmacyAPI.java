package org.project.api;

import org.project.model.response.PharmacyListResponse;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/shop")
public class PharmacyAPI {

    @Autowired
    private PharmacyService pharmacyService;

    @GetMapping("/search/name")
    public List<PharmacyListResponse> searchByName(@RequestParam String name) {
        return pharmacyService.searchByName(name);
    }

    @GetMapping("/search/type")
    public List<PharmacyListResponse> searchByType(@RequestParam String type) {
        return pharmacyService.searchByType(type);
    }

    @GetMapping("/search/price")
    public List<PharmacyListResponse> searchByPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
        return pharmacyService.searchByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/search/advanced")
    public List<PharmacyListResponse> advancedSearch(@RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String type,
                                                    @RequestParam(required = false) BigDecimal minPrice,
                                                    @RequestParam(required = false) BigDecimal maxPrice) {
        return pharmacyService.advancedSearch(name, type, minPrice, maxPrice);
    }

}
