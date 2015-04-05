package com.qprogramming.tasq.signup;

import org.hibernate.validator.constraints.NotBlank;

public class PasswordResetForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";

	@NotBlank(message = PasswordResetForm.NOT_BLANK_MESSAGE)
	private String id;

	@NotBlank(message = PasswordResetForm.NOT_BLANK_MESSAGE)
	private String password;

	@NotBlank(message = PasswordResetForm.NOT_BLANK_MESSAGE)
	private String confirmPassword;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPasswordConfirmed() {
		return password.equals(confirmPassword);
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmpassword) {
		this.confirmPassword = confirmpassword;
	}
}
