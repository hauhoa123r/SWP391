package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.LeaveStatus;
import org.project.enums.LeaveType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestEntity {

    @Column(name = "leave_request_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staffEntity;

    @Column(name = "leave_type")
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType = LeaveType.ANNUAL_LEAVE;

    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;

    @Column(name = "duration_hours")
    private BigDecimal durationHours;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LeaveStatus status = LeaveStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private StaffEntity approvedBy;

    @Column(name = "approved_at")
    private Timestamp approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_staff_id")
    private StaffEntity staffSubstitute;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
