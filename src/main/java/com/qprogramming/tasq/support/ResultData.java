package com.qprogramming.tasq.support;
/**
 * Helper class to produce json readable result to all post events
 * @author Khobar
 *
 */
public class ResultData {
	public static final String OK = "OK";
	public static final String WARNING = "WARNING";
	public static final String ERROR = "ERROR";
	public String code;
	public String message;

	public ResultData(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
