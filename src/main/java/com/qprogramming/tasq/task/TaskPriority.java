package com.qprogramming.tasq.task;

public enum TaskPriority {
	BLOCKER("task.priority.blocker"), CRITICAL("task.priority.critical"), MAJOR(
			"task.priority.major"), MINOR("task.priority.minor"), TRIVIAL(
			"task.priority.trivial");

	private String code;

	private TaskPriority(String code) {
		this.code = code;
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
	
	public String getImgcode(){
		return super.toString().toLowerCase();
	}

	public String getCode() {
		return code;
	}

}
