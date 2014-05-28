/**
 * 
 */
package com.qprogramming.tasq.task;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Controller
public class TaskController {

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private ProjectService projectSrv;

	@Autowired
	private AccountService accSrv;

	@Autowired
	private WorkLogService wlSrv;

	@Autowired
	private MessageSource msg;

	@RequestMapping(value = "task/create", method = RequestMethod.GET)
	public NewTaskForm startTaskCreate(Model model) {
		model.addAttribute("projects", projectSrv.findAllByUser());
		return new NewTaskForm();
	}

	@RequestMapping(value = "task/create", method = RequestMethod.POST)
	public String createTask(
			@Valid @ModelAttribute("newTaskForm") NewTaskForm newTaskForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
		if (errors.hasErrors()) {
			return null;
		}
		Project project = projectSrv.findByName(newTaskForm.getProject());
		if (project != null) {
			Task task = newTaskForm.createTask();
			// build ID
			Long taskCount = project.getTask_count();
			taskCount++;
			String taskID = project.getProjectId() + "-" + taskCount;
			task.setId(taskID);
			task.setProject(project);
			// Create log work
			taskSrv.save(task);
			project.setTask_count(taskCount);
			projectSrv.save(project);
			wlSrv.addWorkLog(task, LogType.CREATE);
			return "redirect:/task?id=" + taskID;
		}
		return null;
	}

	@RequestMapping(value = "task", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") String id,
			Model model, RedirectAttributes ra, HttpServletRequest request) {
		Task task = taskSrv.findById(id);
		if (task == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("task.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		Account account = Utils.getCurrentAccount();
		account = accSrv.findByEmail(account.getEmail());

		List<Task> last_visited = account.getLast_visited();
		last_visited.add(0, task);
		if (last_visited.size() > 4) {
			last_visited = last_visited.subList(0, 4);
		}
		account.setLast_visited(last_visited);
		accSrv.update(account);
		model.addAttribute("task", task);
		return "task/details";
	}

	@RequestMapping(value = "tasks", method = RequestMethod.GET)
	public String listTasks(
			@RequestParam(value = "project", required = false) Long proj_id,
			Model model) {
		List<Project> projects = projectSrv.findAllByUser();
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, true));
		model.addAttribute("projects", projects);
		// Get active or choosen project
		Project active = null;
		if (proj_id == null) {
			active = projectSrv.findUserActiveProject();
		} else {
			active = projectSrv.findById(proj_id);
		}
		if (active != null) {
			List<Task> taskList = taskSrv.findAllByProject(active);
			model.addAttribute("tasks", taskList);
		}

		return "task/list";
	}

}
