package com.qprogramming.tasq.agile;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.qprogramming.tasq.task.DisplayTask;

class SprintData extends AgileData{
	private Map<String, Float> left;
	private Map<String, Float> burned;
	private Map<String, Float> ideal;
	private Integer totalPoints = 0;

	public SprintData() {
		left = new LinkedHashMap<String, Float>();
		burned = new LinkedHashMap<String, Float>();
		ideal = new LinkedHashMap<String, Float>();
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

	public void setBurned(Map<String, Float> pointsBurned) {
		this.burned = pointsBurned;
	}

	public Integer getTotalPoints() {
		return totalPoints;
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


}
