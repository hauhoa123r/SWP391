package org.project.service.impl;

import lombok.RequiredArgsConstructor;
import org.project.entity.CartItemEntity;
import org.project.entity.OrderEntity;
import org.project.entity.OrderItemEntity;
import org.project.entity.OrderItemEntityId;
import org.project.enums.OrderStatus;
import org.project.enums.OrderType;
import org.project.enums.PaymentMethod;
import org.project.model.dto.CheckoutFormDTO;
import org.project.repository.*;
import org.project.service.OrderService;
import org.project.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.project.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserService userService;
    private final PaymentService paymentService;

    private final OrderRepository orderRepository;
    private final CartRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final AppointmentRepository appointmentRepository;
    private final CouponRepository couponRepository;

    @Transactional
    @Override
    public Long placeOrder(CheckoutFormDTO dto, String username) {
        // Lấy userId từ username
        Long userId = userService.getUserIdByUsername(username);
        // 1. Validate cart
        List<CartItemEntity> cartItems = cartItemRepository.findByUserEntity_Id((userId));
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        // 2. Tạo order
        OrderEntity order = new OrderEntity();
        order.setShippingFee(dto.getShippingFee());
        order.setTotalAmount(dto.getTotalAmount());
        order.setRealAmount(dto.getRealAmount());
        order.setOrderStatus(OrderStatus.PENDING); // enum
        order.setShippingAddressEntity(shippingAddressRepository.findById(dto.getShippingAddressId()).orElseThrow());
        if (dto.getOrderType().equals(OrderType.APPOINTMENT)) {
            order.setAppointmentEntity(appointmentRepository.findById(dto.getAppointmentId()).orElseThrow());
        }
        if (dto.getCouponId() != null) {
            order.setCouponEntity(couponRepository.findById(dto.getCouponId()).orElse(null));
        }

        order = orderRepository.save(order);

        // 3. Convert CartItem -> OrderItem
        for (CartItemEntity cartItem : cartItems) {
            OrderItemEntity item = new OrderItemEntity();
            item.setId(new OrderItemEntityId(order.getId(), cartItem.getProductEntity().getId()));
            item.setOrderEntity(order);
            item.setProductEntity(cartItem.getProductEntity());
            item.setQuantity(cartItem.getQuantity());
            orderItemRepository.save(item);
        }

        // 4. Tạo payment record dựa trên payment method
        PaymentMethod paymentMethod = dto.getPaymentMethod() != null ? dto.getPaymentMethod() : PaymentMethod.CASH;
        String paymentStatus = PaymentMethod.CASH.equals(paymentMethod) ? "PENDING" : "SUCCESS";
        paymentService.createPayment(order.getId(), dto.getRealAmount(), paymentMethod, paymentStatus);

        // 5. Xóa cart sau khi tạo order
        cartItemRepository.deleteAll(cartItems);

        return order.getId();
    }
}