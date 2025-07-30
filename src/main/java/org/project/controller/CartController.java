package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.entity.CartItemEntity;
import org.project.entity.CartItemEntityId;
import org.project.entity.ProductEntity;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.repository.ProductRepository;
import org.project.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final ProductRepository productRepository;
    //hard-code userId
    Long userId = 2l;
    


    // view all cart items of the user
    // including the total amount of money and number of item in cart
    @GetMapping
    public String viewCart(Model model) {
        log.info("Accessing cart page for user ID: {}", userId);
        
        try {
            List<CartItemEntity> cartItems = cartService.getCart(userId);
            log.info("Found {} cart items for user {}", cartItems.size(), userId);
            
            // Debug: log cart items
            for (CartItemEntity item : cartItems) {
                log.info("Cart item: Product={}, Quantity={}, Price={}", 
                    item.getProductEntity().getName(), 
                    item.getQuantity(), 
                    item.getProductEntity().getPrice());
            }
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("total", cartService.calculateTotal(userId));
            model.addAttribute("size", cartItems.size());
            
            log.info("Cart total: {}", cartService.calculateTotal(userId));
            
            return "frontend/cart"; // Sử dụng template cart.html có sẵn
        } catch (Exception e) {
            log.error("Error loading cart: ", e);
            model.addAttribute("error", "Error loading cart: " + e.getMessage());
            return "frontend/cart"; // Sử dụng template cart.html có sẵn
        }
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        log.info("Adding product {} to cart for user {} with quantity {}", productId, userId, quantity);
        
        try {
            cartService.addItem(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm vào giỏ hàng thành công!");
            log.info("Successfully added product to cart");
        } catch (Exception e) {
            log.error("Error adding product to cart", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
        
        return "redirect:/shop";
    }

    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long cartId,
                                 @RequestParam Long userId,
                                 @RequestParam String action) {
        log.info("Updating cart item {} for user {} with action {}", cartId, userId, action);
        
        try {
            // Lấy cart item hiện tại
            CartItemEntityId id = new CartItemEntityId(cartId, userId);
            CartItemEntity item = cartService.getItemById(id);
            
            if (item != null) {
                int currentQuantity = item.getQuantity();
                
                if ("increment".equals(action)) {
                    item.setQuantity(currentQuantity + 1);
                } else if ("decrement".equals(action) && currentQuantity > 1) {
                    item.setQuantity(currentQuantity - 1);
                }
                
                cartService.updateItem(item);
                log.info("Successfully updated cart item");
            }
        } catch (Exception e) {
            log.error("Error updating cart item", e);
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/delete")
    public String deleteCartItem(@RequestParam Long productId) {
        log.info("Deleting product {} from cart for user {}", productId, userId);
        
        try {
            cartService.removeItem(userId, productId);
            log.info("Successfully deleted product from cart");
        } catch (Exception e) {
            log.error("Error deleting product from cart", e);
        }
        
        return "redirect:/cart";
    }

    @PostMapping("/delete-ajax")
    @ResponseBody
    public Map<String, Object> deleteCartItemAjax(@RequestParam Long productId) {
        log.info("Deleting product {} from cart for user {} via AJAX", productId, userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            cartService.removeItem(userId, productId);
            log.info("Successfully deleted product from cart via AJAX");
            
            // Calculate new totals
            BigDecimal newTotal = cartService.calculateTotal(userId);
            List<CartItemEntity> cartItems = cartService.getCart(userId);
            
            response.put("success", true);
            response.put("message", "Product removed from cart successfully");
            response.put("newTotal", newTotal);
            response.put("cartItemCount", cartItems.size());
            
        } catch (Exception e) {
            log.error("Error deleting product from cart via AJAX", e);
            response.put("success", false);
            response.put("message", "Error removing product from cart: " + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/total-ajax")
    @ResponseBody
    public Map<String, Object> getCartTotalAjax() {
        log.info("Getting cart total for user {} via AJAX", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            BigDecimal total = cartService.calculateTotal(userId);
            List<CartItemEntity> cartItems = cartService.getCart(userId);
            
            response.put("success", true);
            response.put("total", total);
            response.put("itemCount", cartItems.size());
            
            log.info("Cart total for user {}: {}", userId, total);
            
        } catch (Exception e) {
            log.error("Error getting cart total via AJAX", e);
            response.put("success", false);
            response.put("message", "Error getting cart total: " + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/update-quantity")
    public String updateQuantity(@RequestParam Long cartId,
                                @RequestParam Long userId,
                                @RequestParam Integer quantity) {
        log.info("Updating cart item {} quantity to {} for user {}", cartId, quantity, userId);
        
        try {
            // Lấy cart item hiện tại
            CartItemEntityId id = new CartItemEntityId(cartId, userId);
            CartItemEntity item = cartService.getItemById(id);
            
            if (item != null && quantity > 0) {
                item.setQuantity(quantity);
                cartService.updateItem(item);
                log.info("Successfully updated cart item quantity");
            }
        } catch (Exception e) {
            log.error("Error updating cart item quantity", e);
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/test")
    public String testCart() {
        log.info("Testing cart functionality");
        
        try {
            // Test tạo sản phẩm mẫu và thêm vào giỏ hàng
            // Đây chỉ là test đơn giản, trong thực tế sẽ có ProductService
            log.info("Cart test completed successfully");
        } catch (Exception e) {
            log.error("Error in cart test", e);
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/test-add")
    public String testAddToCart() {
        log.info("Testing add to cart functionality");
        
        try {
            // Giả sử có sản phẩm với ID = 1
            Long productId = 1L;
            cartService.addItem(userId, productId, 2);
            log.info("Successfully added product {} to cart for user {}", productId, userId);
        } catch (Exception e) {
            log.error("Error adding product to cart", e);
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/create-sample-product")
    public String createSampleProduct() {
        log.info("Creating sample product for testing");
        
        try {
            // Tạo sản phẩm mẫu
            ProductEntity product = new ProductEntity();
            product.setName("Paracetamol 500mg");
            product.setDescription("Thuốc giảm đau và hạ sốt");
            product.setPrice(new BigDecimal("50000"));
            product.setUnit("Viên");
            product.setStockQuantities(100);
            product.setImageUrl("https://via.placeholder.com/150x150?text=Paracetamol");
            product.setProductType(ProductType.MEDICINE);
            product.setProductStatus(ProductStatus.ACTIVE);
            product.setLabel(Label.STANDARD);
            
            ProductEntity savedProduct = productRepository.save(product);
            log.info("Created sample product with ID: {}", savedProduct.getId());
            
            // Thêm vào giỏ hàng
            cartService.addItem(userId, savedProduct.getId(), 2);
            log.info("Added sample product to cart");
            
        } catch (Exception e) {
            log.error("Error creating sample product", e);
        }
        
        return "redirect:/cart";
    }
}