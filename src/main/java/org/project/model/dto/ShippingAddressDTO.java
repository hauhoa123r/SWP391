package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDTO {
    //user's dto
    private UserPatientDTO user;
    //province
    private String province;
    //district
    private String district;
    //commune
    private String commune;
    //detail address
    private String detailAddress;
    //address type
    private String addressType;
}
