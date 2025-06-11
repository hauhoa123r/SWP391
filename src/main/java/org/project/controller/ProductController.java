package org.project.controller;

import java.util.List;

import org.project.entity.ProductEntity;
import org.project.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/products")
public class ProductController {
	private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductEntity> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductEntity getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PostMapping("/sell")
    public String sellProduct(@RequestParam Long id, @RequestParam int quantity) {
        productService.sellProduct(id, quantity);
        return "Product sold successfully.";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam Long id, @RequestParam int quantity) {
    	productService.placeOrder(id, quantity);
        return "Order placed successfully.";
    }

    @GetMapping("/stock/{id}")
    public int checkStock(@PathVariable Long id) {
        return productService.findById(id).getStockQuantities();
    }
}
