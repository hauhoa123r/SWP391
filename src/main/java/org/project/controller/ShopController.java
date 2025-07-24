package org.project.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.ProductSortType;
import org.project.model.dto.ProductDetailDTO;
import org.project.model.response.CategoryListResponse;
import org.project.model.response.PharmacyResponse;
import org.project.service.CategoryService;
import org.project.service.ProductService;
import org.project.service.WishlistService;
import org.project.repository.ProductTagRepository;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling shop related operations including product listings,
 * searching, and filtering.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ShopController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductTagRepository productTagRepository;
    private final WishlistService wishlistService;

    private static final String PREVIOUS_SEARCH_KEY = "previousSearch";

    /**
     * Main shop page with filtering and searching capabilities
     * @param searchQuery Optional search query for product names
     * @param sortType Optional sort type for results
     * @param page Page number (zero-based)
     * @param size Number of items per page
     * @param categoryId Optional category ID filter
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param tagRaw Optional tag filter
     * @param session HTTP session for maintaining search context
     * @return ModelAndView for shop page with search results
     */
    @GetMapping("/shop")
    public ModelAndView shop(
            @RequestParam(value = "search", required = false) String searchQuery,
            @RequestParam(value = "sort", required = false) ProductSortType sortType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "tag", required = false) String tagRaw,
            @RequestParam(value = "label", required = false) String label,
            HttpSession session) {
        log.info("Processing shop request: searchQuery={}, sortType={}, page={}, size={}, categoryId={}, minPrice={}, maxPrice={}, tag={}, label={}",
                searchQuery, sortType, page, size, categoryId, minPrice, maxPrice, tagRaw, label);

        ModelAndView mv = new ModelAndView("frontend/shop");
        
        // Convert "%23" back to "#" for tag searches if needed
        String tagProcessed = tagRaw != null ? tagRaw.replace("%23", "#") : null;
        
        // Log tag information for debugging
        if (tagProcessed != null && !tagProcessed.isEmpty()) {
            log.info("Processing tag filter: {}", tagProcessed);
        }
        
        SearchContext context = buildSearchContext(searchQuery, sortType, session);

        // Search for products with all filters applied
        Page<PharmacyResponse> productPage = productService.searchProducts(
                context.searchQuery(), Optional.ofNullable(categoryId),
                Optional.ofNullable(minPrice), Optional.ofNullable(maxPrice),
                Optional.ofNullable(tagProcessed), Optional.ofNullable(label),
                context.sortType(), PageRequest.of(page, size));
        
        log.info("Search results: found {} products", productPage.getTotalElements());

        // Load categories for sidebar
        List<CategoryListResponse> categories = loadCategoriesWithProductCount();

        // Get all available tags for the sidebar
        List<String> availableTags = productTagRepository.findAllDistinctTagNames();
        log.info("Available tags: {}", availableTags);

        // Add all data to model
        mv.addObject("products", productPage.getContent());
        mv.addObject("productPage", productPage);
        mv.addObject("selectedSort", context.sortType());
        mv.addObject("search", context.searchQuery().orElse(null));
        mv.addObject("categories", categories);
        mv.addObject("categoryId", categoryId);
        mv.addObject("size", size);
        mv.addObject("page", page);
        
        // Sidebar data
        mv.addObject("topProducts", productService.findTop10Products());
        mv.addObject("tags", availableTags);
        mv.addObject("minPrice", minPrice);
        mv.addObject("maxPrice", maxPrice);
        mv.addObject("selectedTag", tagProcessed);
        mv.addObject("selectedLabel", label);
        
        log.debug("Shop page prepared with {} products", productPage.getNumberOfElements());
        return mv;
    }

    /**
     * Process search form submission
     * @param searchQuery Optional search query
     * @param sortType Optional sort type
     * @param categoryId Optional category ID to preserve
     * @param tag Optional tag to preserve
     * @param minPrice Optional minimum price to preserve
     * @param maxPrice Optional maximum price to preserve
     * @return Redirect to shop page with search parameters
     */
    @PostMapping("/submit")
    public String search(@RequestParam(value = "search", required = false) String searchQuery,
                         @RequestParam(value = "sort", required = false) String sortType,
                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                         @RequestParam(value = "tag", required = false) String tag,
                         @RequestParam(value = "label", required = false) String label,
                         @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                         @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice) {
        log.debug("Processing search form submission: search={}, sort={}, categoryId={}, tag={}, minPrice={}, maxPrice={}",
                searchQuery, sortType, categoryId, tag, minPrice, maxPrice);
        
        StringBuilder redirectUrl = new StringBuilder("redirect:/shop");
        boolean hasParam = false;

        // Add search parameter if valid
        if (isValid(searchQuery)) {
            redirectUrl.append("?search=").append(searchQuery.trim());
            hasParam = true;
        }

        // Add sort parameter if valid
        if (isValid(sortType)) {
            redirectUrl.append(hasParam ? "&" : "?").append("sort=").append(sortType);
            hasParam = true;
        }
        
        // Add category parameter if valid
        if (categoryId != null) {
            redirectUrl.append(hasParam ? "&" : "?").append("categoryId=").append(categoryId);
            hasParam = true;
        }
        
        // Add tag parameter if valid
        if (isValid(tag)) {
            redirectUrl.append(hasParam ? "&" : "?").append("tag=").append(tag);
            hasParam = true;
        }
        
        // Add label parameter if valid
        if (isValid(label)) {
            redirectUrl.append(hasParam ? "&" : "?").append("label=").append(label);
            hasParam = true;
        }
        
        // Add price parameters if valid
        if (minPrice != null) {
            redirectUrl.append(hasParam ? "&" : "?").append("minPrice=").append(minPrice);
            hasParam = true;
        }
        
        if (maxPrice != null) {
            redirectUrl.append(hasParam ? "&" : "?").append("maxPrice=").append(maxPrice);
        }

        return redirectUrl.toString();
    }

    /**
     * Product home page displaying top products
     * @return ModelAndView for product home page
     */
