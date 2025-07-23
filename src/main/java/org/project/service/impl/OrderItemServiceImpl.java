package org.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.project.entity.OrderItemEntity;
import org.project.entity.OrderItemEntityId;
import org.project.repository.OrderItemRepository;
import org.project.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderItemEntity save(OrderItemEntity orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        return orderItemRepository.save(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemEntity findById(OrderItemEntityId id) {
        if (id == null) {
            throw new IllegalArgumentException("OrderItem ID cannot be null");
        }
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findAll() {
        return orderItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        return orderItemRepository.findByOrderEntity_Id(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemEntity> findByProductId(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        return orderItemRepository.findByProductEntity_Id(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemEntity findByInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            throw new IllegalArgumentException("Invoice number cannot be null or empty");
        }
        
        OrderItemEntityId id = createIdFromInvoiceNumber(invoiceNumber);
        return findById(id);
    }
    
    @Override
    public OrderItemEntityId createIdFromInvoiceNumber(String invoiceNumber) {
        try {
            String[] parts = invoiceNumber.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid invoice number format. Expected format: orderId-productId");
            }
            
            Long orderId = Long.parseLong(parts[0]);
            Long productId = Long.parseLong(parts[1]);
            
            OrderItemEntityId id = new OrderItemEntityId();
            id.setOrderId(orderId);
            id.setProductId(productId);
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid invoice number format. Both orderId and productId must be numbers.");
        }
    }

    @Override
    @Transactional
    public void deleteById(OrderItemEntityId id) {
        if (id == null) {
            throw new IllegalArgumentException("OrderItem ID cannot be null");
        }
        if (!orderItemRepository.existsById(id)) {
            throw new EntityNotFoundException("OrderItem not found with ID: " + id);
        }
        orderItemRepository.deleteById(id);
    }
} 