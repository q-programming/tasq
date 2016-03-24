package com.qprogramming.tasq.task.link;

public enum TaskLinkType {
	RELATES_TO("task.link.relates"), BLOCKS("task.link.blocks"), IS_BLOCKED_BY(
			"task.link.isBlocked"), DUPLICATES("task.link.duplicates"), IS_DUPLICATED_BY(
			"task.link.isDuplicated");

	private String localCode;

	TaskLinkType(String code) {
		this.localCode = code;
	}

	public String getDescription() {
		return toString();
	}

	public String getCode() {
		return localCode;
	}

}
