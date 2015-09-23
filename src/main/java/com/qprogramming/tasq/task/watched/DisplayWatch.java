package com.qprogramming.tasq.task.watched;

import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.task.TaskType;

public class DisplayWatch {

	private String id;
	private String name;
	private TaskType type;
	private Integer watchCount;

	public DisplayWatch() {
		// TODO Auto-generated constructor stub
	}

	public DisplayWatch(WatchedTask task) {
		BeanUtils.copyProperties(task, this);
		this.watchCount = task.getWatchers().size();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public Integer getWatchCount() {
		return watchCount;
	}

	public void setWatchCount(Integer watchCount) {
		this.watchCount = watchCount;
	}
}
