package com.qprogramming.tasq.support.sorters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.worklog.WorkLog;

@RunWith(MockitoJUnitRunner.class)
public class WorkLogSorterTest {
	private List<WorkLog> wlList;

	@Before
	public void setUp() {
		wlList = createList();
	}

	@Test
	public void sortWorkLogSorterTest() {
		Collections.sort(wlList, new WorkLogSorter(false));
		Assert.assertEquals(Utils.convertStringToDate("01-12-2014"), wlList
				.get(0).getRawTimeLogged());
		Collections.sort(wlList, new WorkLogSorter(true));
		Assert.assertEquals(Utils.convertStringToDate("04-12-2014"), wlList
				.get(0).getRawTimeLogged());
	}

	private List<WorkLog> createList() {
		List<WorkLog> wlList = new LinkedList<WorkLog>();
		wlList.add(createWorkLog("04-12-2014"));
		wlList.add(createWorkLog("02-12-2014"));
		wlList.add(createWorkLog("01-12-2014"));
		wlList.add(createWorkLog("03-12-2014"));
		return wlList;
	}

	private WorkLog createWorkLog(String date) {
		WorkLog wl = new WorkLog();
		wl.setTimeLogged(Utils.convertStringToDate(date));
		return wl;

	}
}
