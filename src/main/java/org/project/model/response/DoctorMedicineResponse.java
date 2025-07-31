package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorMedicineResponse {
    private Long medicineId;
    private String medicineName;
    private String typeMedicine;
    private Long price;
}
