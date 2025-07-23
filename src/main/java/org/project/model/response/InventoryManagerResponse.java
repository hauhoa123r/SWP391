package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryManagerResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String inventoryManagerRank;
} 