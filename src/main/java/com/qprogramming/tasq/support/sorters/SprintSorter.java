package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;

import com.qprogramming.tasq.agile.Sprint;

public class SprintSorter implements Comparator<Sprint> {

	public int compare(Sprint a, Sprint b) {
		if (a.getSprint_no() > b.getSprint_no()) {
			return 1;
		} else {
			return -1;
		}
	}
}
