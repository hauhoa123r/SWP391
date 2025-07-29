
package org.project.service;

import org.project.entity.OrderEntity;
import org.project.model.dto.CheckoutFormDTO;

public interface OrderService {
    OrderEntity createOrder(CheckoutFormDTO checkoutFormDTO, Long userId);
}
