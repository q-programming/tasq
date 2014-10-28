package com.qprogramming.tasq.support;
/**
 * Helper class to produce json readable result to all post events
 * @author Khobar
 *
 */
public class ResultData {
	public String code;
	public String message;

	public ResultData(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
