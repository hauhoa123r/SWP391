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
public class InventoryManagerDTO {
    private Long id;
    private StaffDTO staffEntity;
    private Set<SupplierTransactionDTO> supplierTransactionEntities;
}