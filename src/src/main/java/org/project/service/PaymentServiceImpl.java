package org.project.service;

import org.project.entity.Payment;
import org.project.model.response.PaymentResponseDTO;
import org.project.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        List<Payment> list = paymentRepository.findAll();

        return list.stream().map(p -> {
            PaymentResponseDTO dto = new PaymentResponseDTO();
            dto.setPaymentId(p.getPaymentId());
            dto.setOrderId(p.getOrder() != null ? p.getOrder().getOrderId() : null);
            dto.setAmount(p.getAmount());
            dto.setPaymentMethod(p.getPaymentMethod());
            dto.setPaymentStatus(p.getPaymentStatus());
            dto.setPaymentTime(p.getPaymentTime());
            return dto;
        }).collect(Collectors.toList());
    }
}
