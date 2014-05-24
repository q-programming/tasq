package com.qprogramming.tasq.support;

import java.util.Comparator;

import com.qprogramming.tasq.projects.Project;

public class ProjectSorter implements Comparator<Project> {

	public static enum SORTBY {
		NAME, START_DATE, LAST_VISIT;
	}

	private SORTBY sortBy;
	private boolean isDescending;

	public ProjectSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		this.isDescending = isDescending;
	}

	public int compare(Project a, Project b) {
		int result = 0;
		switch (sortBy) {
		case LAST_VISIT:
			if (a.getLastVisit() == null) {
				result = -1;
			} else if (b.getLastVisit() == null) {
				result = 1;
			} else if (a.getLastVisit() == b.getLastVisit()) {
				result = 0;
			} else if (a.getLastVisit().before(b.getLastVisit())) {
				result = -1;
			} else {
				result = 1;
			}
			return isDescending ? -result : result;
		case NAME:
			result = a.getName().compareTo(b.getName());
			return isDescending ? -result : result;
		default:
			result = 0;
			break;
		}
		return isDescending ? -result : result;

	}
}
