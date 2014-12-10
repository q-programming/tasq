package com.qprogramming.tasq.support.sorters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.agile.Sprint;

@RunWith(MockitoJUnitRunner.class)
public class SprintSorterTest {
	private List<Sprint> sprintList;

	@Before
	public void setUp() {
		sprintList = createList();
	}

	@Test
	public void sortSprintsTest() {
		Collections.sort(sprintList, new SprintSorter());
		Assert.assertEquals(new Long(1), sprintList.get(0).getSprintNo());
	}

	private List<Sprint> createList() {
		List<Sprint> sprintList = new LinkedList<Sprint>();
		sprintList.add(createSprint(2L));
		sprintList.add(createSprint(4L));
		sprintList.add(createSprint(3L));
		sprintList.add(createSprint(1L));
		return sprintList;
	}

	private Sprint createSprint(Long number) {
		Sprint sprint = new Sprint();
		sprint.setSprint_no(number);
		return sprint;

	}
}
