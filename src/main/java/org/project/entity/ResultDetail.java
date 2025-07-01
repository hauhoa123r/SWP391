package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "result_details")
public class ResultDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "result_id", nullable = false)
    private Result result;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_item_id", nullable = false)
    private TestItem testItem;

    @Column(name = "value", precision = 10, scale = 2)
    private BigDecimal value;

    @ColumnDefault("'normal'")
    @Lob
    @Column(name = "flag")
    private String flag;

    @Lob
    @Column(name = "suspected_condition")
    private String suspectedCondition;

}