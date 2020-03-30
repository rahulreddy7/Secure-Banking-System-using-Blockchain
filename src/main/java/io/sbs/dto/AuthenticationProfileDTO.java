package io.sbs.dto;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import io.sbs.constant.UserType;

public class AuthenticationProfileDTO implements Serializable {
	
	private String username;
	private String password;
	private UserType role;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public UserType getRole() {
		return role;
	}
	public void setRole(UserType role) {
		this.role = role;
	}

}
