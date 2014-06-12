/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

public enum LogType {
	CREATE("log.type.create"), CHANGE("log.type.change"), LOG("log.type.log"), STATUS(
			"log.type.status"), ESTIMATE("log.type.estimate"), CLOSED(
			"log.type.closed"), COMMENT("log.type.comment"), EDITED(
			"log.type.edited");

	private String code;

	private LogType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return code;
	}
}
