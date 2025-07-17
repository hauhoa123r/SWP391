package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.LeaveType;

import java.math.BigDecimal;
import java.time.Year;

@Entity
@Table(name = "leave_balances")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceEntity {
    @Id
    @Column(name = "balance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staffEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type")
    private LeaveType leaveType;

    @Column(name = "year", nullable = false)
    private Year year;

    @Column(name = "total_entitlement", precision = 5, scale = 2)
    private BigDecimal totalEntitlement;

    @Column(name = "used_balance", precision = 5, scale = 2)
    private BigDecimal usedBalance;

    @Column(name = "pending_balance", precision = 5, scale = 2)
    private BigDecimal pendingBalance;
}
