package com.qprogramming.tasq.task;

public enum TaskType {
	TASK, USER_STORY, ISSUE, BUG, IDLE;

	@Override
	public String toString() {
		// only capitalize the first letter
		String s = super.toString();
		s = s.replaceAll("_", " ");
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

}
