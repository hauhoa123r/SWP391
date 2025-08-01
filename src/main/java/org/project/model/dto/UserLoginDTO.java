package org.project.model.dto;

public class UserLoginDTO {
	//login just using username and password
	private String email;
	private String passwordHash;

	public UserLoginDTO() {
	}

	public UserLoginDTO(String email, String passwordHash) {
		this.email = email;
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

}
