package org.project.model.dto;

import org.project.enums.Label;

public class ProductViewDTO {
	private Long id; 
	private String name; 
	private String categoryNames;  
	private Double price; 
	private Integer stockQuantities; 
	private Label label; 
	
	//non-arg constructor 
	public ProductViewDTO() {
	} 
	
	// constructor with parameters 
	public ProductViewDTO(Long id, String name, String categoryNames, Double price, Integer stockQuantities, Label label) {
		this.id = id;
		this.name = name;
		this.categoryNames = categoryNames;
		this.price = price;
		this.stockQuantities = stockQuantities;
		this.label = label;
	} 
	
	public Long getId() {
		return id;
	} 
	public void setId(Long id) {
		this.id = id;
	} 
	public String getName() {
		return name;
	} 
	public void setName(String name) {
		this.name = name;
	} 
	public String getCategoryNames() {
		return categoryNames;
	} 
	public void setCategoryNames(String categoryNames) {
		this.categoryNames = categoryNames;
	} 
	public Double getPrice() {
		return price;
	} 
	public void setPrice(Double price) {
		this.price = price;
	} 
	public Integer getStockQuantities() {
		return stockQuantities;
	} 
	public void setStockQuantities(Integer stockQuantities) {
		this.stockQuantities = stockQuantities;
	} 
	public Label getLabel() {
		return label;
	} 
	public void setLabel(Label label) {
		this.label = label;
	} 
	@Override 
	public String toString() {
		return "ProductViewDTO [id=" + id + ", name=" + name + ", categoryNames=" + categoryNames + ", price=" + price
				+ ", stockQuantities=" + stockQuantities + ", label=" + label + "]";
	} 
}
