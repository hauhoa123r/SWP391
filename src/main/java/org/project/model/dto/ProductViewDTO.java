package org.project.model.dto;

import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;

import java.math.BigDecimal;

public class ProductViewDTO {
	private Long id; 
	private ProductType type;  
	private String name; 
	private String description; 
	private BigDecimal price; 
	private String unit; 
	private ProductStatus productStatus; 
	private Integer stockQuantities; 
	private String imageUrl; 
	private Label label; 
	private String categoryNames; 
	private String tagNames;
	private String additionalInfos; 
	
	public ProductViewDTO() {
	} 
	
	public ProductViewDTO(Long id, ProductType type, String name, String description, BigDecimal price, String unit, ProductStatus productStatus, Integer stockQuantities, String imageUrl, Label label, String categoryNames, String tagNames, String additionalInfos) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.price = price;
		this.unit = unit;
		this.productStatus = productStatus;
		this.stockQuantities = stockQuantities;
		this.imageUrl = imageUrl;
		this.label = label;
		this.categoryNames = categoryNames;
		this.tagNames = tagNames;
		this.additionalInfos = additionalInfos;
	} 
	
	public Long getId() {
		return id;
	} 
	
	public void setId(Long id) {
		this.id = id;
	} 
	
	public ProductType getType() {
		return type;
	} 
	
	public void setType(ProductType type) {
		this.type = type;
	} 
	
	public String getName() {
		return name;
	} 
	public void setName(String name) {
		this.name = name;
	} 
	
	public String getDescription() {
		return description;
	} 
	public void setDescription(String description) {
		this.description = description;
	} 
	
	public BigDecimal getPrice() {
		return price;
	} 
	public void setPrice(BigDecimal price) {
		this.price = price;
	} 
	
	public String getUnit() {
		return unit;
	} 
	public void setUnit(String unit) {
		this.unit = unit;
	} 
	
	public ProductStatus getProductStatus() {
		return productStatus;
	} 
	public void setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
	} 
	
	public Integer getStockQuantities() {
		return stockQuantities;
	} 
	public void setStockQuantities(Integer stockQuantities) {
		this.stockQuantities = stockQuantities;
	} 
	
	public String getImageUrl() {
		return imageUrl;
	} 
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	} 
	
	public Label getLabel() {
		return label;
	} 
	public void setLabel(Label label) {
		this.label = label;
	} 
	
	public String getCategoryNames() {
		return categoryNames;
	} 
	public void setCategoryNames(String categoryNames) {
		this.categoryNames = categoryNames;
	} 
	
	public String getTagNames() {
		return tagNames;
	} 
	public void setTagNames(String tagNames) {
		this.tagNames = tagNames;
	} 
	
	public String getAdditionalInfos() {
		return additionalInfos;
	} 
	public void setAdditionalInfos(String additionalInfos) {
		this.additionalInfos = additionalInfos;
	} 
	
	@Override 
	public String toString() {
		return "ProductViewDTO{" +
				"id=" + id +
				", type='" + type + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", price=" + price +
				", unit='" + unit + '\'' +
				", productStatus=" + productStatus +
				", stockQuantities=" + stockQuantities +
				", imageUrl='" + imageUrl + '\'' +
				", label=" + label +
				", categoryNames='" + categoryNames + '\'' +
				", tagNames='" + tagNames + '\'' +
				", additionalInfos='" + additionalInfos + '\'' +
				'}';
	} 
}
