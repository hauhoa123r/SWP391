package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MedicineListVResponse {
    private Long id;
    private String name;
    private String description;
    private String price;
    private String imageUrl;
}
