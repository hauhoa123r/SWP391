package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
<<<<<<< HEAD
import org.project.enums.StaffShiftSlot;
=======
import lombok.experimental.FieldNameConstants;
>>>>>>> 1fe7b34939d6bcd04ae5de38ce13891189c4ebc0

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staff_shifts", schema = "swp391")
@FieldNameConstants
public class StaffShiftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_shift_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private StaffEntity staffEntity;

    @NotNull
    @Column(name = "date", nullable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false)
    private StaffShiftSlot shiftType;

    // Note: Hospital information can be accessed via staffEntity.hospitalEntity
    // Removed hospital field to avoid database schema mismatch
}