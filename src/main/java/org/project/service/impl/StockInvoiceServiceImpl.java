package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.StockInvoiceEntity;
import org.project.enums.StockTransactionType;
import org.project.repository.StockInvoiceRepository;
import org.project.service.StockInvoiceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockInvoiceServiceImpl implements StockInvoiceService {

    private final StockInvoiceRepository stockInvoiceRepository;

    @Override
    @Transactional
    public StockInvoiceEntity save(StockInvoiceEntity stockInvoice) {
        return stockInvoiceRepository.save(stockInvoice);
    }

    @Override
    public StockInvoiceEntity findById(Long id) {
        return stockInvoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Invoice not found with ID: " + id));
    }

    @Override
    public List<StockInvoiceEntity> findAll() {
        return stockInvoiceRepository.findAll();
    }

    @Override
    public List<StockInvoiceEntity> findAllByTransactionType(StockTransactionType transactionType) {
        // Sử dụng PageRequest.of(0, Integer.MAX_VALUE) để lấy tất cả kết quả
        Pageable allRecords = PageRequest.of(0, Integer.MAX_VALUE);
        return stockInvoiceRepository.findByTransactionType(transactionType, allRecords).getContent();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        stockInvoiceRepository.deleteById(id);
    }
} 