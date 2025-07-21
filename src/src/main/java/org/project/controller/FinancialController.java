package org.project.controller;

import org.project.model.response.*;
import org.project.service.FinancialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "http://localhost:3000")
public class FinancialController {

    @Autowired
    private FinancialService financialService;

    @GetMapping("/summary")
    public FinancialSummaryDTO getFinancialSummary(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "top", defaultValue = "5") int topN,
            @RequestParam(value = "threshold", defaultValue = "10") int stockAlertThreshold
    ) {
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();
        return financialService.getFinancialSummary(fromDate, toDate, topN, stockAlertThreshold);
    }

    @GetMapping("/payments")
    public List<PaymentResponseDTO> getAllPayments(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();
        return financialService.getAllPayments(fromDate, toDate);
    }

    @GetMapping("/expenses")
    public List<ExpenseResponseDTO> getAllExpenses(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();
        return financialService.getAllExpenses(fromDate, toDate);
    }

    @GetMapping("/top-products")
    public List<TopProductDTO> getTopProducts(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "top", defaultValue = "5") int topN
    ) {
        if (fromDate == null) fromDate = LocalDate.now().withDayOfMonth(1);
        if (toDate == null) toDate = LocalDate.now();
        return financialService.getTopProducts(fromDate, toDate, topN);
    }

    @GetMapping("/low-stock")
    public List<LowStockProductDTO> getLowStockProducts(
            @RequestParam(value = "threshold", defaultValue = "10") int stockAlertThreshold
    ) {
        return financialService.getLowStockProducts(stockAlertThreshold);
    }
}

