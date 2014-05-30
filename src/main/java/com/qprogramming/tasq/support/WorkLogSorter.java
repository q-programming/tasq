package com.qprogramming.tasq.support;

import java.util.Comparator;

import com.qprogramming.tasq.task.worklog.WorkLog;

public class WorkLogSorter implements Comparator<WorkLog> {

	private boolean isDescending;

	public WorkLogSorter(boolean isDescending) {
		this.isDescending = isDescending;
	}

	public int compare(WorkLog a, WorkLog b) {
		int result = 0;
		if (a.getRawTime().before(b.getRawTime())) {
			result = -1;
		} else if (a.getRawTime() == b.getRawTime()) {
			result = 0;
		} else {
			result = 1;
		}
		return isDescending ? -result : result;
	}
}
