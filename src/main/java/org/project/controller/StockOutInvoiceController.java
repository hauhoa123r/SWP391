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
@RequestMapping("/stock-out-invoice")
@RequiredArgsConstructor
public class StockOutInvoiceController {

    private final StockInvoiceService stockInvoiceService;

    @GetMapping
    public String getStockOutInvoicePage(Model model) {
        List<StockInvoiceEntity> invoices = stockInvoiceService.findAllByTransactionType(StockTransactionType.STOCK_OUT);
        model.addAttribute("invoices", invoices);
        return "stock-out-invoice";
    }

    @GetMapping("/{id}")
    public String getStockOutInvoiceDetails(@PathVariable Long id, Model model) {
        StockInvoiceEntity invoice = stockInvoiceService.findById(id);
        model.addAttribute("invoice", invoice);
        return "stock-out-invoice-details";
    }
} 