package org.project.controller;

import org.project.model.response.PharmacyResponse;
import org.project.model.response.ReviewResponse;
import org.project.service.impl.PharmacyServiceImpl;
import org.project.service.ReviewService;
import org.project.service.ProductAdditionalInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {
    private final PharmacyServiceImpl pharmacyServiceImpl;
    private final ReviewService reviewService;
    private final ProductAdditionalInfoService additionalInfoService;

    @GetMapping({"/product-standard/", "/product-standard"})
    public ModelAndView product() {
        ModelAndView mv = new ModelAndView("product-standard");
        return mv ;
    }

    @GetMapping(value = "/product-standard", params = "id")
    public String getProductByQueryParam(@RequestParam("id") Long id, Model model) {
        // Delegate to existing method for common logic
        return getProduct(id, model);
    }

    @GetMapping("/product-standard/{id}")
    public String getProduct(@PathVariable("id") Long id, Model model) {
        log.info("Fetching product with ID: {}", id);

        // Validate ID
        if (id == null || id < 1) {
            log.warn("Invalid product ID: {}", id);
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }

        // Fetch the product by ID
        PharmacyResponse product = pharmacyServiceImpl.findById(id);
        if (product == null) {
            log.warn("No product found with ID: {}", id);
            throw new IllegalArgumentException("No product found with ID: " + id);
        }

        // Add the product to the model
        model.addAttribute("product", product);
        
        // Fetch additional information
        model.addAttribute("additionalInfos", additionalInfoService.findByProductId(id));
        // Fetch related products
        List<PharmacyResponse> relatedProducts = pharmacyServiceImpl.findRelatedProducts(id, 4); // Limit to 4 related products
        model.addAttribute("relatedProducts", relatedProducts);
        
        // Fetch reviews for the product
        List<ReviewResponse> reviews = reviewService.findReviewsByProductId(id);
        model.addAttribute("reviews", reviews);
        
        log.debug("Product {} loaded successfully with {} related products and {} reviews", id, relatedProducts.size(), reviews.size());
        return "product-standard";
    }
 }
