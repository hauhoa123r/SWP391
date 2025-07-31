package org.project.model.dto;

import lombok.Data;

@Data
public class SampleDTO {
    private String name;
    private String unit;
    private String low;
    private String high;
    private String lowSuspect;
    private String highSuspect;
}