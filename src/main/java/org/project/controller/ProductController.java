package org.project.controller;

import org.project.model.response.PharmacyResponse;
import org.project.model.response.ReviewResponse;
import org.project.service.ProductService;
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

/**
 * Controller for handling product display operations
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ReviewService reviewService;
    private final ProductAdditionalInfoService additionalInfoService;

    /**
     * Handle request to product standard page without parameters
     * @return ModelAndView for product standard page
     */
    @GetMapping({"/product-standard/", "/product-standard"})
    public ModelAndView product() {
        log.debug("Accessing product standard page without parameters");
        ModelAndView mv = new ModelAndView("product-standard");
        return mv;
    }

    /**
     * Handle request for product by query parameter
     * @param id Product ID
     * @param model Spring UI model
     * @return View name for product standard page
     */
    @GetMapping(value = "/product-standard", params = "id")
    public String getProductByQueryParam(@RequestParam("id") Long id, Model model) {
        log.debug("Accessing product standard page with ID parameter: {}", id);
        // Delegate to existing method for common logic
        return getProduct(id, model);
    }

    /**
     * Handle request for product by path variable
     * @param id Product ID
     * @param model Spring UI model
     * @return View name for product standard page
     */
    @GetMapping("/product-standard/{id}")
    public String getProduct(@PathVariable("id") Long id, Model model) {
        log.info("Fetching product with ID: {}", id);

        // Validate ID
        if (id == null || id < 1) {
            log.warn("Invalid product ID: {}", id);
            throw new IllegalArgumentException("Invalid product ID: " + id);
        }

        // Fetch the product by ID
        PharmacyResponse product = productService.findProductById(id);
        if (product == null) {
            log.warn("No product found with ID: {}", id);
            throw new IllegalArgumentException("No product found with ID: " + id);
        }

        // Add the product to the model
        model.addAttribute("product", product);
        
        // Fetch additional information
        model.addAttribute("additionalInfos", additionalInfoService.findByProductId(id));
        
        // Fetch related products
        List<PharmacyResponse> relatedProducts = productService.findRelatedProducts(id, 4); // Limit to 4 related products
        model.addAttribute("relatedProducts", relatedProducts);
        
        // Fetch reviews for the product
        List<ReviewResponse> reviews = reviewService.findReviewsByProductId(id);
        model.addAttribute("reviews", reviews);
        
        log.debug("Product {} loaded successfully with {} related products and {} reviews", 
                 id, relatedProducts.size(), reviews.size());
        
        return "product-standard";
    }
}
