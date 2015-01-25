package com.qprogramming.tasq.task;

public enum SubTaskType {
	SUBTASK, SUBBUG, IDLE;

	@Override
	public String toString() {
		// only capitalize the first letter
		String s = super.toString();
		return s.substring(0, 1) + s.substring(1).toLowerCase();
	}

	public String getEnum() {
		return super.toString();
	}

	public String getDescription() {
		return toString();
	}

	public String getCode() {
		return super.toString().toLowerCase();
	}

	public static SubTaskType toType(String string) {
		return valueOf(string.toUpperCase());
	}
}
