package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyListResponse {
    private String name;
    private String type; 
    private Long price;
    private String unit;
    private String avatar; 
}
