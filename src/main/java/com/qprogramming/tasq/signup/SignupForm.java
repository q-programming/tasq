package com.qprogramming.tasq.signup;

import org.hibernate.validator.constraints.*;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Account.Role;

public class SignupForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SignupForm.EMAIL_MESSAGE)
	private String email;
    
    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
    private String name;
    
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Account createAccount() {
		Account account = new Account(getEmail(), getPassword(), Role.ROLE_USER);
		account.setName(getName());
		account.setSurname(getSurname());
        return account;
	}
	
	public boolean isPasswordConfirmed(){
		return password.equals(confirmPassword);
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmpassword) {
		this.confirmPassword = confirmpassword;
	}
}
