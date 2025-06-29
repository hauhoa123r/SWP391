package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "reference_ranges")
public class ReferenceRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "range_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_item_id", nullable = false)
    private TestItem testItem;

    @Lob
    @Column(name = "gender")
    private String gender;

    @NotNull
    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    @NotNull
    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    @Column(name = "min_value", precision = 10, scale = 2)
    private BigDecimal minValue;

    @Column(name = "max_value", precision = 10, scale = 2)
    private BigDecimal maxValue;

    @Lob
    @Column(name = "suspected_condition")
    private String suspectedCondition;

}