package org.project.service;

import org.project.model.response.ExpenseResponseDTO;
import org.project.model.response.FinancialSummaryDTO;
import org.project.model.response.LowStockProductDTO;
import org.project.model.response.PaymentResponseDTO;
import org.project.model.response.TopProductDTO;

import java.time.LocalDate;
import java.util.List;

public interface FinancialService {

    FinancialSummaryDTO getFinancialSummary(LocalDate fromDate, LocalDate toDate, int topN, int stockAlertThreshold);

    List<PaymentResponseDTO> getAllPayments(LocalDate fromDate, LocalDate toDate);

    List<ExpenseResponseDTO> getAllExpenses(LocalDate fromDate, LocalDate toDate);

    List<TopProductDTO> getTopProducts(LocalDate fromDate, LocalDate toDate, int topN);

    List<LowStockProductDTO> getLowStockProducts(int stockAlertThreshold);
}
