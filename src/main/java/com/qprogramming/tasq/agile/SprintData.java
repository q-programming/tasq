package com.qprogramming.tasq.agile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.qprogramming.tasq.task.worklog.DisplayWorkLog;

class SprintData {
	private Map<String, Integer> left;
	private Map<String, Float> burned;
	private Map<String, Integer> ideal;
	private Map<String, Float> timeBurned;
	private String message;
	private List<DisplayWorkLog> worklogs;
	private Integer totalPoints = 0;
	private Integer totalTime = 0;

	public SprintData() {
		left = new LinkedHashMap<String, Integer>();
		burned = new LinkedHashMap<String, Float>();
		ideal = new LinkedHashMap<String, Integer>();
		timeBurned = new LinkedHashMap<String, Float>();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Integer> getLeft() {
		return left;
	}

	public Map<String, Integer> getIdeal() {
		return ideal;
	}

	public void setLeft(Map<String, Integer> left) {
		this.left = left;
	}

	public void setIdeal(Map<String, Integer> ideal) {
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

	public Integer getTotalTime() {
		return totalTime;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public void setTotalTime(Integer totalTime) {
		this.totalTime = totalTime;
	}

	public void createIdeal(String startTime, int value, String endTime) {
		ideal.put(startTime, value);
		ideal.put(endTime, 0);
	}

	public void putToLeft(String time, Integer value) {
		left.put(time, value);
	}

	public void addTime(Integer time) {
		totalTime += time;
	}

}
