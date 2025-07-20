package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.StockInvoiceEntity;
import org.project.enums.StockTransactionType;
import org.project.service.StockInvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stock-in-invoice")
@RequiredArgsConstructor
public class StockInInvoiceController {

    private final StockInvoiceService stockInvoiceService;

    @GetMapping
    public String getStockInInvoicePage(Model model) {
        List<StockInvoiceEntity> invoices = stockInvoiceService.findAllByTransactionType(StockTransactionType.STOCK_IN);
        model.addAttribute("invoices", invoices);
        return "stock-in-invoice";
    }

    @GetMapping("/{id}")
    public String getStockInInvoiceDetails(@PathVariable Long id, Model model) {
        StockInvoiceEntity invoice = stockInvoiceService.findById(id);
        model.addAttribute("invoice", invoice);
        return "stock-in-invoice-details";
    }
} 