package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;

import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.TaskPriority;

public class DisplayTaskSorter implements Comparator<DisplayTask> {

	public enum SORTBY {
		NAME, START_DATE, ID, DUE_DATE, PRIORITY
	}

	private SORTBY sortBy;
	private boolean isDescending;

	public DisplayTaskSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		this.isDescending = isDescending;
	}

	@Override
	public int compare(DisplayTask a, DisplayTask b) {
		int result;
		switch (sortBy) {
		case NAME:
			result = a.getName().compareTo(b.getName());
			break;
		case ID:
			result = compareByID(a, b);
			break;
		case PRIORITY:
			if (((TaskPriority) a.getPriority()).getPriority() == ((TaskPriority) b.getPriority()).getPriority()) {
				result = 0;
			} else
				if (((TaskPriority) a.getPriority()).getPriority() < ((TaskPriority) b.getPriority()).getPriority()) {
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

	private int compareByID(DisplayTask a, DisplayTask b) {
		int result;
		String aId;
		String bId;
		if (a.isSubtask() && b.isSubtask()) {
			aId = a.getId().split("-")[1].split("/")[1];
			bId = b.getId().split("-")[1].split("/")[1];
		} else {
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
