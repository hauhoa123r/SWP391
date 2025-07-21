package org.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id") // ✅ THÊM DÒNG NÀY để Hibernate biết cột trong DB
    private Long patientId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Constructors
    public Patient() {}

    public Patient(String fullName) {
        this.fullName = fullName;
    }

    // Getters & Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
