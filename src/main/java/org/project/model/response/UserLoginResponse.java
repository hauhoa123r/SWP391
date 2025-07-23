package org.project.model.response;

import lombok.Data;

@Data
public class UserLoginResponse {
	private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
}
