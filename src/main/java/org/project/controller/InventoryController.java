package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for inventory management endpoints
 */
@Slf4j
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    /**
     * Handle inventory management page
     * @param model Spring MVC Model
     * @return View name for inventory management page
     */
    @GetMapping("/management")
    public String inventoryManagementPage(Model model) {
        log.info("Accessing inventory management page");
        return "templates_storage/inventory-management";
    }
} 