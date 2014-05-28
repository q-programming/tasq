/**
 * 
 */
package com.qprogramming.tasq.task.worklog;


/**
 * @author romanjak
 * @date 28 maj 2014
 */
public enum LogType {
	CREATE("logtype.create"), CHANGE("logtype.change"), LOG("logtype.log"), STATUS(
			"logtype.status"), ESTIMATE("logtype.estimate"), CLOSED("logtype.closed");

	private String type;
	/**
	 * 
	 */
	private LogType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return type;
	}
}
