package com.qprogramming.tasq.task;

public enum TaskState {

	TO_DO("task.state.todo"), ONGOING("task.state.ongoing"), BLOCKED(
			"task.state.blocked"),COMPLETE("task.state.complete"), CLOSED("task.state.closed");

	private String localCode;

	private TaskState(String code) {
		this.localCode = code;
	}

	@Override
	public String toString() {
		// only capitalize the first letter
		String s = super.toString();
		s = s.replaceAll("_", " ");
		return s.substring(0, 1) + s.substring(1).toLowerCase();
	}

	public String getDescription() {
		return toString();
	}

	public String getCode() {
		return localCode;
	}
}
