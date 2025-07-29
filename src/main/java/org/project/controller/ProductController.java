package org.project.controller;

import org.project.model.dto.ProductDetailDTO;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProductController {

    //inject pharmacyService
    @Autowired
    private PharmacyService pharmacyServiceImpl;


    @GetMapping("/product-standard/{id}")
    public String getProductDetail(@PathVariable("id") Long productId, Model model) {
        // get detailDTO by id
        ProductDetailDTO detailDTO = pharmacyServiceImpl.getProductDetailById(productId);
        // format categories to string
        String categoriesString = String.join(", ", detailDTO.getCategories());
        // format tags to String
        String tagsString = String.join(", ", detailDTO.getTags());
        // Check if categories is empty
        if (detailDTO.getCategories().isEmpty()) {
            categoriesString = "N/A";
        }
        // Check if no tags
        if (detailDTO.getTags().isEmpty()) {
            tagsString = "N/A";
        }
        // add product attribute
        model.addAttribute("product", detailDTO.getProduct());
        // add additional info
        model.addAttribute("additionalInfo", detailDTO.getAdditionalInfos());
        // add categories
        model.addAttribute("categories", detailDTO.getCategories());
        // add categories String
        model.addAttribute("categoriesString", categoriesString);
        // add tags
        model.addAttribute("tags", detailDTO.getTags());
        // add tagsString
        model.addAttribute("tagsString", tagsString);
        // add reviews
        model.addAttribute("reviews", detailDTO.getReviews());
        // add related product
        model.addAttribute("relatedProducts",
                pharmacyServiceImpl.findRandomProductsByType(detailDTO.getProduct().getType().toString()));
        // Return the view name
        return "product-standard";
    }

    @GetMapping("/product-home")
    public ModelAndView productHome() {
        ModelAndView mv = new ModelAndView("product-home");
        // Fetch top 10 products for the home page
        mv.addObject("products", pharmacyServiceImpl.findTop10Products());
        return mv;
    }
}
