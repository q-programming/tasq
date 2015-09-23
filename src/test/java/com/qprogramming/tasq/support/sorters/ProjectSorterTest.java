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
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.task.worklog.WorkLog;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSorterTest {
	private List<Project> projectList;

	@Before
	public void setUp() {
		projectList = createList();
	}

	@Test
	public void sortProjectsByNameTest() {
		Collections.sort(projectList, new ProjectSorter(
				ProjectSorter.SORTBY.NAME, false));
		Assert.assertEquals("Project 1", projectList.get(0).getName());
		Collections.sort(projectList, new ProjectSorter(
				ProjectSorter.SORTBY.NAME, true));
		Assert.assertEquals("Project 5", projectList.get(0).getName());
	}

	@Test
	public void sortProjectsByActiveTest() {
		Collections.sort(projectList, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, new Long(3), true));
		Assert.assertEquals("Project 3", projectList.get(0).getName());
	}

	private List<Project> createList() {
		List<Project> projectList = new LinkedList<Project>();
		projectList.add(createproject(4L));
		projectList.add(createproject(2L));
		projectList.add(createproject(3L));
		projectList.add(createproject(1L));
		projectList.add(createproject(5L));
		return projectList;
	}

	private Project createproject(Long id) {
		Project project = new Project();
		project.setName("Project " + id);
		project.setId(id);
		return project;

	}
}
