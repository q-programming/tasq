package com.qprogramming.tasq.support.sorters;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;

@RunWith(MockitoJUnitRunner.class)
public class TasksSorterTest {
	private static final String FIFT = "Fift";
	private static final String FOURTH = "Fourth";
	private static final String SECOND = "Second";
	private static final String THIRD = "Third";
	private static final String FIRST = "First";
	private static final String SIXTH = "Sixth";
	private List<Task> taskList;

	@Before
	public void setUp() {
		taskList = createList();
	}

	@Test
	public void sortTasksByNameTest() {
		Collections.sort(taskList,
				new TaskSorter(TaskSorter.SORTBY.NAME, false));
		Assert.assertEquals(FIFT, taskList.get(0).getName());
		Collections
				.sort(taskList, new TaskSorter(TaskSorter.SORTBY.NAME, true));
		Assert.assertEquals(THIRD, taskList.get(0).getName());
	}

	@Test
	public void sortTasksByIdTest() {
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID, true));
		Assert.assertEquals(FIRST, taskList.get(0).getName());
	}

	@Test
	public void sortTasksByDueDateTest() {
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.DUE_DATE,
				false));
		Assert.assertEquals(THIRD, taskList.get(0).getName());
	}

	@Test
	public void sortTasksByPriorityTest() {
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.PRIORITY,
				true));
		Assert.assertEquals(SECOND, taskList.get(0).getName());
	}
	@Test
	public void sortTasksByOrderTest() {
		List<Task> taskList = new LinkedList<Task>();
		Task task1 = createTask(5, FOURTH, "06-12-2014", TaskPriority.BLOCKER);
		task1.setTaskOrder(4L);
		Task task2 = createTask(1, FIRST, "06-12-2014", TaskPriority.BLOCKER);
		task2.setTaskOrder(0L);
		Task task3 = createTask(4, THIRD, "06-12-2014", TaskPriority.BLOCKER);
		task3.setTaskOrder(3L);
		Task task4 = createTask(2, SECOND, "06-12-2014", TaskPriority.BLOCKER);
		task4.setTaskOrder(1L);
		taskList.add(task1);
		taskList.add(task2);
		taskList.add(task3);
		taskList.add(task4);
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
				true));
		Assert.assertEquals(FIRST, taskList.get(0).getName());
	}

	

	private List<Task> createList() {
		List<Task> taskList = new LinkedList<Task>();
		taskList.add(createTask(2, SECOND, "06-12-2014", TaskPriority.BLOCKER));
		taskList.add(createTask(3, THIRD, "04-11-2014", TaskPriority.MINOR));
		taskList.add(createTask(6, SIXTH, "04-11-2014", TaskPriority.MINOR));
		taskList.add(createTask(1, FIRST, "02-12-2014", TaskPriority.MAJOR));
		taskList.add(createTask(4, FOURTH, "03-12-2014", TaskPriority.CRITICAL));
		taskList.add(createTask(5, FIFT, "01-12-2014", TaskPriority.TRIVIAL));
		return taskList;
	}

	private Task createTask(Integer id, String name, String strDate,
			TaskPriority priority) {
		Date date = Utils.convertStringToDate(strDate);
		Task task = new Task();
		task.setId("TEST-" + id);
		task.setDue_date(date);
		task.setName(name);
		task.setPriority(priority);
		return task;

	}
}
