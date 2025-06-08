package org.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.project.enums.AppoinmentType;
import org.project.enums.AppointmentStatus;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "appointments", schema = "swp391")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AppointmentsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="appointment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientsEntity patient;

    @Column(name = "datetime", nullable = false)
    private Timestamp datetime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;



    //trạng thái
//    @Enumerated(EnumType.STRING)
//    @Column(name = "type")
//    private AppoinmentType appointmentType;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus appointmentStatus;




    // Nhân viên
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private StaffsEntity doctor;

    @ManyToOne
    @JoinColumn(name = "coordinator_staff_id")
    private StaffsEntity coordinatorStaff;


//    // Note
//    @Column(name = "symptoms")
//    private String symptoms;
//    @Column(name = "diagnosis")
//    private String diagnosis;
//    @Column(name = "notes")
//    private String notes;
//
//
//    @OneToMany(mappedBy = "appointment")
//    private List<LabTestsEntity> testResults;
//
//    @OneToOne
//    private LabTestResultsEntity testResultsEntity;

}