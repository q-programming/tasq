package com.qprogramming.tasq.signup;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.support.Utils;

public class SignupForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SignupForm.EMAIL_MESSAGE)
	private String email;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String username;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String firstname;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String surname;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String password;

	@NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String confirmPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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

	public String getFirstname() {
		return firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Account createAccount() {
		Account account = new Account(getEmail(), getPassword(), getUsername(), Roles.ROLE_VIEWER);
		account.setName(getFirstname());
		account.setSurname(getSurname());
		account.setLanguage(Utils.getDefaultLocale().getLanguage());
		return account;
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
