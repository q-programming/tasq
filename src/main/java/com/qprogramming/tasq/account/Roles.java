package com.qprogramming.tasq.account;

import com.qprogramming.tasq.support.Utils;

public enum Roles {
	ROLE_USER("role.user"), ROLE_ADMIN("role.admin"), ROLE_REPORTER(
			"role.reporter"), ROLE_VIEWER("role.viewer");

	private String code;

	private Roles(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	
	
	/**
	 * Checks if currently logged user have ROLE_REPORTER authority 
	 * @return
	 */
	public static boolean isReporter(){
		return Utils.getCurrentAccount().getIsReporter();
	}
	
	/**
	 * Checks if currently logged user have ROLE_USER authority 
	 * @return
	 */
	public static boolean isUser(){
		return Utils.getCurrentAccount().getIsUser();
	}
	/**
	 * Checks if currently logged user have ROLE_ADMIN authority 
	 * @return
	 */
	public static boolean isAdmin(){
		return Utils.getCurrentAccount().getIsAdmin();
	}

}
