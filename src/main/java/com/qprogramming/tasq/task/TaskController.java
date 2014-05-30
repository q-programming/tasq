/**
 * 
 */
package com.qprogramming.tasq.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.joda.time.Period;
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
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.TaskSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.WorkLogSorter;
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
		Project project = projectSrv.findByProjectId(newTaskForm.getProject());
		if (project != null) {
			Task task = null;
			try {
				task = newTaskForm.createTask();
			} catch (IllegalArgumentException e) {
				errors.rejectValue("estimate", "error.estimateFormat");
				return null;
			}
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
			wlSrv.addWorkLog(task, LogType.CREATE, "");
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
		List<Task> clean = new ArrayList<Task>();
		HashSet<Task> lookup = new HashSet<Task>();
		for (Task item : last_visited) {
			if (lookup.add(item)) {
				clean.add(item);
			}
		}
		account.setLast_visited(clean);
		accSrv.update(account);
		Collections.sort(task.getWorklog(),new WorkLogSorter(true));
		model.addAttribute("task", task);
		return "task/details";
	}

	@RequestMapping(value = "tasks", method = RequestMethod.GET)
	public String listTasks(
			@RequestParam(value = "projectID", required = false) String proj_id,
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
			active = projectSrv.findByProjectId(proj_id);
		}
		if (active != null) {
			List<Task> taskList = taskSrv.findAllByProject(active);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			model.addAttribute("tasks", taskList);
			model.addAttribute("active_project", active);
		}
		return "task/list";
	}

	@RequestMapping(value = "logwork", method = RequestMethod.POST)
	public String logWork(@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "logged_work") String logged_work,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// TODO add LOGFORM to better handle errors
			Period logged = PeriodHelper.inFormat(logged_work);
			Period task_work_log = task.getRawLogged_work();
			task_work_log = PeriodHelper.plusPeriods(task_work_log, logged);
			// TODO if logged is greater than esstimate?
			task.setLogged_work(task_work_log);
			if (task.getState().equals(TaskState.TO_DO)) {
				task.setState(TaskState.ONGOING);
			}
			taskSrv.save(task);
			// TODO add worklog
			wlSrv.addWorkLog(task, LogType.LOG, logged_work);
		}

		return "redirect:/task?id=" + taskID;
	}

	@RequestMapping(value = "/task/state", method = RequestMethod.POST)
	public String changeState(@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "state") TaskState state,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			TaskState old_state = (TaskState) task.getState();
			task.setState(state);
			taskSrv.save(task);
			wlSrv.addWorkLog(task, LogType.STATUS, old_state.getDescription()
					+ " -> " + state.getDescription());
		}
		return "redirect:/task?id=" + taskID;
	}

}
