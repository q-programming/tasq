package com.qprogramming.tasq.projects;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProjectChart {
	private Map<String, Integer> created;
	private Map<String, Integer> closed;

	public ProjectChart() {
		created = new LinkedHashMap<String, Integer>();
		closed = new LinkedHashMap<String, Integer>();
	}

	public Map<String, Integer> getCreated() {
		return created;
	}

	public Map<String, Integer> getClosed() {
		return closed;
	}
}
