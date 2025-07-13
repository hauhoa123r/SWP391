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
public class TestType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_type_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "test_type_name", nullable = false, length = 100)
    private String testTypeName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @OneToMany(mappedBy = "testType")
    private Set<ResultEntity> results = new LinkedHashSet<>();

    @OneToMany(mappedBy = "testType")
    private Set<TestItem> testItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "testType")
    private Set<TestRequestEntity> testRequests = new LinkedHashSet<>();

}