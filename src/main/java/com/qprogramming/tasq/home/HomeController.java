package com.qprogramming.tasq.home;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.TaskSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;

@Controller
public class HomeController {

	@Autowired
	TaskService taskSrv;
	
	@Autowired
	ProjectService projSrv;

	int week = 7 * 24 * 60 * 60 * 1000;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal, Model model) {
		if (principal == null) {
			return "homeNotSignedIn";
		} else {
			List<Task> allTasks = taskSrv.findAll();
			List<Project> usersProjects = projSrv.findAllByUser();
			List<Task> dueTasks = new LinkedList<Task>();
			List<Task> currentAccTasks = new LinkedList<Task>();
			List<Task> unassignedTasks = new LinkedList<Task>();
			for (Task task : allTasks) {
				if(usersProjects.contains(task.getProject())){
					if (task.getRawDue_date() != null
							&& (task.getRawDue_date().getTime()
									- System.currentTimeMillis() < week)
							& !task.getState().equals(TaskState.CLOSED)) {
						dueTasks.add(task);
					}
					if (task.getAssignee() == null
							& !task.getState().equals(TaskState.CLOSED)) {
						unassignedTasks.add(task);
					}
					if (Utils.getCurrentAccount().equals(task.getAssignee())) {
						currentAccTasks.add(task);
					}
				}
			}
			Collections.sort(dueTasks, new TaskSorter(
					TaskSorter.SORTBY.DUE_DATE, false));
			Collections.sort(currentAccTasks, new TaskSorter(
					TaskSorter.SORTBY.PRIORITY, true));
			Collections.sort(unassignedTasks, new TaskSorter(
					TaskSorter.SORTBY.PRIORITY, true));

			model.addAttribute("myTasks", currentAccTasks);
			model.addAttribute("unassignedTasks", unassignedTasks);
			model.addAttribute("dueTasks", dueTasks);
			return "homeSignedIn";
		}
	}
}
