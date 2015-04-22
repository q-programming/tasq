package com.qprogramming.tasq.agile;

import java.util.LinkedHashMap;
import java.util.Map;

public class KanbanData extends AgileData {
	private Map<String, Integer> open;
	private Map<String, Integer> inProgress;
	private Map<String, Integer> closed;
	private String startStop;

	public KanbanData() {
		open = new LinkedHashMap<String, Integer>();
		closed = new LinkedHashMap<String, Integer>();
		inProgress = new LinkedHashMap<String, Integer>();
	}

	public Map<String, Integer> getOpen() {
		return open;
	}

	public void setOpen(Map<String, Integer> open) {
		this.open = open;
	}

	public Map<String, Integer> getClosed() {
		return closed;
	}

	public void setClosed(Map<String, Integer> closed) {
		this.closed = closed;
	}

	public Map<String, Integer> getInProgress() {
		return inProgress;
	}

	public void setInProgress(Map<String, Integer> inProgress) {
		this.inProgress = inProgress;
	}

	public String getStartStop() {
		return startStop;
	}

	public void setStartStop(String startStop) {
		this.startStop = startStop;
	}

	public void putToOpen(String date, Integer value) {
		this.open.put(date, value);
	}

	public void putToClosed(String date, Integer value) {
		this.closed.put(date, value);
	}
	public void putToInProgress(String date, Integer value) {
		this.inProgress.put(date, value);
	}


}
