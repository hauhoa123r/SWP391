package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.StockRequestEntity;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;
import org.project.repository.StockRequestRepository;
import org.project.service.StockRequestService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockRequestServiceImpl implements StockRequestService {

    private final StockRequestRepository stockRequestRepository;

    @Override
    @Transactional
    public StockRequestEntity save(StockRequestEntity stockRequest) {
        return stockRequestRepository.save(stockRequest);
    }

    @Override
    public StockRequestEntity findById(Long id) {
        return stockRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Request not found with ID: " + id));
    }

    @Override
    public List<StockRequestEntity> findAll() {
        return stockRequestRepository.findAll();
    }

    @Override
    public List<StockRequestEntity> findAllByTransactionType(StockTransactionType transactionType) {
        Pageable allRecords = PageRequest.of(0, Integer.MAX_VALUE);
        return stockRequestRepository.findByTransactionType(transactionType, allRecords).getContent();
    }

    @Override
    public List<StockRequestEntity> findAllByStatus(StockStatus status) {
        Pageable allRecords = PageRequest.of(0, Integer.MAX_VALUE);
        return stockRequestRepository.findByStatus(status, allRecords).getContent();
    }

    @Override
    public List<StockRequestEntity> findAllByTransactionTypeAndStatus(StockTransactionType transactionType, StockStatus status) {
        Pageable allRecords = PageRequest.of(0, Integer.MAX_VALUE);
        return stockRequestRepository.findByTransactionTypeAndStatus(transactionType, status, allRecords).getContent();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        stockRequestRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, StockStatus status) {
        StockRequestEntity stockRequest = findById(id);
        stockRequest.setStatus(status);
        stockRequestRepository.save(stockRequest);
    }
} 