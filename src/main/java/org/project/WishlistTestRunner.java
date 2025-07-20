package org.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.model.response.PharmacyResponse;
import org.project.service.WishlistService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Quick manual test for WishlistService.
 * <p>
 * Runs automatically when the application starts and logs the behaviour of
 * add/remove operations. Adjust userId and productIds to values that exist in
 * your database before running.
 * </p>
 */
@Slf4j
@Component
@Order(100) // run after data init
@RequiredArgsConstructor
public class WishlistTestRunner implements CommandLineRunner {

    private final WishlistService wishlistService;

    private static final Long USER_ID = 1L;      // change if needed
    private static final Long PRODUCT_ID_1 = 10L; // change to existing product id
    private static final Long PRODUCT_ID_2 = 11L; // change to existing product id

    @Override
    @Transactional
    public void run(String... args) {
        log.info("--- Wishlist test START ---");

        // add products
        wishlistService.addProduct(USER_ID, PRODUCT_ID_1);
        wishlistService.addProduct(USER_ID, PRODUCT_ID_2);

        // display list after adding
        logList("After add");

        // remove one product
        wishlistService.removeProduct(USER_ID, PRODUCT_ID_1);

        // display list after removing
        logList("After remove");

        log.info("--- Wishlist test END ---");
    }

    private void logList(String stage) {
        List<PharmacyResponse> items = wishlistService.getWishlistItems(USER_ID);
        log.info("{} ({} items):", stage, items.size());
        items.forEach(p -> log.info("- {} | {}", p.getId(), p.getName()));
    }
}
