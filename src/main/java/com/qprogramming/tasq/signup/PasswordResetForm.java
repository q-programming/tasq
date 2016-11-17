package com.qprogramming.tasq.signup;

import org.hibernate.validator.constraints.NotEmpty;


public class PasswordResetForm {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";

    @NotEmpty(message = PasswordResetForm.NOT_BLANK_MESSAGE)
    private String id;

    @NotEmpty(message = PasswordResetForm.NOT_BLANK_MESSAGE)
    private String password;

    @NotEmpty(message = PasswordResetForm.NOT_BLANK_MESSAGE)
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
