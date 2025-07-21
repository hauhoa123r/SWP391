package org.project.service;

import org.project.model.response.PaymentResponseDTO;
import java.util.List;

public interface PaymentService {
    List<PaymentResponseDTO> getAllPayments();
}
