package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staffs", schema = "swp391")
public class StaffsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Lob
    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "hiring_date")
    private LocalDate hiringDate;

    @Lob
    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private StaffsEntity manager;

    @Size(max = 255)
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Size(max = 255)
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    @Size(max = 255)
    @Column(name = "emergency_contact_relation")
    private String emergencyContactRelation;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Timestamp createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}