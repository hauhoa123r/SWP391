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

import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final ProductRepository productRepository;
    //hard-code userId
    Long userId = 4L;
    


    // view all cart items of the user
    // including the total amount of money and number of item in cart
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        Long userId = 4L;
        List<CartItemEntity> cartItems = cartService.getCart(userId);
        BigDecimal totalBigDecimal = cartItems.stream()
            .map(item -> item.getProductEntity().getPrice().multiply(new BigDecimal(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        double total = totalBigDecimal.doubleValue();
        
        // Get coupon information from session
        String couponCode = (String) session.getAttribute("couponCode");
        Double couponValue = (Double) session.getAttribute("couponValue");
        String couponType = (String) session.getAttribute("couponType");
        
        double discountedTotal = total;
        double discountAmount = 0.0;
        
        if (couponCode != null && couponValue != null && couponType != null) {
            logger.info("Applying coupon from session: {}, Value: {}, Type: {}", couponCode, couponValue, couponType);
            if (couponType.equals("PERCENTAGE")) {
                discountAmount = total * (couponValue / 100);
                discountedTotal = total - discountAmount;
            } else if (couponType.equals("FIXED")) {
                discountAmount = couponValue;
                discountedTotal = Math.max(0, total - discountAmount);
            }
            logger.info("Discount amount: {}, Discounted total: {}", discountAmount, discountedTotal);
        }
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("couponCode", couponCode);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("discountedTotal", discountedTotal);
        return "frontend/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Long userId = 4L;
        logger.info("Adding product {} with quantity {} to cart for user {}", productId, quantity, userId);
        try {
            cartService.addItem(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được thêm vào giỏ hàng!");
            logger.info("Product {} added to cart successfully for user {}", productId, userId);
        } catch (Exception e) {
            logger.error("Error adding product {} to cart for user {}: {}", productId, userId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm sản phẩm vào giỏ hàng: " + e.getMessage());
        }
        return "redirect:/shop";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("cartId") Long productId,
                             @RequestParam("quantity") int quantity,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Long userId = 4L;
        logger.info("Updating product {} quantity to {} in cart for user {}", productId, quantity, userId);
        try {
            CartItemEntityId id = new CartItemEntityId(productId, userId);
            CartItemEntity item = cartService.getItemById(id);
            if (item != null && item.getQuantity() < item.getProductEntity().getStockQuantities()) {
                item.setQuantity(quantity);
                cartService.updateItem(item);
                logger.info("Product {} quantity updated to {} in cart for user {}", productId, quantity, userId);
                redirectAttributes.addFlashAttribute("success", "Giỏ hàng đã được cập nhật!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Số lượng phải nhỏ hơn số lượng tồn kho.");
            }
        } catch (Exception e) {
            logger.error("Error updating product {} quantity in cart for user {}: {}", productId, userId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật giỏ hàng: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/delete")
    public String removeFromCart(@RequestParam("productId") Long productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = 4L;
        logger.info("Removing product {} from cart for user {}", productId, userId);
        try {
            cartService.removeItem(userId, productId);
            redirectAttributes.addFlashAttribute("success", "Sản phẩm đã được xóa khỏi giỏ hàng!");
            logger.info("Product {} removed from cart for user {}", productId, userId);
        } catch (Exception e) {
            logger.error("Error removing product {} from cart for user {}: {}", productId, userId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
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

//    @PostMapping("/update-quantity")
//    public String updateQuantity(@RequestParam("cartId") Long productId,
//                                 @RequestParam("quantity") int quantity,
//                                 HttpSession session,
//                                 RedirectAttributes redirectAttributes) {
//        Long userId = 4L;
//        logger.info("Updating product {} quantity to {} in cart for user {}", productId, quantity, userId);
//        try {
//            CartItemEntityId id = new CartItemEntityId(productId, userId);
//            CartItemEntity item = cartService.getItemById(id);
//            if (item != null && quantity > 0) {
//                item.setQuantity(quantity);
//                cartService.updateItem(item);
//                logger.info("Product {} quantity updated to {} in cart for user {}", productId, quantity, userId);
//            }
//            redirectAttributes.addFlashAttribute("success", "Số lượng sản phẩm đã được cập nhật!");
//        } catch (Exception e) {
//            logger.error("Error updating product {} quantity in cart for user {}: {}", productId, userId, e.getMessage());
//            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật số lượng sản phẩm: " + e.getMessage());
//        }
//        return "redirect:/cart";
//    }

    @PostMapping("/update-quantity")
    public String updateQuantity(@RequestParam("cartId") Long productId,
                                 @RequestParam("quantity") int currentQuantity,
                                 @RequestParam("action") String action,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Long userId = 4L;
        logger.info("Updating product {} with action {} for user {}", productId, action, userId);
        try {
            CartItemEntityId id = new CartItemEntityId(productId, userId);
            CartItemEntity item = cartService.getItemById(id);
            if (item != null) {
                int newQuantity = currentQuantity;
                if ("increment".equals(action)) {
                    newQuantity = item.getQuantity() + 1;
                } else if ("decrement".equals(action)) {
                    newQuantity = item.getQuantity() - 1;
                }
                if (newQuantity > 0 && item.getProductEntity().getStockQuantities() > newQuantity) {
                    item.setQuantity(newQuantity);
                    cartService.updateItem(item);
                    logger.info("Product {} quantity updated to {} in cart for user {}", productId, newQuantity, userId);
                    redirectAttributes.addFlashAttribute("success", "Số lượng sản phẩm đã được cập nhật!");
                } else if (newQuantity >= item.getProductEntity().getStockQuantities()) {
                    redirectAttributes.addFlashAttribute("error", "Số lượng phải nhỏ hơn số lượng tồn kho!!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Số lượng phải lớn hơn 0!!");
                }
            }
        } catch (Exception e) {
            logger.error("Error updating product {} quantity in cart for user {}: {}", productId, userId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật số lượng sản phẩm: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/test")
    public String testCart() {
        logger.info("Testing cart functionality");
        
        try {
            // Test tạo sản phẩm mẫu và thêm vào giỏ hàng
            // Đây chỉ là test đơn giản, trong thực tế sẽ có ProductService
            logger.info("Cart test completed successfully");
        } catch (Exception e) {
            logger.error("Error in cart test", e);
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/test-add")
    public String testAddToCart() {
        logger.info("Testing add to cart functionality");
        
        try {
            // Giả sử có sản phẩm với ID = 1
            Long productId = 1L;
            cartService.addItem(userId, productId, 2);
            logger.info("Successfully added product {} to cart for user {}", productId, userId);
        } catch (Exception e) {
            logger.error("Error adding product to cart", e);
        }
        
        return "redirect:/cart";
    }

    @GetMapping("/create-sample-product")
    public String createSampleProduct() {
        logger.info("Creating sample product for testing");
        
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
            logger.info("Created sample product with ID: {}", savedProduct.getId());
            
            // Thêm vào giỏ hàng
            cartService.addItem(userId, savedProduct.getId(), 2);
            logger.info("Added sample product to cart");
            
        } catch (Exception e) {
            logger.error("Error creating sample product", e);
        }
        
        return "redirect:/cart";
    }
}