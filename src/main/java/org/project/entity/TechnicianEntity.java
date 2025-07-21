package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "technicians", schema = "swp391")
public class TechnicianEntity {
    @Id
    @Column(name = "technician_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private StaffEntity staffEntity;

//    @OneToMany
//    private Set<TestRequestItemEntity> testRequestItemEntities = new LinkedHashSet<>();

    @ColumnDefault("'JUNIOR_TECHNICIAN'")
    @Column(name = "technician_rank", columnDefinition = "enum not null")
    private Object technicianRank;
}