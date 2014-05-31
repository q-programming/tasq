package com.qprogramming.tasq.home;

import java.util.Collections;
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
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.TaskSorter;
import com.qprogramming.tasq.support.Utils;
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
			List<Project> projects = projSrv.findAllByUser();
			Collections.sort(projects, new ProjectSorter(
					ProjectSorter.SORTBY.LAST_VISIT, true));
			if (projects.size() > 5) {
				return projects.subList(0, 5);
			} else {
				return projects;
			}
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
			List<Task> tasks = current_account.getLast_visited();
			return tasks;
		}
		return null;
	}
}