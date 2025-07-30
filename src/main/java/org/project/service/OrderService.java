package org.project.service;

import org.project.entity.CartItemEntity;
import org.project.entity.OrderEntity;
import org.project.entity.OrderItemEntity;
import org.project.model.dto.CheckoutFormDTO;
import org.project.model.dto.OrderSummaryDTO;

public interface OrderService {
    OrderEntity createOrder(CheckoutFormDTO checkoutFormDTO, Long userId);
    OrderSummaryDTO mapToSummaryDTO(OrderEntity order, Long userId);
}