//    @GetMapping("/product-home")
//    public ModelAndView productHome() {
//        log.info("Loading product home page");
//
//        ModelAndView mv = new ModelAndView("product-home");
//
//        try {
//            // Fetch top 10 products for the home page
//            List<PharmacyResponse> products = productService.findTop10Products();
//
//            // Validate returned list
//            if (products == null) {
//                log.warn("findTop10Products returned null, using empty list");
//                products = Collections.emptyList();
//            } else if (products.isEmpty()) {
//                log.debug("No products found, returning empty list");
//            } else {
//                log.debug("Loaded {} products for home page", products.size());
//            }
//
//            // Add products to model
//            mv.addObject("products", products);
//
//        } catch (Exception e) {
//            log.error("Error fetching top 10 products: {}", e.getMessage(), e);
//            // Return empty list on error to avoid rendering issues
//            mv.addObject("products", Collections.emptyList());
//        }
//
//        return mv;
//    }

    // ==================== Other page mappings ====================

    /**
     * Checkout page
     * @return ModelAndView for checkout page
     */
    @GetMapping("/checkout")
    public ModelAndView checkout() {
        log.debug("Accessing checkout page");
        return new ModelAndView("frontend/checkout");
    }

    /**
     * User account page
     * @return ModelAndView for account page
     */
    @GetMapping("/my-account")
    public ModelAndView myAccount() {
        log.debug("Accessing my account page");
        return new ModelAndView("frontend/my-account");
    }

    /**
     * Order tracking page
     * @return ModelAndView for order tracking page
     */
    @GetMapping("/track-order")
    public ModelAndView trackOrder() {
        log.debug("Accessing track order page");
        return new ModelAndView("frontend/track-order");
    }

    /**
     * New products page
     * @return ModelAndView for new products page
     */
    @GetMapping("/product-new")
    public ModelAndView productNew() {
        log.debug("Accessing new products page");
        return new ModelAndView("frontend/product-new");
    }

    /**
     * Product sale page
     * @return ModelAndView for product sale page
     */
    @GetMapping("/product-sale")
    public ModelAndView productSale() {
        log.debug("Accessing product sale page");
        return new ModelAndView("frontend/product-sale");
    }

    /**
     * Shop page with left sidebar layout
     * @return ModelAndView for shop page with left sidebar
     */
    @GetMapping("/shop-left-sidebar")
    public ModelAndView shopLeftSidebar() {
        log.debug("Accessing shop with left sidebar");
        return new ModelAndView("frontend/shop-left-sidebar");
    }

    /**
     * Shop page with right sidebar layout
     * @return ModelAndView for shop page with right sidebar
     */
    @GetMapping("/shop-right-sidebar")
    public ModelAndView shopRightSidebar() {
        log.debug("Accessing shop with right sidebar");
        return new ModelAndView("frontend/shop-right-sidebar");
    }

    /**
     * Shop page without sidebar layout
     * @return ModelAndView for shop page without sidebar
     */
    @GetMapping("/shop-no-sidebar")
    public ModelAndView shopNoSidebar() {
        log.debug("Accessing shop with no sidebar");
        return new ModelAndView("frontend/shop-no-sidebar");
    }

    // ==================== Helper methods ====================
    
    /**
     * Build search context from parameters and session state
     * @param searchQuery Search query string
     * @param sortType Sort type
     * @param session HTTP session
     * @return Search context containing normalized query and sort type
     */
    private SearchContext buildSearchContext(String searchQuery, ProductSortType sortType, HttpSession session) {
        String normalizedQuery = normalizeQuery(searchQuery);
        String previousSearch = (String) session.getAttribute(PREVIOUS_SEARCH_KEY);
        boolean isNewSearch = normalizedQuery != null && !normalizedQuery.equals(previousSearch);

        ProductSortType finalSortType = sortType != null ? sortType : ProductSortType.DEFAULT;
        if (isNewSearch) {
            finalSortType = ProductSortType.DEFAULT;
            session.setAttribute(PREVIOUS_SEARCH_KEY, normalizedQuery);
        } else if (normalizedQuery == null) {
            session.removeAttribute(PREVIOUS_SEARCH_KEY);
        }

        return new SearchContext(Optional.ofNullable(normalizedQuery), finalSortType);
    }

    /**
     * Load categories with product count for sidebar
     * @return List of categories with product count
     */
    private List<CategoryListResponse> loadCategoriesWithProductCount() {
        // Only keep categories that have at least one product to display in sidebar
        List<CategoryListResponse> categories = categoryService.findAllCategory();
        categories.forEach(category -> category.setProductCount(
                String.valueOf(categoryService.countProductsByCategory(category.getId()))));
        
        // Filter out categories with no products
        return categories.stream()
                .filter(c -> !"0".equals(c.getProductCount()))
                .toList();
    }

    /**
     * Normalize a search query
     * @param query Raw search query
     * @return Normalized query or null if invalid
     */
    private String normalizeQuery(String query) {
        return isValid(query) ? query.trim() : null;
    }

    /**
     * Check if a string is valid (not null and not empty)
     * @param input String to validate
     * @return true if valid, false otherwise
     */
    private boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }

    /**
     * Record class representing search context
     */
    private record SearchContext(Optional<String> searchQuery, ProductSortType sortType) {}



}