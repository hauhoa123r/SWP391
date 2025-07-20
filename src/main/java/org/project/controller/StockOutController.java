package org.project.controller;

import lombok.RequiredArgsConstructor;
import org.project.entity.StockInvoiceEntity;
import org.project.entity.StockRequestEntity;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.service.StockInvoiceService;
import org.project.service.StockRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/stock-out")
@RequiredArgsConstructor
public class StockOutController {

    private final StockRequestService stockRequestService;
    private final StockInvoiceService stockInvoiceService;

    @GetMapping
    public String getStockOutPage(Model model) {
        List<StockRequestEntity> preparingRequests = stockRequestService.findAllByTransactionTypeAndStatus(
                StockTransactionType.STOCK_OUT, StockStatus.PREPARING);
        List<StockRequestEntity> readyRequests = stockRequestService.findAllByTransactionTypeAndStatus(
                StockTransactionType.STOCK_OUT, StockStatus.READY_FOR_SHIPPING);
        
        model.addAttribute("preparingRequests", preparingRequests);
        model.addAttribute("readyRequests", readyRequests);
        return "stock-out";
    }

    @GetMapping("/{id}")
    public String getStockOutDetails(@PathVariable Long id, Model model) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        model.addAttribute("stockRequest", stockRequest);
        return "stock-out-details";
    }

    @PostMapping("/create")
    public String createStockOut(@ModelAttribute StockRequestEntity stockRequest) {
        stockRequest.setTransactionType(StockTransactionType.STOCK_OUT);
        stockRequest.setRequestDate(Timestamp.valueOf(LocalDateTime.now()));
        stockRequest.setStatus(StockStatus.PREPARING_INVOICE);
        
        stockRequestService.save(stockRequest);
        return "redirect:/stock-out";
    }

    @PostMapping("/prepare/{id}")
    public String prepareStockOut(@PathVariable Long id) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.PREPARING);
        stockRequestService.save(stockRequest);
        return "redirect:/stock-out";
    }

    @PostMapping("/ready/{id}")
    public String readyForShipping(@PathVariable Long id) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.READY_FOR_SHIPPING);
        stockRequestService.save(stockRequest);
        return "redirect:/stock-out";
    }

    @PostMapping("/ship/{id}")
    public String shipStockOut(@PathVariable Long id) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.SHIPPED_OUT);
        stockRequestService.save(stockRequest);
        
        // Create invoice
        StockInvoiceEntity invoice = new StockInvoiceEntity();
        invoice.setStockRequest(stockRequest);
        invoice.setInvoiceNumber("OUT-" + System.currentTimeMillis());
        invoice.setTransactionType(StockTransactionType.STOCK_OUT);
        invoice.setInvoiceDate(Timestamp.valueOf(LocalDateTime.now()));
        invoice.setStatus(StockStatus.COMPLETED);
        invoice.setCreatedBy(stockRequest.getRequestedBy());
        invoice.setTotalAmount(stockRequest.getStockRequestItems().stream()
                .map(item -> item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        
        stockInvoiceService.save(invoice);
        
        return "redirect:/stock-out-invoice";
    }
} 