package org.project.entity;

import jakarta.persistence.*;
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
@Table(name = "medicines", schema = "swp391")
@FieldNameConstants
public class MedicineEntity {
    @Id
    @Column(name = "medicine_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private ProductEntity productEntity;

    @OneToMany
    private Set<MedicineIngredientEntity> medicineIngredientEntities = new LinkedHashSet<>();

    @OneToMany
    private Set<PrescriptionItemEntity> prescriptionItemEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Batch> batches = new LinkedHashSet<>();
}