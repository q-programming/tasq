/**
 * 
 */
package com.qprogramming.tasq.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.hibernate.Hibernate;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.account.Account.Role;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.comments.CommentsRepository;
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

	private static final String COLS = "ABCDEFGHIJKLMNOPRSTUVWXYZ";
	private static final String CHANGE_TO = " -> ";
	private static final String BR = "<br>";
	private static final String START = "start";
	private static final String STOP = "stop";
	private static final Object XLS = "xls";
	private static final Object XLM = "xml";
	private static final int NAME_CELL = 0;
	private static final int DESCRIPTION_CELL = 1;
	private static final int TYPE_CELL = 2;
	private static final int PRIORITY_CELL = 3;
	private static final int ESTIMATE_CELL = 4;
	private static final int SP_CELL = 5;
	private static final int DUE_DATE_CELL = 6;
	private static final Object NEW_LINE = "\n";
	private static final Object ROW_SKIPPED = "Row was skipped</br>";

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

	@Autowired
	private CommentsRepository commRepo;

	@RequestMapping(value = "task/create", method = RequestMethod.GET)
	public TaskForm startTaskCreate(Model model) {
		if (!Roles.isReporter()) {
			throw new TasqAuthException(msg);
		}
		Project project = projectSrv.findUserActiveProject();
		if (project == null) {
			throw new TasqAuthException(msg, "error.noProjects");
		}
		model.addAttribute("project", projectSrv.findUserActiveProject());
		model.addAttribute("projects_list", projectSrv.findAllByUser());
		return new TaskForm();
	}

	@RequestMapping(value = "task/create", method = RequestMethod.POST)
	public String createTask(
			@Valid @ModelAttribute("taskForm") TaskForm taskForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request,
			Model model) {
		if (!Roles.isReporter()) {
			throw new TasqAuthException(msg);
		}
		if (errors.hasErrors()) {
			model.addAttribute("projects_list", projectSrv.findAllByUser());
			model.addAttribute("project", projectSrv.findUserActiveProject());
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
			// assigne
			Account assignee = accSrv.findById(project.getDefaultAssigneeID());
			task.setAssignee(assignee);
			// Create log work
			taskSrv.save(task);
			projectSrv.save(project);
			wlSrv.addActivityLog(task, "", LogType.CREATE);
			return "redirect:/task?id=" + taskID;
		}
		return null;
	}

	@Transactional
	@RequestMapping(value = "/task/edit", method = RequestMethod.GET)
	public TaskForm startEditTask(@RequestParam("id") String id, Model model) {
		Task task = taskSrv.findById(id);
		if (!Roles.isReporter()
				|| !task.getOwner().equals(Utils.getCurrentAccount())) {
			throw new TasqAuthException(msg);
		}
		Hibernate.initialize(task.getRawWorkLog());
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
		if (!canEdit(task.getProject())
				&& (!Roles.isReporter() || !task.getOwner().equals(
						Utils.getCurrentAccount()))) {
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
		if ((taskForm.getEstimate() != null)
				&& (!task.getEstimate()
						.equalsIgnoreCase(taskForm.getEstimate()))) {
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
		if (!task.getDue_date().equalsIgnoreCase(taskForm.getDue_date())) {
			message.append("Due date: ");
			message.append(task.getDue_date());
			message.append(CHANGE_TO);
			message.append(taskForm.getDue_date());
			message.append(BR);
			task.setDue_date(Utils.convertDueDate(taskForm.getDue_date()));
		}
		LOG.debug(message.toString());
		taskSrv.save(task);
		wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);
		return "redirect:/task?id=" + taskID;
	}

	@Transactional
	@RequestMapping(value = "task", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") String id,
			@RequestParam(value = "tab", required = false) String tab,
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
		// TASK
		Hibernate.initialize(task.getComments());
		Hibernate.initialize(task.getWorklog());
		Hibernate.initialize(task.getSprints());
		task.setDescription(task.getDescription().replaceAll("\n", "<br>"));
		model.addAttribute("task", task);
		return "task/details";
	}

	@Transactional
	@RequestMapping(value = "tasks", method = RequestMethod.GET)
	public String listTasks(
			@RequestParam(value = "projectID", required = false) String proj_id,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "priority", required = false) String priority,
			Model model, HttpServletRequest request) {
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
			if (priority != null && priority != "") {
				List<Task> searchResult = new LinkedList<Task>();
				for (Task task : taskList) {
					if (task.getPriority() != null
							&& task.getPriority().equals(
									TaskPriority.valueOf(priority))) {
						searchResult.add(task);
					}
				}
				taskList = searchResult;
			}
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			Utils.initializeWorkLogs(taskList);
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
			if (!canEdit(task.getProject()) && !Roles.isUser()) {
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
				MessageHelper.addSuccessAttribute(
						ra,
						msg.getMessage("task.logWork.logged", new Object[] {
								logged_work, task.getId() },
								Utils.getCurrentLocale()));
			} catch (IllegalArgumentException e) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.estimateFormat", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/state", method = RequestMethod.POST)
	public String changeState(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "state") TaskState state,
			@RequestParam(value = "zero_checkbox", required = false) Boolean remaining_zero,
			@RequestParam(value = "message", required = false) String message,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			if (state.equals(task.getState())) {
				return "redirect:" + request.getHeader("Referer");
			}
			// check if can edit
			if (!canEdit(task.getProject()) && !Roles.isUser()) {
				throw new TasqAuthException(msg);
			}
			// TODO eliminate this?
			if (state.equals(TaskState.TO_DO)
					&& !task.getLogged_work().equals("0m")) {
				MessageHelper.addWarningAttribute(ra, msg.getMessage(
						"task.alreadyStarted", new Object[] { task.getId() },
						Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}

			TaskState old_state = (TaskState) task.getState();
			task.setState(state);
			// Zero remaining time
			if (remaining_zero != null && remaining_zero) {
				task.setRemaining(PeriodHelper.inFormat("0m"));
			}
			// add comment for task change state?
			if (message != null && message != "") {
				if (Utils.containsHTMLTags(message)) {
					MessageHelper.addErrorAttribute(
							ra,
							msg.getMessage("comment.htmlTag", null,
									Utils.getCurrentLocale()));
					return "redirect:" + request.getHeader("Referer");
				} else {
					Comment comment = new Comment();
					comment.setTask(task);
					comment.setAuthor(Utils.getCurrentAccount());
					comment.setDate(new Date());
					comment.setMessage(message);
					commRepo.save(comment);
					Hibernate.initialize(task.getComments());
					task.addComment(comment);
					wlSrv.addActivityLog(task, message, LogType.COMMENT);
				}
			}
			// Save all
			taskSrv.save(task);
			String resultMessage = worklogStateChange(state, old_state, task);
			MessageHelper.addSuccessAttribute(ra, resultMessage);
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/changeState", method = RequestMethod.POST)
	@ResponseBody
	public ResultData changeStatePOST(
			@RequestParam(value = "id") String taskID,
			@RequestParam(value = "state") TaskState state,
			@RequestParam(value = "zero_checkbox", required = false) Boolean remaining_zero,
			@RequestParam(value = "message", required = false) String message) {
		// check if not admin or user
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// check if can edit
			if (!canEdit(task.getProject()) && !Roles.isUser()) {
				throw new TasqAuthException(msg, "role.error.task.permission");
			}
			if (state.equals(TaskState.TO_DO)) {
				Hibernate.initialize(task.getLogged_work());
				if (!task.getLogged_work().equals("0m")) {
					return new ResultData(ResultData.ERROR, msg.getMessage(
							"task.alreadyStarted", null,
							Utils.getCurrentLocale()));
				}
			}
			TaskState old_state = (TaskState) task.getState();
			task.setState(state);
			if (message != null && message != "") {
				if (Utils.containsHTMLTags(message)) {
					return new ResultData(ResultData.ERROR, msg.getMessage(
							"comment.htmlTag", null, Utils.getCurrentLocale()));
				} else {
					Comment comment = new Comment();
					comment.setTask(task);
					comment.setAuthor(Utils.getCurrentAccount());
					comment.setDate(new Date());
					comment.setMessage(message);
					commRepo.save(comment);
					Hibernate.initialize(task.getComments());
					task.addComment(comment);
					wlSrv.addActivityLog(task, message, LogType.COMMENT);
				}
			}

			// Zero remaining time
			if (remaining_zero != null && remaining_zero) {
				task.setRemaining(PeriodHelper.inFormat("0m"));
			}
			taskSrv.save(task);
			return new ResultData(ResultData.OK, worklogStateChange(state,
					old_state, task));
		}
		return new ResultData(ResultData.ERROR, msg.getMessage("error.unknown",
				null, Utils.getCurrentLocale()));
	}

	@RequestMapping(value = "/task/time", method = RequestMethod.GET)
	public String handleTimer(@RequestParam(value = "id") String taskID,
			@RequestParam(value = "action") String action,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Utils.setHttpRequest(request);
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			// check if can edit
			if (!canEdit(task.getProject()) && !Roles.isUser()) {
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
				MessageHelper.addSuccessAttribute(ra, msg.getMessage(
						"task.logWork.logged",
						new Object[] { PeriodHelper.outFormat(log_work),
								task.getId() }, Utils.getCurrentLocale()));
				accSrv.update(account);
			}
		} else {
			return "redirect:" + request.getHeader("Referer");
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/assign", method = RequestMethod.POST)
	public String assign(@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "email") String email, RedirectAttributes ra,
			HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			if (!Roles.isUser()) {
				throw new TasqAuthException(msg);
			}
			if (task.getState().equals(TaskState.CLOSED)) {
				String localized = msg.getMessage(
						((TaskState) task.getState()).getCode(), null,
						Utils.getCurrentLocale());
				MessageHelper.addWarningAttribute(ra, msg.getMessage(
						"task.closed", new Object[] { localized },
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
					MessageHelper.addSuccessAttribute(
							ra,
							msg.getMessage(
									"task.assigned",
									new Object[] { task.getId(),
											assignee.toString() },
									Utils.getCurrentLocale()));
				}
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/assignMe", method = RequestMethod.POST)
	@ResponseBody
	public ResultData assignMe(@RequestParam(value = "id") String id) {
		// check if not admin or user
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Task task = taskSrv.findById(id);
		if (!canEdit(task.getProject())) {
			return new ResultData(ResultData.ERROR, msg.getMessage(
					"role.error.task.permission", null,
					Utils.getCurrentLocale()));
		}
		Account assignee = Utils.getCurrentAccount();
		task.setAssignee(assignee);
		taskSrv.save(task);
		wlSrv.addActivityLog(task, assignee.toString(), LogType.ASSIGNED);
		return new ResultData(ResultData.OK, msg.getMessage("task.assinged.me",
				null, Utils.getCurrentLocale()) + " " + task.getId());
	}

	@RequestMapping(value = "/task/priority", method = RequestMethod.GET)
	public String changePriority(@RequestParam(value = "id") String taskID,
			@RequestParam(value = "priority") String priority,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			if (task.getState().equals(TaskState.CLOSED)) {
				String localized = msg.getMessage(
						((TaskState) task.getState()).getCode(), null,
						Utils.getCurrentLocale());
				MessageHelper.addWarningAttribute(ra, msg.getMessage(
						"task.closed", new Object[] { localized },
						Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");

			}
			if (canEdit(task.getProject()) && Roles.isUser()) {
				StringBuffer message = new StringBuffer();
				String oldPriority = "";
				// TODO temporary due to old DB
				if (task.getPriority() != null) {
					oldPriority = task.getPriority().toString();
				}
				message.append(oldPriority);
				message.append(CHANGE_TO);
				task.setPriority(TaskPriority.valueOf(priority));
				message.append(task.getPriority().toString());
				taskSrv.save(task);
				wlSrv.addActivityLog(task, message.toString(), LogType.PRIORITY);

			}

		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/delete", method = RequestMethod.GET)
	public String deleteTask(@RequestParam(value = "id") String taskID,
			RedirectAttributes ra, HttpServletRequest request, Model model) {
		Task task = taskSrv.findById(taskID);
		if (task != null) {
			Project project = projectSrv.findById(task.getProject().getId());
			// Only allow delete for administrators, owner or app admin
			if (isAdmin(task, project)) {
				task.setOwner(null);
				task.setAssignee(null);
				task.setProject(null);
				// clear last and potential actives
				List<Account> accounts = accSrv.findAll();
				for (Account account : accounts) {
					boolean update = false;
					if (account.getActive_task() != null
							&& account.getActive_task().length > 0
							&& account.getActive_task()[0].equals(taskID)) {
						if (account.equals(Utils.getCurrentAccount())) {
							account.clearActive_task();
							update = true;
						} else {
							MessageHelper.addErrorAttribute(ra, msg.getMessage(
									"task.delete.work",
									new Object[] { account },
									Utils.getCurrentLocale()));
							return "redirect:" + request.getHeader("Referer");
						}
					}
					List<Task> lastVisited = account.getLast_visited_t();
					if (lastVisited.contains(task)) {
						lastVisited.remove(task);
						account.setLast_visited_t(lastVisited);
						update = true;
					}
					if (update) {
						accSrv.update(account);
					}
				}
				// leave message and clear all
				StringBuffer message = new StringBuffer();
				message.append("[");
				message.append(task.getId());
				message.append("]");
				message.append(" - ");
				message.append(task.getName());
				taskSrv.delete(task);
				wlSrv.addWorkLogNoTask(message.toString(), project,
						LogType.DELETED);
			}
			return "redirect:/project?id=" + project.getId();
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/getTemplateFile", method = RequestMethod.GET)
	public @ResponseBody
	String downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		URL fileURL = getClass().getResource("/template.xls");
		File file;
		try {
			file = new File(fileURL.toURI());
			if (file != null) {
				response.setHeader("content-Disposition",
						"attachment; filename=" + file.getName());
				InputStream is = new FileInputStream(file);
				IOUtils.copyLarge(is, response.getOutputStream());
			}
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/import", method = RequestMethod.GET)
	public String startImportTasks(Model model) {
		model.addAttribute("projects", projectSrv.findAll());
		return "/task/import";
	}

	@Transactional
	@RequestMapping(value = "/task/import", method = RequestMethod.POST)
	public String importTasks(
			@RequestParam(value = "file") MultipartFile importFile,
			@RequestParam(value = "project") String projectName,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		if (importFile.getSize() != 0) {
			try {
				String extension = FilenameUtils.getExtension(importFile
						.getOriginalFilename());
				Project project = projectSrv.findByProjectId(projectName);
				int taskCount = project.getTasks().size();
				if (extension.equals(XLS)) {
					HSSFWorkbook workbook = new HSSFWorkbook(
							importFile.getInputStream());
					HSSFSheet sheet = workbook.getSheetAt(0);
					StringBuffer logger = new StringBuffer();
					for (Iterator<Row> rowIterator = sheet.iterator(); rowIterator
							.hasNext();) {
						Row row = rowIterator.next();
						if (row.getRowNum() == 0) {
							continue;
						}
						StringBuffer log_row = verifyRow(row);
						// If there was at least one error with row , add it to
						// logger and move to next row
						if (log_row.length() > 0) {
							logger.append(log_row);
							continue;
						}
						// validation finished
						TaskForm taskForm = new TaskForm();
						taskForm.setName(row.getCell(NAME_CELL)
								.getStringCellValue());
						taskForm.setDescription(row.getCell(DESCRIPTION_CELL)
								.getStringCellValue());
						taskForm.setType(row.getCell(TYPE_CELL)
								.getStringCellValue());
						taskForm.setPriority(row.getCell(PRIORITY_CELL)
								.getStringCellValue());
						if (row.getCell(ESTIMATE_CELL) != null) {
							taskForm.setEstimate(row.getCell(ESTIMATE_CELL)
									.getStringCellValue());
						}
						Task task = taskForm.createTask();
						// optional fields
						if (row.getCell(SP_CELL) != null) {
							task.setStory_points(((Double) row.getCell(SP_CELL)
									.getNumericCellValue()).intValue());
						}
						if (row.getCell(DUE_DATE_CELL) != null) {
							Date date = row.getCell(DUE_DATE_CELL)
									.getDateCellValue();
							task.setDue_date(date);
						}
						// Create ID
						taskCount++;
						String taskID = project.getProjectId() + "-"
								+ taskCount;
						task.setId(taskID);
						task.setProject(project);
						project.getTasks().add(task);
						task = taskSrv.save(task);
						projectSrv.save(project);
						wlSrv.addActivityLog(task, "", LogType.CREATE);
						String log_header = "[Row " + row.getRowNum() + "]";
						logger.append(log_header);
						logger.append("Task ");
						logger.append(task);
						logger.append(" succesfully created");
						logger.append(BR);
					}
					model.addAttribute("logger", logger.toString().trim());
				} else if (extension.equals(XLM)) {
					// TODO
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LOG.error(e.getLocalizedMessage());
			}
		}

		return "/task/importResults";
	}

	private String worklogStateChange(TaskState state, TaskState old_state,
			Task task) {
		if (state.equals(TaskState.CLOSED)) {
			wlSrv.addActivityLog(task, "", LogType.CLOSED);
			return msg.getMessage("task.state.changed.closed",
					new Object[] { task.getId() }, Utils.getCurrentLocale());
		} else if (old_state.equals(TaskState.CLOSED)) {
			wlSrv.addActivityLog(task, "", LogType.REOPEN);
			return msg.getMessage("task.state.changed.reopened",
					new Object[] { task.getId() }, Utils.getCurrentLocale());
		} else {
			wlSrv.addActivityLog(task, old_state.getDescription() + CHANGE_TO
					+ state.getDescription(), LogType.STATUS);
			String localised = msg.getMessage(state.getCode(), null,
					Utils.getCurrentLocale());
			return msg.getMessage("task.state.changed",
					new Object[] { task.getId(), localised },
					Utils.getCurrentLocale());
		}
	}

	private StringBuffer verifyRow(Row row) {
		StringBuffer logger = new StringBuffer();
		String log_header = "[Row " + row.getRowNum() + "]";
		for (int i = 0; i < 7; i++) {
			Cell cell = row.getCell(i);
			if ((cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK)
					&& (i != ESTIMATE_CELL & i != SP_CELL & i != DUE_DATE_CELL)) {
				logger.append(log_header);
				logger.append("Cell ");
				logger.append(COLS.charAt(i));
				logger.append(row.getRowNum());
				logger.append(" can't be empty");
				logger.append(BR);
			}
		}
		if (!isNumericCellValid(row, SP_CELL)) {
			logger.append(log_header);
			logger.append("Story points must be blank or numeric in cell ");
			logger.append(COLS.charAt(SP_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskTypeValid(row)) {
			logger.append(log_header);
			logger.append("Wrong Task Priority in cell ");
			logger.append(COLS.charAt(TYPE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isTaskPriorityValid(row)) {
			logger.append(log_header);
			logger.append("Wrong Task Priority in cell ");
			logger.append(COLS.charAt(PRIORITY_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (!isDATECellValid(row, DUE_DATE_CELL)) {
			logger.append(log_header);
			logger.append("Due date must be blank or date formated in cell ");
			logger.append(COLS.charAt(DUE_DATE_CELL));
			logger.append(row.getRowNum());
			logger.append(BR);
		}
		if (logger.length() > 0) {
			logger.append(log_header);
			logger.append(ROW_SKIPPED);
		}
		return logger;
	}

	private boolean isAdmin(Task task, Project project) {
		Account current_account = Utils.getCurrentAccount();
		return project.getAdministrators().contains(current_account)
				|| task.getOwner().equals(current_account) || Roles.isAdmin();
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
				|| repo_project.getParticipants().contains(current_account) || Roles
					.isAdmin());
	}

	private boolean isNumericCellValid(Row row, int cell) {
		return row.getCell(cell) != null
				&& row.getCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC;
	}

	private boolean isDATECellValid(Row row, int cell) {
		try {
			if (row.getCell(cell) != null
					&& row.getCell(cell).getCellType() != Cell.CELL_TYPE_BLANK) {
				if (!HSSFDateUtil.isCellDateFormatted(row.getCell(cell))) {
					return false;
				}
			}
		} catch (java.lang.IllegalStateException e) {
			return false;
		}
		return true;
	}

	private boolean isTaskTypeValid(Row row) {
		Cell cell = row.getCell(TYPE_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskType.toType(row.getCell(TYPE_CELL).getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return true;
	}

	private boolean isTaskPriorityValid(Row row) {
		Cell cell = row.getCell(PRIORITY_CELL);
		if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			try {
				TaskPriority.toPriority(cell.getStringCellValue());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return true;
	}
}
