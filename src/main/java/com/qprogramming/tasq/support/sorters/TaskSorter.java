package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;

import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;

public class TaskSorter implements Comparator<Task> {

	public enum SORTBY {
		NAME, START_DATE, ID, DUE_DATE, PRIORITY, ORDER
	}

	private SORTBY sortBy;
	private boolean isDescending;

	public TaskSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		this.isDescending = isDescending;
	}

	public int compare(Task a, Task b) {
		int result;
		switch (sortBy) {
		case NAME:
			result = a.getName().compareTo(b.getName());
			break;
		case ORDER:
			if (a.getTaskOrder() != null && b.getTaskOrder() == null) {
				result = 1;
			} else if (a.getTaskOrder() == null && b.getTaskOrder() != null) {
				result = -11;
			} else if (a.getTaskOrder() == null && b.getTaskOrder() == null) {
				result = compareByID(a, b);
			} else {
				if (a.getTaskOrder() > b.getTaskOrder()) {
					result = -1;
				} else if (a.getTaskOrder() < b.getTaskOrder()) {
					result = 1;
				} else {
					result = 0;
				}
			}
			break;
		case ID:
			result = compareByID(a, b);
			break;
		case DUE_DATE:
			if (a.getRawDue_date().before(b.getRawDue_date())) {
				result = -1;
			} else {
				result = 1;
			}
			break;
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
			break;
		default:
			result = 0;
			break;
		}
		return isDescending ? -result : result;
	}

	private int compareByID(Task a, Task b) {
		int result;
		String aId;
		String bId;
		if(a.isSubtask()&& b.isSubtask()){
			aId = a.getId().split("-")[1].split("/")[1];
			bId = b.getId().split("-")[1].split("/")[1];
		}else{
			aId = a.getId().split("-")[1].split("/")[0];
			bId = b.getId().split("-")[1].split("/")[0];
		}
		if (Integer.parseInt(aId) > Integer.parseInt(bId)) {
			result = -1;
		} else {
			result = 1;
		}
		return result;
	}
}
