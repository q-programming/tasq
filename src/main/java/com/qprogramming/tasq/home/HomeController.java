package com.qprogramming.tasq.home;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.events.EventsService;

@Controller
public class HomeController {

	private TaskService taskSrv;
	private ProjectService projSrv;
	private EventsService eventSrv;

	@Autowired
	public HomeController(TaskService taskSrv, ProjectService projSrv) {
		this.taskSrv = taskSrv;
		this.projSrv = projSrv;
	}

	int week = 7 * 24 * 60 * 60 * 1000;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Account account, Model model) {
		if (account == null) {
			return "homeNotSignedIn";
		} else {
			List<Project> usersProjects = projSrv.findAllByUser();
			if (usersProjects.size() == 0
					&& (account.getRole().equals(Roles.ROLE_VIEWER) || account
							.getRole().equals(Roles.ROLE_REPORTER))) {
				return "homeNewUser";
			}
			List<Task> allTasks = new LinkedList<Task>();
			for (Project project : usersProjects) {
				allTasks.addAll(taskSrv.findAllByProject(project));
			}
			List<Task> dueTasks = new LinkedList<Task>();
			List<Task> currentAccTasks = new LinkedList<Task>();
			List<Task> unassignedTasks = new LinkedList<Task>();
			for (Task task : allTasks) {
				TaskState state = (TaskState) task.getState();
				if (task.getRawDue_date() != null
						&& (task.getRawDue_date().getTime()
								- System.currentTimeMillis() < week)
						& !TaskState.CLOSED.equals(state)) {
					dueTasks.add(task);
				}
				if (task.getAssignee() == null
						& !TaskState.CLOSED.equals(state)) {
					unassignedTasks.add(task);
				}
				if (Utils.getCurrentAccount().equals(task.getAssignee())
						&& !TaskState.CLOSED.equals(state)) {
					currentAccTasks.add(task);
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
	@RequestMapping(value = "/eventCount", method = RequestMethod.GET)
	@ResponseBody int getEventCount(){
		List<Event> events = eventSrv.getUnread(); 
		return events.size();
	}
	
}
