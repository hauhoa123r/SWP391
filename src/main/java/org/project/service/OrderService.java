
package org.project.service;


import org.project.model.dto.CheckoutFormDTO;

public interface OrderService {
    Long placeOrder(CheckoutFormDTO checkoutFormDTO, String username);
}
