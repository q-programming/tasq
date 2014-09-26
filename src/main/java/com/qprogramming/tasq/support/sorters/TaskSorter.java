package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;

import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;

public class TaskSorter implements Comparator<Task> {

	public static enum SORTBY {
		NAME, START_DATE, ID, DUE_DATE, PRIORITY;
	}

	private SORTBY sortBy;
	private boolean isDescending;

	public TaskSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		this.isDescending = isDescending;
	}

	public int compare(Task a, Task b) {
		int result = 0;
		switch (sortBy) {
		case NAME:
			result = a.getName().compareTo(b.getName());
			return isDescending ? -result : result;
		case ID:
			String a_id = a.getId().split("-")[1];
			String b_id = b.getId().split("-")[1];
			if (Integer.parseInt(a_id) > Integer.parseInt(b_id)) {
				result = 1;
			} else {
				result = -1;
			}
			return isDescending ? -result : result;
		case DUE_DATE:
			if (a.getRawDue_date().before(b.getRawDue_date())) {
				result = -1;
			} else {
				result = 1;
			}
			return isDescending ? -result : result;
		case PRIORITY:
			if (((TaskPriority) a.getPriority()).getPriority() == ((TaskPriority) b
					.getPriority()).getPriority()) {
				result = 0;
			} else if (((TaskPriority) a.getPriority()).getPriority() < ((TaskPriority) b
					.getPriority()).getPriority()) {
				result = 1;
			} else {
				result = -1;
			}
			return isDescending ? -result : result;
		default:
			result = 0;
			break;
		}
		return isDescending ? -result : result;

	}
}
