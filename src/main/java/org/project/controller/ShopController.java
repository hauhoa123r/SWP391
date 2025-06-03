package org.project.controller;

import org.project.model.response.PharmacyListResponse;
import org.project.service.PharmacyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
public class ShopController {

    @Autowired
    private PharmacyService pharmacyServiceImpl;

    @GetMapping("/shop")
    public ModelAndView shop() {
        ModelAndView mv = new ModelAndView("shop");
        List<PharmacyListResponse> products = pharmacyServiceImpl.getAllPharmacies();
        mv.addObject("products", products);
        return mv;
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

