package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.project.enums.AppointmentStatus;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointments", schema = "swp391")
@FieldNameConstants
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctorEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patientEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceEntity;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "result_url")
    private String resultUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scheduling_coordinator_id", nullable = false)
    private SchedulingCoordinatorEntity schedulingCoordinatorEntity;

    @OneToMany(mappedBy = "appointmentEntity")
    private final Set<IngredientRequestEntity> ingredientRequestEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointmentEntity")
    private final Set<MedicalRecordEntity> medicalRecordEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointmentEntity")
    private final Set<OrderEntity> orderEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointmentEntity")
    private final Set<TestRequestEntity> testRequestEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status")
    private AppointmentStatus appointmentStatus;

    @Column(name = "result_url")
    private String resultUrl;
}