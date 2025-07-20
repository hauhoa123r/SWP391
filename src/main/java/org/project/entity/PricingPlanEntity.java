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
@Table(name = "pricing_plans", schema = "swp391")
@FieldNameConstants
public class PricingPlanEntity {
    @Id
    @Column(name = "pricing_plan_id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pricing_plan_id", nullable = false)
    private ProductEntity productEntity;

    @OneToMany
    private Set<PricingPlanFeatureEntity> pricingPlanFeatureEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "pricingPlanEntity")
    private Set<PricingPlanSubscriptionEntity> pricingPlanSubscriptionEntities = new LinkedHashSet<>();

/*
 TODO [Reverse Engineering] create field to map the 'plan_type' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "plan_type", columnDefinition = "enum not null")
    private Object planType;
*/
}