package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
import org.project.enums.StaffRole;
import org.project.enums.StaffStatus;
import org.project.enums.StaffType;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "staffs", schema = "swp391")
@FieldNameConstants
public class StaffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id", nullable = false)
    private Long id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private StaffEntity manager;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity departmentEntity;

    @Size(max = 255)
    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Size(max = 255)
    @Column(name = "avatar_url")
    private String avatarUrl;

    @NotNull
    @Column(name = "hire_date", nullable = false)
    private Date hireDate;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "rank_level", nullable = false)
    private Integer rankLevel;

    @OneToMany(mappedBy = "staff")
    private Set<StaffEducationEntity> staffEducationEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "staffEntity")
    private Set<StaffExperienceEntity> staffExperienceEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "staffEntity")
    private Set<StaffSkillEntity> staffSkillEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "staffEntity")
    private Set<StaffQualificationEntity> staffQualificationEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "staffEntity")
    private Set<StaffSpecialityEntity> staffSpecialityEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role")
    private StaffRole staffRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_type")
    private StaffType staffType;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_status")
    private StaffStatus staffStatus;

    @OneToMany(mappedBy = "manager")
    private Set<StaffEntity> staffs = new LinkedHashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false)
    private HospitalEntity hospitalEntity;

    @ManyToMany()
    @JoinTable(name = "staff_reviews",
            joinColumns = @JoinColumn(name = "staff_id"),
            inverseJoinColumns = @JoinColumn(name = "staff_review_id"))
    private Set<ReviewEntity> reviewEntities = new LinkedHashSet<>();

    @OneToOne(mappedBy = "staffEntity")
    private DoctorEntity doctorEntity;

    @OneToMany(mappedBy = "manager")
    private List<StaffEntity> subordinates = new ArrayList<>();

    @OneToMany(mappedBy = "staffEntity")
    private List<LeaveRequestEntity> leaveRequestEntities = new ArrayList<>();

    @OneToMany(mappedBy = "staffSubstitute")
    private List<LeaveRequestEntity> leaveRequestSubstituteEntities = new ArrayList<>();

    @OneToMany(mappedBy = "staffEntity")
    private List<LeaveBalanceEntity> leaveBalanceEntities = new ArrayList<>();

    @OneToMany(mappedBy = "approvedBy")
    private Set<LeaveRequestEntity> leaveRequests = new LinkedHashSet<>();
    @OneToOne(mappedBy = "staffEntity", fetch = FetchType.LAZY)
    private TechnicianEntity technicianEntity;

    public Double getAverageRating() {
        return reviewEntities.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);
    }

    public Integer getReviewCount() {
        return reviewEntities.size();
    }
}