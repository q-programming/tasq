package com.qprogramming.tasq.support;

import java.util.Comparator;

import com.qprogramming.tasq.projects.Project;

public class ProjectSorter implements Comparator<Project> {

	public static enum SORTBY {
		NAME, START_DATE, LAST_VISIT;
	}

	private SORTBY sortBy;
	private boolean isDescending;
	private Long active_project;

	public ProjectSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		this.isDescending = isDescending;
	}

	/**
	 * @param active_project
	 * @param isDescending2
	 */
	public ProjectSorter(SORTBY sortBy, Long active_project,
			boolean isDescending) {
		this.active_project = active_project;
		this.isDescending = isDescending;
		this.sortBy = sortBy;
	}

	public int compare(Project a, Project b) {
		int result = 0;
		switch (sortBy) {
		// sort by active , then just by name
		case LAST_VISIT:
			if (a.getId().equals(active_project)) {
				result = 1;
			} else if (b.getId().equals(active_project)) {
				result = -1;
			} else {
				result = a.getName().compareTo(b.getName());
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
