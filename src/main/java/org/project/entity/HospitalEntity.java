package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.project.enums.HospitalStatus;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hospitals", schema = "swp391")
@FieldNameConstants
public class HospitalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Size(max = 255)
    @NotNull
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Lob
    @Column(name = "avatar_url")
    private String avatarUrl;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hospitalEntity", fetch = FetchType.LAZY)
    private Set<StaffEntity> staffEntities = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "hospital_status")
    private HospitalStatus hospitalStatus;

}