package com.qprogramming.tasq.home;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.DisplayProject;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;

@Secured("ROLE_USER")
@ControllerAdvice
public class HomeControllerAdvice {
	private AccountService accSrv;
	private EventsService eventSrv;

	@Autowired
	public HomeControllerAdvice(AccountService accSrv, EventsService eventSrv) {
		this.eventSrv = eventSrv;
		this.accSrv = accSrv;

	}

	@ModelAttribute("last_projects")
	public List<DisplayProject> getLastProjects() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// Get lasts 5 projects
			Account currentAccount = Utils.getCurrentAccount();
			currentAccount = accSrv.findByUsername(currentAccount.getUsername());
			List<Project> projects = currentAccount.getLast_visited_p();
			Collections.sort(projects, new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
					Utils.getCurrentAccount().getActive_project(), true));
			List<DisplayProject> result = new LinkedList<DisplayProject>();
			for (Project project : projects) {
				result.add(new DisplayProject(project));
			}
			return result;
		}
		return null;
	}

	@ModelAttribute("last_tasks")
	public List<DisplayTask> getLastTasks() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// Get lasts 5 Tasks
			Account current_account = Utils.getCurrentAccount();
			current_account = accSrv.findByUsername(current_account.getUsername());
			List<Task> tasks = current_account.getLast_visited_t();
			List<DisplayTask> result = new LinkedList<DisplayTask>();
			for (Task task : tasks) {
				result.add(new DisplayTask(task));
			}
			return result;
		}
		return null;
	}

	@ModelAttribute("eventCount")
	public int getEvents() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			List<Event> events = eventSrv.getUnread();
			return events.size();
		}
		return 0;
	}
}
