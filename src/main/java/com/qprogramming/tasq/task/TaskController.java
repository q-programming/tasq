/**
 * 
 */
package com.qprogramming.tasq.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Account.Role;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.TaskSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Controller
public class TaskController {

	private static final Logger LOG = LoggerFactory
			.getLogger(TaskController.class);

	private static final String CHANGE_TO = " -> ";

	private static final String BR = "<br>";

	private static final String START = "start";

	private static final String STOP = "stop";

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
	public TaskForm startTaskCreate(Model model) {
		model.addAttribute("projects", projectSrv.findAllByUser());
		return new TaskForm();
	}

	@RequestMapping(value = "task/create", method = RequestMethod.POST)
	public String createTask(
			@Valid @ModelAttribute("taskForm") TaskForm taskForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request,
			Model model) {
		if (errors.hasErrors()) {
			model.addAttribute("projects", projectSrv.findAllByUser());
			return null;
		}
		Project project = projectSrv.findByProjectId(taskForm.getProject());
		if (project != null) {
			// check if can edit
			if (!canEdit(project)) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.accesRights", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			Task task = null;
			try {
				task = taskForm.createTask();
			} catch (IllegalArgumentException e) {
				errors.rejectValue("estimate", "error.estimateFormat");
				model.addAttribute("projects", projectSrv.findAllByUser());
				return null;
			}
			// build ID
			int taskCount = project.getTasks().size();
			taskCount++;
			String taskID = project.getProjectId() + "-" + taskCount;
			task.setId(taskID);
			task.setProject(project);
			project.getTasks().add(task);
			// Create log work
			taskSrv.save(task);
			projectSrv.save(project);
			wlSrv.addActivityLog(task, "", LogType.CREATE);
			return "redirect:/task?id=" + taskID;
		}
		return null;
	}

	@RequestMapping(value = "/task/edit", method = RequestMethod.GET)
	public TaskForm startEditTask(@RequestParam("id") String id, Model model) {
		Task task = taskSrv.findById(id);
		model.addAttribute("task", task);
		return new TaskForm(task);
	}

