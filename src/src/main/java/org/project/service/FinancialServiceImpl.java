package org.project.service;

import org.project.model.response.*;
import org.project.repository.OrderItemRepository;
import org.project.repository.PaymentRepository;
import org.project.repository.ProductRepository;
import org.project.repository.SupplierTransactionItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialServiceImpl implements FinancialService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SupplierTransactionItemRepository supplierTransactionItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public FinancialSummaryDTO getFinancialSummary(LocalDate fromDate, LocalDate toDate, int topN, int stockAlertThreshold) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(23, 59, 59);

        BigDecimal totalIncome = paymentRepository.sumPaymentsBetween(from, to);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpense = supplierTransactionItemRepository.sumExpensesBetween(fromDate, toDate);
        if (totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal netProfit = totalIncome.subtract(totalExpense);

        List<TopProductDTO> topProducts = orderItemRepository.findTopProductsByRevenue(PageRequest.of(0, topN));
        List<LowStockProductDTO> lowStockProducts = productRepository.findProductsBelowStock(stockAlertThreshold);

        FinancialSummaryDTO dto = new FinancialSummaryDTO();
        dto.setTotalIncome(totalIncome);
        dto.setTotalExpense(totalExpense);
        dto.setNetProfit(netProfit);
        dto.setTopProducts(topProducts);
        dto.setLowStockProducts(lowStockProducts);

        return dto;
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(23, 59, 59);

        return paymentRepository.findByPaymentTimeBetween(from, to)
                .stream()
                .map(p -> {
                    PaymentResponseDTO dto = new PaymentResponseDTO();
                    dto.setPaymentId(p.getPaymentId());
                    dto.setOrderId(p.getOrder() != null ? p.getOrder().getOrderId() : null);
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMethod(p.getPaymentMethod());
                    dto.setPaymentStatus(p.getPaymentStatus());
                    dto.setPaymentTime(p.getPaymentTime());

                    // Lấy tên bệnh nhân nếu có
                    String customerName = "Không rõ";
                    try {
                        if (p.getOrder() != null &&
                            p.getOrder().getAppointment() != null &&
                            p.getOrder().getAppointment().getPatient() != null) {
                            customerName = p.getOrder().getAppointment().getPatient().getFullName();
                        }
                    } catch (Exception ignored) {}

                    dto.setCustomerName(customerName);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ExpenseResponseDTO> getAllExpenses(LocalDate fromDate, LocalDate toDate) {
        return supplierTransactionItemRepository
                .findByTransaction_TransactionDateBetween(fromDate, toDate)
                .stream()
                .map(st -> {
                    ExpenseResponseDTO dto = new ExpenseResponseDTO();
                    dto.setTransactionId(st.getSupplierTransactionId());
                    dto.setTransactionDate(st.getTransaction().getTransactionDate());
                    dto.setSupplierName("Supplier #" + st.getTransaction().getSupplierId());
                    dto.setTotalAmount(st.getUnitPrice().multiply(BigDecimal.valueOf(st.getQuantity())));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TopProductDTO> getTopProducts(LocalDate fromDate, LocalDate toDate, int topN) {
        return orderItemRepository.findTopProductsByRevenue(PageRequest.of(0, topN));
    }

    @Override
    public List<LowStockProductDTO> getLowStockProducts(int stockAlertThreshold) {
        return productRepository.findProductsBelowStock(stockAlertThreshold);
    }
}

