package com.qprogramming.tasq.agile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;

public class AgileData {

	public static final String ALL = "ALL";
	public static final String CLOSED = "CLOSED";
	private String message;
	private List<DisplayWorkLog> worklogs;
	private String totalTime;
	protected Map<String, List<DisplayTask>> tasks;
	protected Map<String, Float> timeBurned;

	public AgileData() {
		timeBurned = new LinkedHashMap<String, Float>();
		tasks = new HashMap<String, List<DisplayTask>>();
		tasks.put(CLOSED, new LinkedList<DisplayTask>());
		tasks.put(ALL, new LinkedList<DisplayTask>());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<DisplayWorkLog> getWorklogs() {
		return worklogs;
	}

	public void setWorklogs(List<DisplayWorkLog> worklogs) {
		this.worklogs = worklogs;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public Map<String, List<DisplayTask>> getTasks() {
		return tasks;
	}

	public void setTasks(Map<String, List<DisplayTask>> tasks) {
		this.tasks = tasks;
	}

	public Map<String, Float> getTimeBurned() {
		return timeBurned;
	}

	public void setTimeBurned(Map<String, Float> timeBurned) {
		this.timeBurned = timeBurned;
	}

	public void addTime(Integer time) {
		totalTime += time;
	}
}
