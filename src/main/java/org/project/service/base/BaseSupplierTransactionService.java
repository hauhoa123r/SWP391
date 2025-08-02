package org.project.service.base;

import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseSupplierTransactionService<T> {

    List<T> getAllTransactions();
    
    Page<T> getAllTransactions(int page, int size, String keyword, String status);
    
    Page<T> getFilteredTransactions(int page, int size, String status, String search, String type);
    
    Page<T> getFilteredTransactionsForView(int page, int size, String status, String search, 
                                         String type, List<SupplierTransactionStatus> allowedStatuses);
    
    Page<T> getFilteredTransactions(int page, int size, String keyword, String status, 
                                  List<SupplierTransactionStatus> allowedStatuses,
                                  SupplierTransactionType transactionType);
    
    T getTransactionById(Long id);
    
    T createTransaction(T dto);
    
    T updateTransaction(Long id, T dto);
    
    T updateTransactionStatus(Long id, String status);
    
    void deleteTransaction(Long id);
} 