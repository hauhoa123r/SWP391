package org.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;  // ✅ đổi từ 'id' sang 'paymentId' để đồng bộ với DTO

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod; // ✅ đổi tên cho khớp với DTO & Service

    @Column(name = "payment_status")
    private String paymentStatus; // ✅

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    // Constructors
    public Payment() {}

    public Payment(BigDecimal amount, String paymentMethod, String paymentStatus, LocalDateTime paymentTime) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}
