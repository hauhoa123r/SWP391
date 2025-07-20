package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "services", schema = "swp391")
@FieldNameConstants
public class ServiceEntity {
    @Id
    @Column(name = "service_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ProductEntity productEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity departmentEntity;

    @OneToMany(mappedBy = "serviceEntity")
    private final Set<AppointmentEntity> appointmentEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "serviceEntity")
    private final Set<ServiceFeatureEntity> serviceFeatureEntities = new LinkedHashSet<>();

}