	@Transactional
	@RequestMapping(value = "/task/edit", method = RequestMethod.POST)
	public String editTask(
			@Valid @ModelAttribute("taskForm") TaskForm taskForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request,
			Model model) {
		if (errors.hasErrors()) {
			return null;
		}
		String taskID = taskForm.getId();
		Task task = taskSrv.findById(taskID);
		if (task == null) {
			// something went wrong
			return null;
		}
		// check if can edit
		if (!canEdit(task.getProject())) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("error.accesRights", null,
							Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		StringBuffer message = new StringBuffer();
		if (!task.getName().equalsIgnoreCase(taskForm.getName())) {
			message.append("Name: ");
			message.append(task.getName());
			message.append(CHANGE_TO);
			message.append(taskForm.getName());
			message.append(BR);
			task.setName(taskForm.getName());
		}
		if (!task.getDescription().equalsIgnoreCase(taskForm.getDescription())) {
			message.append("Description: ");
			message.append(task.getDescription());
			message.append(CHANGE_TO);
			message.append(taskForm.getDescription());
			message.append(BR);
			task.setDescription(taskForm.getDescription());
		}
		if (!task.getEstimate().equalsIgnoreCase(taskForm.getEstimate())) {
			message.append("Estimate:");
			message.append(task.getEstimate());
			message.append(CHANGE_TO);
			message.append(taskForm.getEstimate());
			message.append(BR);
			Period estimate = PeriodHelper.inFormat(taskForm.getEstimate());
			task.setEstimate(estimate);
			task.setRemaining(estimate);
		}
		if (!task.getEstimated().equals(
				!Boolean.parseBoolean(taskForm.getNo_estimation()))) {
			message.append("Estimated changed to ");
			message.append(!Boolean.parseBoolean(taskForm.getNo_estimation()));
			message.append(BR);
			task.setEstimated(!Boolean.parseBoolean(taskForm.getNo_estimation()));
		}
		int story_points = taskForm.getStory_points().equals("") ? 0 : Integer
				.parseInt(taskForm.getStory_points());
		if (task.getStory_points() != story_points) {
			message.append("Story points: ");
			message.append(task.getStory_points());
			message.append(CHANGE_TO);
			message.append(story_points);
			task.setStory_points(story_points);
		}
		LOG.debug(message.toString());
		taskSrv.save(task);
		wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);
		return "redirect:/task?id=" + taskID;
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
			return "redirect:/tasks";
		}
		Account account = Utils.getCurrentAccount();
		account = accSrv.findByEmail(account.getEmail());
		List<Task> last_visited = account.getLast_visited_t();
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
		account.setLast_visited_t(clean);
		accSrv.update(account);
		// TODO Add sorting
		// Collections.sort(task.getWorklog(), new WorkLogSorter(true));
		model.addAttribute("task", task);
		return "task/details";
	}

	@RequestMapping(value = "tasks", method = RequestMethod.GET)
	public String listTasks(
			@RequestParam(value = "projectID", required = false) String proj_id,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "query", required = false) String query,
			Model model) {
		List<Project> projects = projectSrv.findAllByUser();
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
						.getActive_project(), true));
		model.addAttribute("projects", projects);

		// Get active or choosen project
		Project active = null;
		if (proj_id == null) {
			active = projectSrv.findUserActiveProject();
		} else {
			active = projectSrv.findByProjectId(proj_id);
		}
		if (active != null) {
			List<Task> taskList = new LinkedList<Task>();
			if (state == null || state == "") {
				taskList = taskSrv.findAllByProject(active);
			} else {
				taskList = taskSrv.findByProjectAndState(active,
						TaskState.valueOf(state));
			}
			if (query != null && query != "") {
				List<Task> searchResult = new LinkedList<Task>();
				for (Task task : taskList) {
					if (task.getName().contains(query)
							|| task.getDescription().contains(query)) {
						searchResult.add(task);
					}
				}
				taskList = searchResult;
			}
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			model.addAttribute("tasks", taskList);
			model.addAttribute("active_project", active);
		}
		return "task/list";
	}

	/**
	 * Logs work . If only digits are sent , it's pressumed that those were
	 * hours
	 * 
	 * @param taskID
	 *            - ID of task for which work is logged
	 * @param logged_work
	 *            - amount of time spent
	 * @param ra
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "logwork", method = RequestMethod.POST)
	public String logWork(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "logged_work") String logged_work,
			@RequestParam(value = "remaining", required = false) String remaining_txt,
			@RequestParam("date_logged") String date_logged,
			@RequestParam("time_logged") String time_logged,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// check if can edit
			if (!canEdit(task.getProject())) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.accesRights", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			try {
				if (logged_work.matches("[0-9]+")) {
					logged_work += "h";
				}
				Period logged = PeriodHelper.inFormat(logged_work);
				StringBuffer message = new StringBuffer(logged_work);
				Period remaining = null;
				if (remaining_txt != null && remaining_txt != "") {
					if (remaining_txt.matches("[0-9]+")) {
						remaining_txt += "h";
					}
					remaining = PeriodHelper.inFormat(remaining_txt);
					message.append(BR);
					message.append("Remaining: ");
					message.append(remaining_txt);
				}
				Date when = new Date();
				if (date_logged != "" && time_logged != "") {
					when = new SimpleDateFormat("dd-M-yyyy HH:mm")
							.parse(date_logged + " " + time_logged);
					message.append(BR);
					message.append("Date: ");
					message.append(date_logged + " " + time_logged);
				}
				wlSrv.addTimedWorkLog(task, message.toString(), when,
						remaining, logged, LogType.LOG);
				// wlSrv.addWorkLog(task, LogType.LOG, logged_work, logged,
				// remaining);
			} catch (IllegalArgumentException e) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.estimateFormat", null,
								Utils.getCurrentLocale()));
				return "redirect:/task?id=" + taskID;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "redirect:/task?id=" + taskID;
	}

	@RequestMapping(value = "/task/state", method = RequestMethod.POST)
	public String changeState(@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "state") TaskState state,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// check if can edit
			if (!canEdit(task.getProject())) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.accesRights", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			TaskState old_state = (TaskState) task.getState();
			task.setState(state);
			taskSrv.save(task);
			wlSrv.addActivityLog(task, old_state.getDescription() + CHANGE_TO
					+ state.getDescription(), LogType.STATUS);
		}
		return "redirect:/task?id=" + taskID;
	}

	@RequestMapping(value = "/task/time", method = RequestMethod.GET)
	public String handleTimer(@RequestParam(value = "id") String taskID,
			@RequestParam(value = "action") String action,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Utils.setHttpRequest(request);
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// check if can edit
			if (!canEdit(task.getProject())) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.accesRights", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			if (action.equals(START)) {
				Account account = Utils.getCurrentAccount();
				if (account.getActive_task() != null
						&& account.getActive_task().length > 0
						&& !account.getActive_task()[0].equals("")) {
					// String task_URL = Utils.getBaseURL() + "task?id="
					// + account.getActive_task()[0];
					MessageHelper.addWarningAttribute(ra, msg.getMessage(
							"task.stopTime.warning",
							new Object[] { account.getActive_task()[0] },
							Utils.getCurrentLocale()));
					return "redirect:" + request.getHeader("Referer");
				}
				account.startTimerOnTask(taskID);
				accSrv.update(account);
				if (task.getState().equals(TaskState.TO_DO)) {
					task.setState(TaskState.ONGOING);
					taskSrv.save(task);
				}
			} else if (action.equals(STOP)) {
				Account account = Utils.getCurrentAccount();
				DateTime now = new DateTime();
				Period log_work = new Period(
						(DateTime) account.getActive_task_time(), now);
				// Only log work if greater than 1 minute
				if (log_work.toStandardDuration().getMillis() / 1000 / 60 < 1) {
					log_work = new Period().plusMinutes(1);
				}
				wlSrv.addNormalWorkLog(task, PeriodHelper.outFormat(log_work),
						log_work, LogType.LOG);
				account.clearActive_task();
				accSrv.update(account);
			} else {

			}
			// taskSrv.save(task);
			// wlSrv.addWorkLog(task, LogType.STATUS, old_state.getDescription()
			// + " -> " + state.getDescription(), null);
		} else {
			return "redirect:" + request.getHeader("Referer");
		}
		return "redirect:/task?id=" + taskID;
	}

	@RequestMapping(value = "/task/assign", method = RequestMethod.POST)
	public String assign(@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "email") String email, RedirectAttributes ra,
			HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			if (task.getState().equals(TaskState.CLOSED)) {
				String localized = msg.getMessage(
						((TaskState) task.getState()).getCode(), null,
						Utils.getCurrentLocale());
				MessageHelper.addWarningAttribute(ra, msg.getMessage(
						"task.closed", new Object[]{ localized },
						Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");

			}
			if (email.equals("") && task.getAssignee() != null) {
				task.setAssignee(null);
				taskSrv.save(task);
				wlSrv.addActivityLog(task, "Unassigned", LogType.ASSIGNED);

			} else {
				Account assignee = accSrv.findByEmail(email);
				if (assignee != null && !assignee.equals(task.getAssignee())) {
					// check if can edit
					if (!canEdit(task.getProject())) {
						MessageHelper.addErrorAttribute(
								ra,
								msg.getMessage("error.accesRights", null,
										Utils.getCurrentLocale()));
						return "redirect:" + request.getHeader("Referer");
					}
					task.setAssignee(assignee);
					taskSrv.save(task);
					wlSrv.addActivityLog(task, assignee.toString(),
							LogType.ASSIGNED);
				}
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	/**
	 * Checks if currently logged in user have privileges to change anything in
	 * project
	 * 
	 * @param task
	 * @return
	 */
	private boolean canEdit(Project project) {
		Project repo_project = projectSrv.findById(project.getId());
		if (repo_project == null) {
			return false;
		}
		Account current_account = Utils.getCurrentAccount();
		return (repo_project.getAdministrators().contains(current_account)
				|| repo_project.getParticipants().contains(current_account) || current_account
				.getRole().equals(Role.ROLE_ADMIN));
	}

}
