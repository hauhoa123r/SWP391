package org.project.model.dto;

import java.util.Objects;

public class ReviewDTO {
	private String patientFullName; 
	private String patientImageUrl; 
	private String content; 
	private Integer rating; 
	public ReviewDTO() {
	} 
	public ReviewDTO(String patientFullName, String patientImageUrl, String content, Integer rating) {
		this.patientFullName = patientFullName;
		this.patientImageUrl = patientImageUrl;
		this.content = content;
		this.rating = rating;
	} 
	public String getPatientFullName() {
		return patientFullName;
	} 
	public void setPatientFullName(String patientFullName) {
		this.patientFullName = patientFullName;
	} 
	public String getPatientImageUrl() {
		return patientImageUrl;
	} 
	public void setPatientImageUrl(String patientImageUrl) {
		this.patientImageUrl = patientImageUrl;
	} 
	public String getContent() {
		return content;
	} 
	public void setContent(String content) {
		this.content = content;
	} 
	public Integer getRating() {
		return rating;
	} 
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	@Override 
	public String toString() {
		return "ReviewDTO [patientFullName=" + patientFullName + ", patientImageUrl=" + patientImageUrl + ", content="
				+ content + ", rating=" + rating + "]";
	}
	//Overriding equals and hashCode methods for proper comparison 
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof ReviewDTO)) return false;
	    ReviewDTO that = (ReviewDTO) o;

	    return Objects.equals(patientFullName, that.patientFullName) &&
	           Objects.equals(patientImageUrl, that.patientImageUrl) &&
	           Objects.equals(content, that.content) &&
	           Objects.equals(rating, that.rating);
	}
	@Override
	public int hashCode() {
	    return Objects.hash(patientFullName, patientImageUrl, content, rating);
	}
	
}
