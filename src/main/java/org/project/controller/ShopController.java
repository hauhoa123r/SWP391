package org.project.controller;

import jakarta.servlet.http.HttpSession;
import org.project.enums.ProductSortType;
import org.project.model.response.PharmacyListResponse;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Comparator;


@Controller
public class ShopController {

    @Autowired
    private PharmacyService pharmacyServiceImpl;

    @GetMapping("/shop")
    public ModelAndView shop(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) ProductSortType sort,
            HttpSession session
    ) {
        ModelAndView mv = new ModelAndView("/shop");

        String trimmedSearch = (search != null) ? search.trim() : null;
        String previousSearch = (String) session.getAttribute("previousSearch");

        boolean isNewSearch = trimmedSearch != null && !trimmedSearch.isEmpty()
                && (previousSearch == null || !trimmedSearch.equals(previousSearch));

        // Nếu là tìm kiếm mới, ép sort về DEFAULT
        if (isNewSearch) {
            sort = ProductSortType.DEFAULT;
            session.setAttribute("previousSearch", trimmedSearch);
        } else if (trimmedSearch == null || trimmedSearch.isEmpty()) {
            // Nếu không có search, xóa session
            trimmedSearch = null;
            session.removeAttribute("previousSearch");
            if (sort == null) {
                sort = ProductSortType.DEFAULT;
            }
        }

        // Lấy dữ liệu
        List<PharmacyListResponse> products = (trimmedSearch != null)
                ? pharmacyServiceImpl.searchByName(trimmedSearch)
                : pharmacyServiceImpl.getAllPharmacies();

        products = pharmacyServiceImpl.sortProducts(products, sort);

        mv.addObject("products", products);
        mv.addObject("selectedSort", sort);
        mv.addObject("search", trimmedSearch);
        return mv;
    }



    @PostMapping("/submit")
    public String search(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        StringBuilder url = new StringBuilder("redirect:/shop");
        boolean hasQuery = false;

        if (search != null && !search.trim().isEmpty()) {
            url.append("?search=").append(search.trim());
            hasQuery = true;
        }

        if (sort != null && !sort.isEmpty()) {
            url.append(hasQuery ? "&" : "?").append("sort=").append(sort);
        }

        return url.toString();
    }



    @GetMapping("/product-standard")
    public ModelAndView product() {
        ModelAndView mv = new ModelAndView("product-standard");
        // Lấy chi tiết sản phẩm theo ID (cần thêm phương thức trong PharmacyService)
//        PharmacyListResponse product = pharmacyServiceImpl.getPharmacyById(id);
//        mv.addObject("product", product);
        return mv;
    }

    @GetMapping("/product-home")
    public ModelAndView productHome() {
        ModelAndView mv = new ModelAndView("product-home");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
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
        ModelAndView mv = new ModelAndView("wishlist");
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
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
    }

    @GetMapping("/product-sale")
    public ModelAndView productSale() {
        ModelAndView mv = new ModelAndView("product-sale");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
    }

    @GetMapping("/shop-left-sidebar")
    public ModelAndView shopLeftSidebar() {
        ModelAndView mv = new ModelAndView("shop-left-sidebar");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
    }

    @GetMapping("/shop-right-sidebar")
    public ModelAndView shopRightSidebar() {
        ModelAndView mv = new ModelAndView("shop-right-sidebar");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
    }

    @GetMapping("/shop-no-sidebar")
    public ModelAndView shopNoSidebar() {
        ModelAndView mv = new ModelAndView("shop-no-sidebar");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
    }
}

