package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierEntityDTO {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Set<SupplierTransactionDTO> supplierTransactions;
}