package com.qprogramming.tasq.error;

import org.springframework.context.MessageSource;

import com.qprogramming.tasq.support.Utils;

public class TasqAuthException extends RuntimeException {
	/**
	 * 
	 */
	MessageSource msg;

	String message;

	private static final long serialVersionUID = 1L;

	public TasqAuthException() {
	}

	/**
	 * Throws authorization exception MessageSource has to be passed form calling method
	 * @param msg
	 */
	public TasqAuthException(MessageSource msg) {
		this.msg = msg;
		String role = msg.getMessage(Utils.getCurrentAccount().getRole()
				.getCode(), null, Utils.getCurrentLocale());
		this.message = msg.getMessage("role.error.auth",
				new Object[] { role }, Utils.getCurrentLocale());
	}
	
	/**
	 * Throws authorization exception with given code, MessageSource has to be passed form calling method
	 * @param msg
	 */
	public TasqAuthException(MessageSource msg, String code) {
		this.msg = msg;
		this.message = msg.getMessage(code,	null, Utils.getCurrentLocale());
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
