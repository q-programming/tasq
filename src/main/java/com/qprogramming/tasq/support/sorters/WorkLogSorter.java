package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;

import com.qprogramming.tasq.task.worklog.WorkLog;

public class WorkLogSorter implements Comparator<WorkLog> {

	private boolean isDescending;

	public WorkLogSorter(boolean isDescending) {
		this.isDescending = isDescending;
	}

	public int compare(WorkLog a, WorkLog b) {
		int result;
		if (a.getRawTimeLogged().before(b.getRawTimeLogged())) {
			result = -1;
		} else if (a.getRawTimeLogged() == b.getRawTimeLogged()) {
			result = 0;
		} else {
			result = 1;
		}
		return isDescending ? -result : result;
	}
}
