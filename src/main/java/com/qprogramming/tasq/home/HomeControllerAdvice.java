package com.qprogramming.tasq.home;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

@Secured("ROLE_USER")
@ControllerAdvice
public class HomeControllerAdvice {
	private static final Logger LOG = LoggerFactory
			.getLogger(HomeControllerAdvice.class);

	@Autowired
	ProjectService projSrv;
	@Autowired
	TaskService taskSrv;
	@Autowired
	AccountService accSrv;

	@ModelAttribute("last_projects")
	public List<Project> getLastProjects(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// Get lasts 5 projects
			Account current_account = Utils.getCurrentAccount();
			current_account = accSrv.findByEmail(current_account.getEmail());
			List<Project> projects = current_account.getLast_visited_p();
			Collections.sort(projects, new ProjectSorter(
					ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
							.getActive_project(), true));
			return projects;
		}
		return null;
	}

	@ModelAttribute("last_tasks")
	public List<Task> getLastTasks(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			// Get lasts 5 Tasks
			Account current_account = Utils.getCurrentAccount();
			current_account = accSrv.findByEmail(current_account.getEmail());
			List<Task> tasks = current_account.getLast_visited_t();
			return tasks;
		}
		return null;
	}
}
