package com.ga2sa.helpers.forms;

public class LoginForm {
	
	private String username;
	private String password;
	
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

	public String validate() {
		if (username == null || username.isEmpty()) {
			return "Username is empty.";
		}
		
		if (password == null || password.isEmpty()) {
			return "Password is empty.";
		}
		
		return null;
	}

}
