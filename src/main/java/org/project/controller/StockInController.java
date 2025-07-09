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
@RequestMapping("/stock-in")
@RequiredArgsConstructor
public class StockInController {

    private final StockRequestService stockRequestService;
    private final StockInvoiceService stockInvoiceService;

    @GetMapping
    public String getStockInPage(Model model) {
        List<StockRequestEntity> waitingRequests = stockRequestService.findAllByTransactionTypeAndStatus(
                StockTransactionType.STOCK_IN, StockStatus.WAITING_FOR_DELIVERY);
        List<StockRequestEntity> inspectedRequests = stockRequestService.findAllByTransactionTypeAndStatus(
                StockTransactionType.STOCK_IN, StockStatus.INSPECTED);
        
        model.addAttribute("waitingRequests", waitingRequests);
        model.addAttribute("inspectedRequests", inspectedRequests);
        return "stock-in";
    }

    @GetMapping("/{id}")
    public String getStockInDetails(@PathVariable Long id, Model model) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        model.addAttribute("stockRequest", stockRequest);
        return "stock-in-details";
    }

    @PostMapping("/inspect/{id}")
    public String inspectStockIn(@PathVariable Long id, @RequestParam String notes) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.INSPECTED);
        stockRequest.setNotes(notes);
        stockRequestService.save(stockRequest);
        return "redirect:/stock-in";
    }

    @PostMapping("/problem/{id}")
    public String reportProblem(@PathVariable Long id, @RequestParam String notes) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.PROBLEM);
        stockRequest.setNotes(notes);
        stockRequestService.save(stockRequest);
        return "redirect:/stock-in";
    }

    @PostMapping("/complete/{id}")
    public String completeStockIn(@PathVariable Long id) {
        StockRequestEntity stockRequest = stockRequestService.findById(id);
        stockRequest.setStatus(StockStatus.STOCKED);
        stockRequestService.save(stockRequest);
        
        // Create invoice
        StockInvoiceEntity invoice = new StockInvoiceEntity();
        invoice.setStockRequest(stockRequest);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setTransactionType(StockTransactionType.STOCK_IN);
        invoice.setInvoiceDate(Timestamp.valueOf(LocalDateTime.now()));
        invoice.setStatus(StockStatus.COMPLETED);
        invoice.setCreatedBy(stockRequest.getRequestedBy());
        invoice.setTotalAmount(stockRequest.getStockRequestItems().stream()
                .map(item -> item.getUnitPrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        
        stockInvoiceService.save(invoice);
        
        return "redirect:/stock-in-invoice";
    }
} 