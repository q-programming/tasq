package com.qprogramming.tasq.home;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;

@Controller
public class HomeController {

	private TaskService taskSrv;
	private ProjectService projSrv;
	private EventsService eventSrv;
	private AppService appSrv;

	@Value("${skip.landing.page}")
	private String skipLandingPage;

	@Value("1.1.0")
	private String version;

	@Autowired
	public HomeController(TaskService taskSrv, ProjectService projSrv, AppService appSrv) {
		this.taskSrv = taskSrv;
		this.projSrv = projSrv;
		this.appSrv = appSrv;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Account account, Model model) {
		if (account == null) {
			if (Boolean.parseBoolean(skipLandingPage)) {
				return "signin";
			} else {
				return "homeNotSignedIn";
			}
		} else {
			List<Project> usersProjects = projSrv.findAllByUser(account.getId());
			if (usersProjects.size() == 0
					&& (account.getRole().equals(Roles.ROLE_VIEWER) || account.getRole().equals(Roles.ROLE_USER))) {
				return "homeNewUser";
			}
			List<Task> allTasks = new LinkedList<Task>();
			for (Project project : usersProjects) {
				allTasks.addAll(taskSrv.findAllByProject(project));
			}
			List<Task> currentAccTasks = new LinkedList<Task>();
			List<Task> unassignedTasks = new LinkedList<Task>();
			for (Task task : allTasks) {
				TaskState state = (TaskState) task.getState();
				if (task.getAssignee() == null & !TaskState.CLOSED.equals(state)) {
					unassignedTasks.add(task);
				}
				if (Utils.getCurrentAccount().equals(task.getAssignee()) && !TaskState.CLOSED.equals(state)) {
					currentAccTasks.add(task);
				}
			}
			Collections.sort(currentAccTasks, new TaskSorter(TaskSorter.SORTBY.PRIORITY, true));
			Collections.sort(unassignedTasks, new TaskSorter(TaskSorter.SORTBY.PRIORITY, true));
			model.addAttribute("myTasks", currentAccTasks);
			model.addAttribute("unassignedTasks", unassignedTasks);
			return "homeSignedIn";
		}
	}

	@RequestMapping(value = "/eventCount", method = RequestMethod.GET)
	@ResponseBody
	int getEventCount() {
		List<Event> events = eventSrv.getUnread();
		return events.size();
	}

	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String help(Model model, HttpServletRequest request) {
		// Utils.setHttpRequest(request);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String lang = "en";
		// if (!(authentication instanceof AnonymousAuthenticationToken)) {
		// lang = Utils.getCurrentAccount().getLanguage();
		// if (lang == null) {
		// }
		// }
		model.addAttribute("version", version);
		model.addAttribute("projHome", appSrv.getProperty(AppService.TASQROOTDIR));
		return "help/" + lang;
	}

}
