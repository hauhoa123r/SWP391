package org.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id") // ⚠️ Bổ sung cho đồng nhất với DB
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY) // ✅ Nên thêm LAZY cho tối ưu hiệu suất
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id") // đảm bảo khớp DB
    private Patient patient;

    // Constructors
    public Appointment() {}

    // Getters & Setters
    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}

