package org.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.model.dto.ReviewDTO;
import org.project.model.response.PharmacyListResponse;
import org.project.projection.ProductViewProjection;
import org.project.service.CartService;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ShopController {

	@Autowired
	private PharmacyService pharmacyServiceImpl;
	@Autowired
	private CartService cartService;

	@GetMapping("/shop")
	public ModelAndView shop(@RequestParam(value = "search", required = false) String search) {
		ModelAndView mv = new ModelAndView("frontend/shop");
		// If search is not empty, filter products by name
		if (search != null && !search.isEmpty()) {
			mv.addObject("products", pharmacyServiceImpl.findByName(search));
		} else {
			// If search is empty, show all products
			mv.addObject("products", pharmacyServiceImpl.getAllPharmacies());
		}

		// create cart object
		Long userId = 2l; // temporary hard-code user id, need changing after finishing login
		Long cartId =2l; //hard code, need fix later
		List<CartItemEntity> cartItems = cartService.getCart(userId);
		mv.addObject("cartItems", cartItems);
		mv.addObject("total", cartService.calculateTotal(userId));
		mv.addObject("size", cartItems.size());
		//
		mv.addObject("cartId", cartId);
		mv.addObject("userId", userId);
		return mv;
	}

	@PostMapping("/submit")
	public String search(@RequestParam("search") String search) {
		// Redirect to the shop page with the search query
		if (search != null && !search.isEmpty()) {
			return "redirect:/shop?search=" + search;
		}
		// If search is empty, redirect to the shop page without a search query
		return "redirect:/shop";
	}

	@GetMapping("/product-standard/")
	public ModelAndView product() {
		ModelAndView mv = new ModelAndView("frontend/product-standard");
		return mv;
	}

	@GetMapping("/product-standard/{id}")
	public String getProduct(@PathVariable("id") Long id, Model model) {
		// Check if the ID is null or less than 1
		if (id == null || id < 1) {
			throw new IllegalArgumentException("Invalid product ID: " + id);
		}
		// Fetch the product by ID
//		PharmacyListResponse product = pharmacyServiceImpl.findById(id);
		List<ProductViewProjection> product = pharmacyServiceImpl.findAllProductsWithFullInfo(id);
		// Check if the product is null
		if (product == null) {
			throw new IllegalArgumentException("No product found with ID: " + id);
		}
		// Map of additional information (name-value pairs)
		Map<String, String> additionalInfo = new HashMap<>();
		// List of categories name
		Set<String> categories = new HashSet<>();
		// set of tag
		Set<String> tags = new HashSet<>();
		// Set of reviews with linkedHashSet to avoid duplicates and keep insertion
		// order
		Set<ReviewDTO> reviews = new LinkedHashSet<>();
		// List of related products
		List<PharmacyListResponse> relatedProducts = pharmacyServiceImpl
				.findRandomProductsByType(product.get(0).getType());
		// iterate through the product's additional information and add it to the map
		for (ProductViewProjection info : product) {
			// Get name
			String name = info.getAdditionalInfoName();
			// Get value
			String value = info.getAdditionalInfoValue();
			// set naem of categories
			String categoryName = info.getCategoryName();
			// set the tag name
			String tagName = info.getTagName();
			// set the string of review content
			String reviewContent = info.getReviewContent();
			// If name is not null, add to the map
			if (name != null && !name.isEmpty()) {
				// add to the map
				additionalInfo.put(name, value);
			}
			// If category name is not null, add to the list
			if (categoryName != null && !categoryName.isEmpty()) {
				// add to the list
				categories.add(categoryName);
			} else {
				categories.add("N/A"); // If no category, add "N/A"
			}
			// If tag name is not null, add to the set
			if (tagName != null && !tagName.isEmpty()) {
				tags.add(tagName);
			} else {
				tags.add("N/A"); // If no tag, add "N/A"
			}
			// If review content is not null, create a ReviewDTO and add to the list
			if (reviewContent != null && !reviewContent.isEmpty()) {
				// Create a ReviewDTO object
				ReviewDTO review = new ReviewDTO();
				// Set the patient full name
				review.setPatientFullName(info.getPatientFullName());
				// Set the patient image URL
				review.setPatientImageUrl(info.getPatientAvatarUrl());
				// Set the content of the review
				review.setContent(reviewContent);
				// Set the rating of the review
				review.setRating(info.getReviewRating());
				// Add the review to the list
				reviews.add(review);
			}
			// If review content is null, add a default review
			else {
				ReviewDTO review = new ReviewDTO();
				review.setPatientFullName("Anonymous");
				review.setPatientImageUrl("/frontend_assets/assets/images/general/avatar.png");
				review.setContent("No reviews yet.");
				review.setRating(0);
				reviews.add(review);
			}
		}
		// join the categories set to a string with commas
		String categoriesString = String.join(", ", categories);
		// join the tags set to a string with commas
		String tagsString = String.join(", ", tags);
		// If categories is empty, add "N/A"
		if (categories.isEmpty()) {
			categories.add("N/A");
		}
		// Add the product to the model
		model.addAttribute("product", product.get(0)); // Assuming the projection returns a list, take the first item
		// add the additional information map to the model
		model.addAttribute("additionalInfo", additionalInfo);
		// add the categories list to the model
		model.addAttribute("categories", categories);
		// add the categories string to the model
		model.addAttribute("categoriesString", categoriesString);
		// add the tags string to the model
		model.addAttribute("tagsString", tagsString);
		// Add the reviews list to the model
		model.addAttribute("reviews", reviews);
		// Add the related products to the model
		model.addAttribute("relatedProducts", relatedProducts);
		// Return the view name
		return "frontend/product-standard";
	}

	@GetMapping("/product-home")
	public ModelAndView productHome() {
		ModelAndView mv = new ModelAndView("frontend/product-home");
		// Fetch top 10 products for the home page
		mv.addObject("products", pharmacyServiceImpl.findTop10Products());
		return mv;
	}

	@PostMapping("/shop/add-to-cart")
	public String addToCart(@RequestParam("userId") Long userId, @RequestParam("quantity") int quantity,
			@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
		userId=2l;
		if (quantity <= 0) {
	        redirectAttributes.addFlashAttribute("error", "Please enter a valid quantity.");
	        return "redirect:/shop";
	    }
		cartService.addItem(userId, productId, 1); // default quantity = 1

        redirectAttributes.addFlashAttribute("message", "Item added to cart!");
        return "redirect:/shop"; 
	}
//
//	@GetMapping("/checkout")
//	public ModelAndView checkout() {
//		ModelAndView mv = new ModelAndView("frontend/checkout");
//		return mv;
//	}

	@GetMapping("/wishlist")
	public ModelAndView wishlist() {
		ModelAndView mv = new ModelAndView("frontend/wishlist");
		return mv;
	}

	@GetMapping("/my-account")
	public ModelAndView myAccount() {
		ModelAndView mv = new ModelAndView("frontend/my-account");
		return mv;
	}

	@GetMapping("/track-order")
	public ModelAndView trackOrder() {
		ModelAndView mv = new ModelAndView("frontend/track-order");
		return mv;
	}

	@GetMapping("/product-new")
	public ModelAndView productNew() {
		ModelAndView mv = new ModelAndView("frontend/product-new");
		return mv;
	}

	@GetMapping("/product-sale")
	public ModelAndView productSale() {
		ModelAndView mv = new ModelAndView("frontend/product-sale");
		return mv;
	}

	@GetMapping("/shop-left-sidebar")
	public ModelAndView shopLeftSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-left-sidebar");
		return mv;
	}

	@GetMapping("/shop-right-sidebar")
	public ModelAndView shopRightSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-right-sidebar");
		return mv;
	}

	@GetMapping("/shop-no-sidebar")
	public ModelAndView shopNoSidebar() {
		ModelAndView mv = new ModelAndView("frontend/shop-no-sidebar");
		return mv;
	}
}
