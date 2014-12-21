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
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.agile.SprintRepository;
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
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.link.TaskLinkType;
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

	@Autowired
	private SprintRepository sprintRepository;

	@Autowired
	private TaskLinkService linkService;

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
		Project project = projectSrv.findById(taskForm.getProject());
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
			if(taskForm.getAssignee()!=null){
				Account assignee = accSrv.findById(taskForm.getAssignee());
				task.setAssignee(assignee);	
			}
			// lookup for sprint
			if (taskForm.getAddToSprint() != null) {
				Sprint sprint = sprintRepository.findByProjectIdAndSprintNo(
						project.getId(), taskForm.getAddToSprint()); 
				task.addSprint(sprint);
				//increase scope
				if(sprint.isActive()){
					wlSrv.addActivityLog(task, null, LogType.TASKSPRINTADD);
				}
				//TODO
			}
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
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
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
		StringBuilder message = new StringBuilder();
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
		int storyPoints = ("").equals(taskForm.getStory_points()) ? 0 : Integer
				.parseInt(taskForm.getStory_points());
		if (task.getStory_points() != storyPoints) {
			message.append("Story points: ");
			message.append(task.getStory_points());
			message.append(CHANGE_TO);
			message.append(storyPoints);
			task.setStory_points(storyPoints);
		}
		if (!task.getDue_date().equalsIgnoreCase(taskForm.getDue_date())) {
			message.append("Due date: ");
			message.append(task.getDue_date());
			message.append(CHANGE_TO);
			message.append(taskForm.getDue_date());
			message.append(BR);
			task.setDue_date(Utils.convertStringToDate(taskForm.getDue_date()));
		}
		LOG.debug(message.toString());
		taskSrv.save(task);
		wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);
		return "redirect:/task?id=" + taskID;
	}

	@Transactional
	@RequestMapping(value = "task", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") String id,Model model,
			RedirectAttributes ra) {
		Task task = taskSrv.findById(id);
		if (task == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("task.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/tasks";
		}
		Account account = Utils.getCurrentAccount();
		List<Task> lastVisited = account.getLast_visited_t();
		lastVisited.add(0, task);
		if (lastVisited.size() > 4) {
			lastVisited = lastVisited.subList(0, 4);
		}
		List<Task> clean = new ArrayList<Task>();
		Set<Task> lookup = new HashSet<Task>();
		for (Task item : lastVisited) {
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
		Map<TaskLinkType, List<DisplayTask>> links = linkService
				.findTaskLinks(id);
		model.addAttribute("task", task);
		model.addAttribute("links", links);
		return "task/details";
	}

	@Transactional
	@RequestMapping(value = "tasks", method = RequestMethod.GET)
	public String listTasks(
			@RequestParam(value = "projectID", required = false) String projId,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "priority", required = false) String priority,
			Model model) {
		List<Project> projects = projectSrv.findAllByUser();
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
						.getActive_project(), true));
		model.addAttribute("projects", projects);

		// Get active or choosen project
		Project active = null;
		if (projId == null) {
			active = projectSrv.findUserActiveProject();
		} else {
			active = projectSrv.findByProjectId(projId);
		}
		if (active != null) {
			List<Task> taskList = new LinkedList<Task>();
			if (state == null || state == "") {
				taskList = taskSrv.findAllByProject(active);
			} else {
				if (("OPEN").equals(state)) {
					taskList = taskSrv.findByProjectAndOpen(active);
				} else {
					taskList = taskSrv.findByProjectAndState(active,
							TaskState.valueOf(state));
				}
			}
			if (query != null && query != "") {
				List<Task> searchResult = new LinkedList<Task>();
				for (Task task : taskList) {

					if (StringUtils.containsIgnoreCase(task.getId(), query)
							|| StringUtils.containsIgnoreCase(task.getName(),
									query)
							|| StringUtils.containsIgnoreCase(
									task.getDescription(), query)) {
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
	 * @param loggedWork
	 *            - amount of time spent
	 * @param ra
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "logwork", method = RequestMethod.POST)
	public String logWork(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "logged_work") String loggedWork,
			@RequestParam(value = "remaining", required = false) String remainingTxt,
			@RequestParam("date_logged") String dateLogged,
			@RequestParam("time_logged") String timeLogged,
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
				if (loggedWork.matches("[0-9]+")) {
					loggedWork += "h";
				}
				Period logged = PeriodHelper.inFormat(loggedWork);
				StringBuilder message = new StringBuilder(loggedWork);
				Period remaining = null;
				if (remainingTxt != null && remainingTxt != "") {
					if (remainingTxt.matches("[0-9]+")) {
						remainingTxt += "h";
					}
					remaining = PeriodHelper.inFormat(remainingTxt);
					message.append(BR);
					message.append("Remaining: ");
					message.append(remainingTxt);
				}
				Date when = new Date();
				if (dateLogged != "" && timeLogged != "") {
					when = new SimpleDateFormat("dd-M-yyyy HH:mm")
							.parse(dateLogged + " " + timeLogged);
					message.append(BR);
					message.append("Date: ");
					message.append(dateLogged + " " + timeLogged);
				}
				wlSrv.addTimedWorkLog(task, message.toString(), when,
						remaining, logged, LogType.LOG);
				MessageHelper.addSuccessAttribute(
						ra,
						msg.getMessage("task.logWork.logged", new Object[] {
								loggedWork, task.getId() },
								Utils.getCurrentLocale()));
			} catch (IllegalArgumentException e) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("error.estimateFormat", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				LOG.error(e.getLocalizedMessage());
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/state", method = RequestMethod.POST)
	public String changeState(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "state") TaskState state,
			@RequestParam(value = "zero_checkbox", required = false) Boolean remainingZero,
			@RequestParam(value = "message", required = false) String message,
			RedirectAttributes ra, HttpServletRequest request) {
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
					&& !("0m").equals(task.getLogged_work())) {
				MessageHelper.addWarningAttribute(ra, msg.getMessage(
						"task.alreadyStarted", new Object[] { task.getId() },
						Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}

			TaskState oldState = (TaskState) task.getState();
			task.setState(state);
			// Zero remaining time
			if (remainingZero != null && remainingZero) {
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
			String resultMessage = worklogStateChange(state, oldState, task);
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
			@RequestParam(value = "zero_checkbox", required = false) Boolean remainingZero,
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
				if (!("0m").equals(task.getLogged_work())) {
					return new ResultData(ResultData.ERROR, msg.getMessage(
							"task.alreadyStarted", null,
							Utils.getCurrentLocale()));
				}
			}
			TaskState oldState = (TaskState) task.getState();
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
			if (remainingZero != null && remainingZero) {
				task.setRemaining(PeriodHelper.inFormat("0m"));
			}
			taskSrv.save(task);
			return new ResultData(ResultData.OK, worklogStateChange(state,
					oldState, task));
		}
		return new ResultData(ResultData.ERROR, msg.getMessage("error.unknown",
				null, Utils.getCurrentLocale()));
	}

	@RequestMapping(value = "/task/time", method = RequestMethod.GET)
	public String handleTimer(@RequestParam(value = "id") String taskID,
			@RequestParam(value = "action") String action,
			RedirectAttributes ra, HttpServletRequest request) {
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
						&& !("").equals(account.getActive_task()[0])) {
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
				Period logWork = new Period(
						(DateTime) account.getActive_task_time(), now);
				// Only log work if greater than 1 minute
				if (logWork.toStandardDuration().getMillis() / 1000 / 60 < 1) {
					logWork = new Period().plusMinutes(1);
				}
				wlSrv.addNormalWorkLog(task, PeriodHelper.outFormat(logWork),
						logWork, LogType.LOG);
				account.clearActive_task();
				MessageHelper.addSuccessAttribute(ra, msg.getMessage(
						"task.logWork.logged",
						new Object[] { PeriodHelper.outFormat(logWork),
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
			HttpServletRequest request) {
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
			if (("").equals(email) && task.getAssignee() != null) {
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
			RedirectAttributes ra, HttpServletRequest request) {
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
				StringBuilder message = new StringBuilder();
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
			RedirectAttributes ra, HttpServletRequest request) {
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
				StringBuilder message = new StringBuilder();
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

	@RequestMapping(value = "/getTasks", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplayTask> showTasks(@RequestParam Long projectID,
			@RequestParam String term, HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projectSrv.findById(projectID);
		List<Task> allTasks = taskSrv.findAllByProject(project);
		List<DisplayTask> result = new ArrayList<DisplayTask>();
		for (Task task : allTasks) {
			if (term == null) {
				result.add(new DisplayTask(task));
			} else {
				if (StringUtils.containsIgnoreCase(task.getName(), term)
						|| StringUtils.containsIgnoreCase(task.getId(), term)) {
					result.add(new DisplayTask(task));
				}
			}
		}
		return result;
	}

	private String worklogStateChange(TaskState state, TaskState oldState,
			Task task) {
		if (state.equals(TaskState.CLOSED)) {
			wlSrv.addActivityLog(task, "", LogType.CLOSED);
			return msg.getMessage("task.state.changed.closed",
					new Object[] { task.getId() }, Utils.getCurrentLocale());
		} else if (oldState.equals(TaskState.CLOSED)) {
			wlSrv.addActivityLog(task, "", LogType.REOPEN);
			return msg.getMessage("task.state.changed.reopened",
					new Object[] { task.getId() }, Utils.getCurrentLocale());
		} else {
			wlSrv.addActivityLog(task, oldState.getDescription() + CHANGE_TO
					+ state.getDescription(), LogType.STATUS);
			String localised = msg.getMessage(state.getCode(), null,
					Utils.getCurrentLocale());
			return msg.getMessage("task.state.changed",
					new Object[] { task.getId(), localised },
					Utils.getCurrentLocale());
		}
	}

	private boolean isAdmin(Task task, Project project) {
		Account currentAccount = Utils.getCurrentAccount();
		return project.getAdministrators().contains(currentAccount)
				|| task.getOwner().equals(currentAccount) || Roles.isAdmin();
	}

	/**
	 * Checks if currently logged in user have privileges to change anything in
	 * project
	 * 
	 * @param task
	 * @return
	 */
	private boolean canEdit(Project project) {
		Project repoProject = projectSrv.findById(project.getId());
		if (repoProject == null) {
			return false;
		}
		Account currentAccount = Utils.getCurrentAccount();
		return repoProject.getAdministrators().contains(currentAccount)
				|| repoProject.getParticipants().contains(currentAccount)
				|| Roles.isAdmin();
	}

}
