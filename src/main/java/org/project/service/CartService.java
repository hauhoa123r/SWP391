//package org.project.service;
//
//import org.project.model.response.CartItemResponse;
//import org.project.model.response.PharmacyResponse;
//
//import java.util.List;
//
//public interface CartService {
//    /**
//     * Add a product to the cart
//     * @param userId User ID
//     * @param productId Product ID
//     * @param quantity Quantity to add
//     */
//    void addToCart(Long userId, Long productId, Integer quantity);
//
//    /**
//     * Remove a product from the cart
//     * @param userId User ID
//     * @param productId Product ID
//     */
//    void removeFromCart(Long userId, Long productId);
//
//    /**
//     * Update the quantity of a product in the cart
//     * @param userId User ID
//     * @param productId Product ID
//     * @param quantity New quantity
//     */
//    void updateQuantity(Long userId, Long productId, Integer quantity);
//
//    /**
//     * Get all items in the user's cart
//     * @param userId User ID
//     * @return List of cart items
//     */
//    List<CartItemResponse> getCartItems(Long userId);
//
//    /**
//     * Get the number of items in the user's cart
//     * @param userId User ID
//     * @return Number of items
//     */
//    int getCartItemCount(Long userId);
//
//    /**
//     * Get the total price of all items in the user's cart
//     * @param userId User ID
//     * @return Total price
//     */
//    double getCartTotal(Long userId);
//
//    /**
//     * Clear all items from the user's cart
//     * @param userId User ID
//     */
//    void clearCart(Long userId);
//}