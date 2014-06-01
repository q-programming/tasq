package com.qprogramming.tasq.support;

import java.util.Comparator;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.task.Task;

public class TaskSorter implements Comparator<Task> {

	public static enum SORTBY {
		NAME, START_DATE, LAST_VISIT,ID;
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
		case LAST_VISIT:
			if (a.isActive()) {
				result = 1;
			} else if (b.isActive()) {
				result = -1;
			} else if (a.getLast_visit() == null) {
				result = -1;
			} else if (b.getLast_visit() == null) {
				result = 1;
			} else if (a.getLast_visit() == b.getLast_visit()) {
				result = 0;
			} else if (a.getLast_visit().before(b.getLast_visit())) {
				result = -1;
			} else {
				result = 1;
			}
			return isDescending ? -result : result;
		case NAME:
			result = a.getName().compareTo(b.getName());
			return isDescending ? -result : result;
		case ID:
			String a_id = a.getId().split("-")[1];
			String b_id = b.getId().split("-")[1];
			if(Integer.parseInt(a_id) > Integer.parseInt(b_id)){
				result = 1;
			}else{
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
