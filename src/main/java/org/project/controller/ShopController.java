package org.project.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.enums.ProductSortType;
import org.project.model.response.CategoryListResponse;
import org.project.model.response.PharmacyListResponse;
import org.project.service.CategoryService;
import org.project.service.PharmacyService;
import org.project.service.ProductService;
import org.project.repository.ProductTagRepository;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ShopController {
    private final ProductService productService;
    private final PharmacyService pharmacyServiceImpl;
    private final CategoryService categoryService;
    private final ProductTagRepository productTagRepository;

    private static final String PREVIOUS_SEARCH_KEY = "previousSearch";

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
            HttpSession session) {
        log.info("Processing shop request: searchQuery={}, sortType={}, page={}, size={}", searchQuery, sortType, page, size);

        ModelAndView mv = new ModelAndView("shop");
        SearchContext context = buildSearchContext(searchQuery, sortType, session);

        Page<PharmacyListResponse> productPage = productService.searchProducts(
                context.searchQuery(), Optional.ofNullable(categoryId),
                Optional.ofNullable(minPrice), Optional.ofNullable(maxPrice),
                Optional.ofNullable(tagRaw), context.sortType(), PageRequest.of(page, size));

        List<CategoryListResponse> categories = loadCategoriesWithProductCount();

        mv.addObject("products", productPage.getContent());
        mv.addObject("productPage", productPage);
        mv.addObject("selectedSort", context.sortType());
        mv.addObject("search", context.searchQuery().orElse(null));
        mv.addObject("categories", categories);
        mv.addObject("categoryId", categoryId);
        mv.addObject("size", size);
        mv.addObject("page", page);
        // Sidebar data
        mv.addObject("topProducts", pharmacyServiceImpl.findTop10Products());
        mv.addObject("tags", productTagRepository.findDistinctTagNames());
        mv.addObject("minPrice", minPrice);
        mv.addObject("maxPrice", maxPrice);
        mv.addObject("selectedTag", tagRaw);
        return mv;
    }

    @PostMapping("/submit")
    public String search(@RequestParam(value = "search", required = false) String searchQuery,
                         @RequestParam(value = "sort", required = false) String sortType) {
        StringBuilder redirectUrl = new StringBuilder("redirect:/shop");
        boolean hasQuery = false;

        if (isValid(searchQuery)) {
            redirectUrl.append("?search=").append(searchQuery.trim());
            hasQuery = true;
        }

        if (isValid(sortType)) {
            redirectUrl.append(hasQuery ? "&" : "?").append("sort=").append(sortType);
        }

        return redirectUrl.toString();
    }


    @GetMapping("/product-home")
    public ModelAndView productHome() {
        log.info("Loading product home page");

        ModelAndView mv = new ModelAndView("product-home");

        try {
            // Fetch top 10 products for the home page
            List<PharmacyListResponse> products = pharmacyServiceImpl.findTop10Products();

            // Kiểm tra nếu danh sách trả về là null
            if (products == null) {
                log.warn("findTop10Products returned null, using empty list");
                products = Collections.emptyList();
            } else if (products.isEmpty()) {
                log.debug("No products found, returning empty list");
            } else {
                log.debug("Loaded {} products for home page", products.size());
            }

            // Thêm danh sách sản phẩm vào model
            mv.addObject("products", products);

        } catch (Exception e) {
            log.error("Error fetching top 10 products: {}", e.getMessage(), e);
            // Trả về danh sách rỗng nếu có lỗi để tránh lỗi render
            mv.addObject("products", Collections.emptyList());
        }

        return mv;
    }

    @GetMapping("/cart")
    public ModelAndView cart() {
        ModelAndView mv = new ModelAndView("cart");
        return mv;
    }

    @GetMapping("/checkout")
    public ModelAndView checkout() {
        ModelAndView mv = new ModelAndView("checkout");
        return mv;
    }

    @GetMapping("/wishlist")
    public ModelAndView wishlist() {
        ModelAndView mv = new ModelAndView("/wishlist");
        return mv;
    }

    @GetMapping("/my-account")
    public ModelAndView myAccount() {
        ModelAndView mv = new ModelAndView("my-account");
        return mv;
    }

    @GetMapping("/track-order")
    public ModelAndView trackOrder() {
        ModelAndView mv = new ModelAndView("track-order");
        return mv;
    }

    @GetMapping("/product-new")
    public ModelAndView productNew() {
        ModelAndView mv = new ModelAndView("product-new");
        return mv;
    }

    @GetMapping("/product-sale")
    public ModelAndView productSale() {
        ModelAndView mv = new ModelAndView("product-sale");
        return mv;
    }

    @GetMapping("/shop-left-sidebar")
    public ModelAndView shopLeftSidebar() {
        ModelAndView mv = new ModelAndView("shop-left-sidebar");
        return mv;
    }

    @GetMapping("/shop-right-sidebar")
    public ModelAndView shopRightSidebar() {
        ModelAndView mv = new ModelAndView("shop-right-sidebar");
        return mv;
    }

    @GetMapping("/shop-no-sidebar")
    public ModelAndView shopNoSidebar() {
        ModelAndView mv = new ModelAndView("shop-no-sidebar");
        return mv;
    }

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

    private List<CategoryListResponse> loadCategoriesWithProductCount() {
        // Only keep categories that have at least one product to display in sidebar

        List<CategoryListResponse> categories = categoryService.findAllCategory();
        categories.forEach(category -> category.setProductCount(
                String.valueOf(categoryService.countProductsByCategory(category.getId()))));
        // filter
        return categories.stream()
                .filter(c -> !"0".equals(c.getProductCount()))
                .toList();
    }

    private String normalizeQuery(String query) {
        return isValid(query) ? query.trim() : null;
    }

    private boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }


    private record SearchContext(Optional<String> searchQuery, ProductSortType sortType) {}
}