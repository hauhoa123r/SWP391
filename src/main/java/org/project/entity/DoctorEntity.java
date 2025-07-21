package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.project.enums.DoctorRank;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "doctors", schema = "swp391")
@FieldNameConstants
public class DoctorEntity {
    @Id
    @Column(name = "doctor_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private StaffEntity staffEntity;

    @OneToMany
    private final Set<AppointmentEntity> appointmentEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "doctor_rank")
    private DoctorRank doctorRank;

    @OneToMany(mappedBy = "doctorEntity", fetch = FetchType.LAZY)
    private final Set<TestRequestEntity> testRequestEntities = new LinkedHashSet<>();
}