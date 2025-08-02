package org.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/warehouse/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @GetMapping("/management")
    public String inventoryManagementPage(Model model) {
        log.info("Accessing inventory management page");
        return "templates_storage/inventory-management";
    }
}