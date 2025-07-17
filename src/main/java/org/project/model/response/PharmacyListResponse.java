package org.project.model.response;

import org.project.enums.Label;

import lombok.Data;

@Data
public class PharmacyListResponse {
	private Long id;
    private String name;
    private String type; 
    private Long price;
    private String unit;
    private String imageUrl; 
    private String description;
    private Label label; 
}
