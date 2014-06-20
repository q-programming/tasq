package com.qprogramming.tasq.task;

public enum TaskPriority {
	BLOCKER("task.priority.blocker", 0), CRITICAL("task.priority.critical", 1), MAJOR(
			"task.priority.major", 2), MINOR("task.priority.minor", 3), TRIVIAL(
			"task.priority.trivial", 4);

	private String code;
	private int priority;

	private TaskPriority(String code, int priority) {
		this.code = code;
		this.priority = priority;
	}

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

	public String getImgcode() {
		return super.toString().toLowerCase();
	}

	public String getCode() {
		return code;
	}

	public int getPriority() {
		return priority;
	}

}
