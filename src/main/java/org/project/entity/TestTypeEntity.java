package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "test_types")
public class TestTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_type_id", nullable = false)
    private Long id;

    @Size(min = 1, max = 100)
    @Column(name = "test_type_name", nullable = false, length = 100)
    private String testTypeName;

    @Column(name = "status")
    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToMany(mappedBy = "testTypeEntity")
    private Set<ResultEntity> results = new LinkedHashSet<>();

    @OneToMany(mappedBy = "testTypeEntity")
    private Set<TestItemEntity> testItemEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "testTypeEntity")
    private Set<TestRequestEntity> testRequests = new LinkedHashSet<>();

}