package com.qprogramming.tasq.agile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.qprogramming.tasq.task.worklog.DisplayWorkLog;

class SprintData {
	private Map<String, Float> left;
	private Map<String, Float> burned;
	private Map<String, Float> ideal;
	private Map<String, Float> timeBurned;
	private String message;
	private List<DisplayWorkLog> worklogs;
	private Integer totalPoints = 0;
	private String totalTime;

	public SprintData() {
		left = new LinkedHashMap<String, Float>();
		burned = new LinkedHashMap<String, Float>();
		ideal = new LinkedHashMap<String, Float>();
		timeBurned = new LinkedHashMap<String, Float>();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Float> getLeft() {
		return left;
	}

	public Map<String, Float> getIdeal() {
		return ideal;
	}

	public void setLeft(Map<String, Float> left) {
		this.left = left;
	}

	public void setIdeal(Map<String, Float> ideal) {
		this.ideal = ideal;
	}

	public Map<String, Float> getBurned() {
		return burned;
	}

	public Map<String, Float> getTimeBurned() {
		return timeBurned;
	}

	public List<DisplayWorkLog> getWorklogs() {
		return worklogs;
	}

	public void setWorklogs(List<DisplayWorkLog> worklogs) {
		this.worklogs = worklogs;
	}

	public void setBurned(Map<String, Float> pointsBurned) {
		this.burned = pointsBurned;
	}

	public void setTimeBurned(Map<String, Float> timeBurned) {
		this.timeBurned = timeBurned;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public void createIdeal(String startTime, Float value, String endTime) {
		ideal.put(startTime, value);
		ideal.put(endTime, new Float(0));
	}

	public void putToLeft(String time, Float value) {
		left.put(time, value);
	}

	public void addTime(Integer time) {
		totalTime += time;
	}

}
