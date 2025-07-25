//package org.project.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDate;
//
//@Getter
//@Setter
//@Entity
//@Table(name = "batches")
//public class Batch {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "batch_id", nullable = false)
//    private Long id;
//
//    @Size(max = 100)
//    @NotNull
//    @Column(name = "batch_code", nullable = false, length = 100)
//    private String batchCode;
//
//    @NotNull
//    @Column(name = "quantity", nullable = false)
//    private Integer quantity;
//
//    @Column(name = "manufacture_date")
//    private LocalDate manufactureDate;
//
//    @Column(name = "expiry_date")
//    private LocalDate expiryDate;
//
//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "medicine_id", nullable = false)
//    private MedicineEntity medicine;
//
//}