package org.project.service;

import org.project.entity.OrderItemEntity;
import org.project.entity.OrderItemEntityId;
import org.project.model.dto.OrderItemDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemEntity save(OrderItemEntity orderItem);
    OrderItemEntity findById(OrderItemEntityId id);
    List<OrderItemEntity> findAll();
    List<OrderItemEntity> findByOrderId(Long orderId);
    List<OrderItemEntity> findByProductId(Long productId);
    
    /**
     * Tìm OrderItemEntity bằng invoiceNumber dạng "orderId-productId"
     * @param invoiceNumber Chuỗi định dạng "orderId-productId"
     * @return OrderItemEntity
     */
    OrderItemEntity findByInvoiceNumber(String invoiceNumber);
    
    /**
     * Tạo OrderItemEntityId từ chuỗi invoiceNumber
     * @param invoiceNumber Chuỗi định dạng "orderId-productId"
     * @return OrderItemEntityId
     */
    OrderItemEntityId createIdFromInvoiceNumber(String invoiceNumber);
    
    void deleteById(OrderItemEntityId id);
} 