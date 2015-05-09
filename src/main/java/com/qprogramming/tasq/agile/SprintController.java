package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.SprintSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class SprintController {

	private static final Logger LOG = LoggerFactory
			.getLogger(SprintController.class);
	private static final String SPACE = " ";
	private static final String NEW_LINE = "\n";

	private ProjectService projSrv;
	private TaskService taskSrv;
	private AgileService agileSrv;
	private WorkLogService wrkLogSrv;
	private MessageSource msg;

	@Autowired
	public SprintController(ProjectService prjSrv, TaskService taskSrv,
			AgileService agileSrv, WorkLogService wrkLogSrv, MessageSource msg) {
		this.projSrv = prjSrv;
		this.taskSrv = taskSrv;
		this.agileSrv = agileSrv;
		this.wrkLogSrv = wrkLogSrv;
		this.msg = msg;
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "{id}/scrum/board", method = RequestMethod.GET)
	public String showBoard(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canEdit(project)) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			Sprint sprint = agileSrv.findByProjectIdAndActiveTrue(project
					.getId());
			if (sprint == null) {
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.sprint.noActive", null,
								Utils.getCurrentLocale()));
				return "redirect:/" + project.getProjectId() + "/scrum/backlog";
			}
			List<Task> taskList = taskSrv.findAllBySprint(sprint);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
					true));
			Set<Tag> tags = new HashSet<Tag>();
			for (Task task : taskList) {
				Hibernate.initialize(task.getTags());
				tags.addAll(task.getTags());
			}
			List<DisplayTask> resultList = taskSrv.convertToDisplay(taskList,
					true);
			model.addAttribute("tags", tags);
			model.addAttribute("sprint", sprint);
			model.addAttribute("tasks", resultList);
			return "/scrum/board";
		}
		return "";
	}

	@Transactional
	@RequestMapping(value = "/{id}/scrum/backlog", method = RequestMethod.GET)
	public String showBacklog(@PathVariable String id, Model model,
			HttpServletRequest request) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canEdit(project)) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			// Don't show closed tasks in backlog view
			List<Task> taskList = taskSrv.findAllByProject(project);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
					true));
			List<DisplayTask> resultList = DisplayTask
					.convertToDisplayTasks(taskList);
			Map<Sprint, List<DisplayTask>> sprint_result = new LinkedHashMap<Sprint, List<DisplayTask>>();
			List<Sprint> sprintList = agileSrv.findByProjectIdAndFinished(
					project.getId(), false);
			Collections.sort(sprintList, new SprintSorter());
			// Assign tasks to sprints in order to display them
			for (Sprint sprint : sprintList) {
				List<DisplayTask> sprint_tasks = new LinkedList<DisplayTask>();
				for (Task task : taskList) {
					Hibernate.initialize(task.getSprints());
					if (task.getSprints().contains(sprint)) {
						sprint_tasks.add(new DisplayTask(task));
					}
				}
				sprint_result.put(sprint, sprint_tasks);
			}
			model.addAttribute("sprint_result", sprint_result);
			model.addAttribute("tasks", resultList);
			model.addAttribute("sprints", sprintList);
		}
		return "/scrum/backlog";
	}

	@RequestMapping(value = "/{id}/scrum/create", method = RequestMethod.POST)
	public String createSprint(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (!projSrv.canAdminister(project)) {
			throw new TasqAuthException(msg);
		}
		List<Sprint> sprints = agileSrv.findByProjectId(project.getId());
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setSprint_no((long) sprints.size() + 1);
		agileSrv.save(sprint);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.createdSprint", null,
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/sprintAssign", method = RequestMethod.POST)
	public @ResponseBody ResultData assignSprint(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "sprintID") Long sprintID,
			HttpServletRequest request, RedirectAttributes ra) {
		ResultData result = new ResultData();
		Sprint sprint = agileSrv.findById(sprintID);
		Task task = taskSrv.findById(taskID);
		Project project = task.getProject();
		if (!projSrv.canAdminister(project)) {
			throw new TasqAuthException(msg);
		}
		Hibernate.initialize(task.getSprints());
		if (sprint.isActive()) {
			if (checkIfNotEstimated(task, project)) {
				result.code = ResultData.WARNING;
				result.message = msg.getMessage(
						"agile.task2Sprint.Notestimated",
						new Object[] { task.getId(), sprint.getSprintNo() },
						Utils.getCurrentLocale());
				return result;
			}
			String message = "";
			if (task.isEstimated() && project.getTimeTracked()) {
				message = task.getEstimate();
			}
			wrkLogSrv.addActivityLog(task, message, LogType.TASKSPRINTADD);
		}
		task.addSprint(sprint);
		taskSrv.save(task);
		List<Task> subtasks = taskSrv.findSubtasks(task);
		for (Task subtask : subtasks) {
			subtask.addSprint(sprint);
			taskSrv.save(subtask);
		}
		result.code = ResultData.OK;
		result.message = msg.getMessage("agile.task2Sprint", new Object[] {
				task.getId(), sprint.getSprintNo() }, Utils.getCurrentLocale());
		return result;
	}

	@Transactional
	@RequestMapping(value = "/task/sprintRemove", method = RequestMethod.POST)
	public @ResponseBody ResultData removeFromSprint(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "sprintID") Long sprintID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Task task = taskSrv.findById(taskID);
		Project project = task.getProject();
		if (!projSrv.canAdminister(project)) {
			throw new TasqAuthException(msg);
		}
		ResultData result = new ResultData();
		Sprint sprint = agileSrv.findById(sprintID);
		if (sprint.isActive()) {
			wrkLogSrv.addActivityLog(task, null, LogType.TASKSPRINTREMOVE);
		}
		Hibernate.initialize(task.getSprints());
		task.removeSprint(sprint);
		taskSrv.save(task);
		List<Task> subtasks = taskSrv.findSubtasks(task);
		for (Task subtask : subtasks) {
			subtask.removeSprint(sprint);
			taskSrv.save(subtask);
		}
		result.code = ResultData.OK;
		result.message = msg.getMessage("agile.taskRemoved",
				new Object[] { task.getId() }, Utils.getCurrentLocale());
		return result;
	}

	@Transactional
	@RequestMapping(value = "/scrum/delete", method = RequestMethod.GET)
	public String deleteSprint(@RequestParam(value = "id") Long id,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = agileSrv.findById(id);
		Project project = sprint.getProject();
		if (!projSrv.canAdminister(project)) {
			throw new TasqAuthException(msg);
		}
		// consider checking if is active?
		if (sprint != null) {
			if (canEdit(sprint.getProject())) {
				List<Task> taskList = taskSrv.findAllBySprint(sprint);
				for (Task task : taskList) {
					Hibernate.initialize(task.getSprints());
					if (task.getSprints().contains(sprint)) {
						task.removeSprint(sprint);
						taskSrv.save(task);
					}
				}
			}
		}
		agileSrv.delete(sprint);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.sprint.removed", null,
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@ResponseBody
	@RequestMapping(value = "/scrum/start", method = RequestMethod.POST)
	public ResultData startSprint(@RequestParam(value = "sprintID") Long id,
			@RequestParam(value = "projectID") Long projectId,
			@RequestParam(value = "sprintStart") String sprintStart,
			@RequestParam(value = "sprintEnd") String sprintEnd,
			@RequestParam(value = "sprintStartTime") String sprintStartTime,
			@RequestParam(value = "sprintEndTime") String sprintEndTime) {
		Project project = projSrv.findById(projectId);
		if (!projSrv.canAdminister(project)) {
			throw new TasqAuthException(msg);
		}
		// check if other sprints are not ending when this new is starting
		List<Sprint> allSprints = agileSrv.findByProjectId(projectId);
		sprintStart += " " + sprintStartTime;
		sprintEnd += " " + sprintEndTime;
		Date startDate = Utils.convertStringToDateAndTime(sprintStart);
		Date endDate = Utils.convertStringToDateAndTime(sprintEnd);
		for (Sprint sprint : allSprints) {
			if (sprint.getRawEnd_date() != null) {
				DateTime sprintEndDate = new DateTime(sprint.getRawEnd_date());
				DateTime sprintStartDate = new DateTime(startDate);
				if (sprintEndDate.equals(sprintStartDate)
						|| sprintStartDate.isBefore(sprintEndDate)) {
					return new ResultData(ResultData.WARNING, msg.getMessage(
							"agile.sprint.startOnEnd",
							new Object[] { sprint.getSprintNo(), sprintStart },
							Utils.getCurrentLocale()));
				}
			}
		}
		Sprint sprint = agileSrv.findById(id);
		Sprint active = agileSrv.findByProjectIdAndActiveTrue(projectId);
		if (sprint != null && !sprint.isActive() && active == null) {
			if (canEdit(sprint.getProject()) || Roles.isAdmin()) {
				Period total_estimate = new Period();
				int totalStoryPoints = 0;
				StringBuilder warnings = new StringBuilder();
				List<Task> taskList = taskSrv.findAllBySprint(sprint);
				for (Task task : taskList) {
					if (task.getState().equals(TaskState.ONGOING)
							|| task.getState().equals(TaskState.BLOCKED)) {
						total_estimate = PeriodHelper.plusPeriods(
								total_estimate, task.getRawRemaining());
					} else {
						total_estimate = PeriodHelper.plusPeriods(
								total_estimate, task.getRawEstimate());
					}
					if (!project.getTimeTracked()) {
						if (!task.isSubtask() && task.getStory_points() == 0
								&& task.isEstimated()) {
							warnings.append(task.getId());
							warnings.append(" ");
						}
					}
					totalStoryPoints += task.getStory_points();
				}
				if (warnings.length() > 0) {
					return new ResultData(ResultData.WARNING, msg.getMessage(
							"agile.sprint.notEstimated.sp",
							new Object[] { warnings.toString() },
							Utils.getCurrentLocale()));
				}
				sprint.setTotalEstimate(total_estimate);
				sprint.setTotalStoryPoints(totalStoryPoints);
				sprint.setStart_date(startDate);
				sprint.setEnd_date(endDate);
				sprint.setActive(true);
				agileSrv.save(sprint);
				wrkLogSrv.addWorkLogNoTask(null, project, LogType.SPRINT_START);
				return new ResultData(ResultData.OK, msg.getMessage(
						"agile.sprint.started",
						new Object[] { sprint.getSprintNo() },
						Utils.getCurrentLocale()));
			}
		}
		return new ResultData(ResultData.ERROR, msg.getMessage("error.unknown",
				null, Utils.getCurrentLocale()));
	}

	@Transactional
	@RequestMapping(value = "/scrum/stop", method = RequestMethod.GET)
	public String finishSprint(@RequestParam(value = "id") Long id,
			HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = agileSrv.findById(id);
		if (sprint != null) {
			Project project = projSrv.findById(sprint.getProject().getId());
			if (!projSrv.canAdminister(project)) {
				throw new TasqAuthException(msg);
			}
			if (sprint.isActive()
					&& (canEdit(sprint.getProject()) || Roles.isAdmin())) {
				sprint.setActive(false);
				sprint.finish();
				sprint.setEnd_date(new Date());
				List<Task> taskList = taskSrv.findAllBySprint(sprint);
				Map<TaskState, Integer> state_count = new HashMap<TaskState, Integer>();
				for (Task task : taskList) {
					task.setInSprint(false);
					Integer value = state_count.get(task.getState());
					value = value == null ? 0 : value;
					value++;
					state_count.put((TaskState) task.getState(), value);
					taskSrv.save(task);
				}
				StringBuilder message = new StringBuilder(msg.getMessage(
						"agile.sprint.finished",
						new Object[] { sprint.getSprintNo() },
						Utils.getCurrentLocale()));
				for (Entry<TaskState, Integer> entry : state_count.entrySet()) {
					message.append(NEW_LINE);
					message.append(msg.getMessage(entry.getKey().getCode(),
							null, Utils.getCurrentLocale()));
					message.append(SPACE);
					message.append(entry.getValue());
				}
				MessageHelper.addSuccessAttribute(ra, message.toString());
				wrkLogSrv.addWorkLogNoTask(null, project, LogType.SPRINT_STOP);
				agileSrv.save(sprint);
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/{id}/scrum/reports", method = RequestMethod.GET, produces = "application/json")
	public String showBurndown(@PathVariable String id,
			@RequestParam(value = "sprint", required = false) Long sprintNo,
			Model model, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (sprintNo != null) {
				Sprint sprint = agileSrv.findByProjectIdAndSprintNo(
						project.getId(), sprintNo);
				if (sprint.getRawEnd_date() == null & !sprint.isActive()) {
					MessageHelper.addWarningAttribute(ra,
							msg.getMessage("agile.sprint.notStarted",
									new Object[] { sprintNo },
									Utils.getCurrentLocale()));
					return "redirect:/" + project.getProjectId()
							+ "/scrum/backlog";
				}
			}
			Sprint lastSprint = agileSrv.findByProjectIdAndActiveTrue(project
					.getId());
			if (lastSprint == null) {
				List<Sprint> sprints = agileSrv
						.findByProjectId(project.getId());
				if (sprints.isEmpty()) {
					MessageHelper.addWarningAttribute(ra, msg.getMessage(
							"agile.sprint.noSprints", null,
							Utils.getCurrentLocale()));
					return "redirect:/" + project.getProjectId()
							+ "/scrum/backlog";
				}
				int counter = 1;
				Collections.sort(sprints, new SprintSorter());
				lastSprint = sprints.get(sprints.size() - counter);
				while (lastSprint.getStart_date() == "") {
					counter++;
					if (counter > sprints.size()) {
						MessageHelper.addWarningAttribute(ra, msg.getMessage(
								"agile.sprint.noSprints", null,
								Utils.getCurrentLocale()));
						return "redirect:/" + project.getProjectId()
								+ "/scrum/backlog";
					}
					lastSprint = sprints.get(sprints.size() - counter);
				}
			}
			model.addAttribute("lastSprint", lastSprint);
			model.addAttribute("project", project);
		}
		return "/scrum/reports";
	}

	/**
	 * Retrieves burndown map for sprint. No extra checking, only if exists and
	 * if is started;
	 * 
	 * @param id
	 * @param sprintNo
	 * @param model
	 * @param ra
	 * @return
	 */
	@RequestMapping(value = "/{id}/sprint-data", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody SprintData showBurndownChart(@PathVariable String id,
			@RequestParam(value = "sprint") Long sprintNo) {
		SprintData result = new SprintData();
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			Sprint sprint = agileSrv.findByProjectIdAndSprintNo(
					project.getId(), sprintNo);
			if (sprint == null
					|| (sprint.getRawEnd_date() == null & !sprint.isActive())) {
				String message = msg.getMessage("agile.sprint.notStarted",
						new Object[] { sprintNo }, Utils.getCurrentLocale());
				result.setMessage(message);
				return result;
			}
			DateTime startTime = new DateTime(sprint.getRawStart_date());
			DateTime endTime = new DateTime(sprint.getRawEnd_date());
			List<Task> sprintTasks = taskSrv.findAllBySprint(sprint);
			for (Task task : sprintTasks) {
				DateTime finishDate = null;
				if (task.getFinishDate() != null) {
					finishDate = new DateTime(task.getFinishDate());
				}
				if (task.getState().equals(TaskState.CLOSED)
						&& (finishDate != null && endTime.isAfter(finishDate))) {
					result.getTasks().get(SprintData.CLOSED)
							.add(new DisplayTask(task));
				} else {
					result.getTasks().get(SprintData.ALL)
							.add(new DisplayTask(task));
				}
			}
			// Fill maps based on time or story point driven board

			boolean timeTracked = project.getTimeTracked();
			List<WorkLog> wrkList = wrkLogSrv.getAllSprintEvents(sprint);
			result.setWorklogs(DisplayWorkLog.convertToDisplayWorkLogs(wrkList));
			result.setTimeBurned(agileSrv.fillTimeBurndownMap(wrkList,
					startTime, endTime));
			Period totalTime = new Period();
			for (Map.Entry<String, Float> entry : result.getTimeBurned()
					.entrySet()) {
				totalTime = PeriodHelper.plusPeriods(totalTime,
						Utils.getPeriodValue(entry.getValue()));
			}

			result.setTotalTime(String.valueOf(Utils.round(
					Utils.getFloatValue(totalTime), 2)));
			return fillLeftAndBurned(result, sprint, wrkList, timeTracked);
		} else {
			return result;
		}
	}

	@RequestMapping(value = "/getSprints", method = RequestMethod.GET)
	public @ResponseBody List<DisplaySprint> showProjectSprints(
			@RequestParam Long projectID, HttpServletResponse response) {
		response.setContentType("application/json");
		List<Sprint> projectSprints = agileSrv.findByProjectIdAndFinished(
				projectID, false);
		List<DisplaySprint> result = agileSrv.convertToDisplay(projectSprints);
		Collections.sort(result);
		return result;
	}

	/**
	 * Checks if sprint with given id is active or not
	 * 
	 * @param sprintID
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/scrum/isActive", method = RequestMethod.GET)
	public @ResponseBody boolean checkIfActive(
			@RequestParam(value = "id") Long sprintID,
			HttpServletResponse response) {
		Sprint sprint = agileSrv.findById(sprintID);
		return sprint.isActive();
	}

	/**
	 * Checks if task is properly estimated based on project settings (
	 * Estimated time not 0m for time based or story points not 0 for story
	 * points driven
	 * 
	 * @param task
	 * @param project
	 * @return
	 */
	private boolean checkIfNotEstimated(Task task, Project project) {
		if (!project.getTimeTracked()) {
			if (task.getStory_points() == 0 && task.isEstimated()) {
				return true;
			}
		} else {
			if (task.getEstimate().equals("0m") && task.isEstimated()) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Fills Left and burndown charts data
	 * 
	 * @param result
	 *            - Previously filled SpringData
	 * @param sprint
	 *            - sprint for which data will be filled
	 * @param wrkList
	 *            - list of all worklogs from this sprint
	 * @param timeTracked
	 *            - true if project for which chart is sent is time tracked or
	 *            story point
	 * @return
	 */
	private SprintData fillLeftAndBurned(SprintData result, Sprint sprint,
			List<WorkLog> wrkList, boolean timeTracked) {
		Map<LocalDate, Float> leftMap = fillLeftMap(wrkList, timeTracked);
		Map<LocalDate, Float> burnedMap = fillBurnednMap(wrkList, timeTracked);
		LocalDate startTime = new LocalDate(sprint.getRawStart_date());
		LocalDate endTime = new LocalDate(sprint.getRawEnd_date());
		int sprintDays = Days.daysBetween(startTime, endTime).getDays() + 1;
		Integer totalPoints = new Integer(0);
		SprintData data = result;
		Float remaining_estimate = new Float(0);
		Float burned = new Float(0);
		if (timeTracked) {
			remaining_estimate = Utils.getFloatValue(sprint.getTotalEstimate());
		} else {
			remaining_estimate = new Float(sprint.getTotalStoryPoints());
		}
		// Fill ideal burndown
		data.createIdeal(startTime.toString(), remaining_estimate,
				endTime.toString());
		// Iterate over sprint days
		for (int i = 0; i < sprintDays; i++) {
			LocalDate date = startTime.plusDays(i);
			Float value = leftMap.get(date);
			Float valueBurned = burnedMap.get(date);
			value = value == null ? 0 : value;
			valueBurned = valueBurned == null ? 0 : valueBurned;
			remaining_estimate -= value;
			burned += valueBurned;
			if (date.isAfter(LocalDate.now())) {
				data.putToLeft(date.toString(), null);
				data.getBurned().put(date.toString(), null);
			} else {
				data.putToLeft(date.toString(), remaining_estimate);
				data.getBurned().put(date.toString(), burned);
				totalPoints = burned.intValue();
			}
		}
		if (!timeTracked) {
			data.setTotalPoints(totalPoints);
		}
		return data;
	}

	/**
	 * Fils burned story points map based on worklogs
	 * 
	 * @param worklogList
	 *            list of events with task closed event
	 * @return
	 */
	private Map<LocalDate, Float> fillLeftMap(List<WorkLog> worklogList,
			boolean time) {
		Map<LocalDate, Float> burndownMap = new LinkedHashMap<LocalDate, Float>();
		for (WorkLog workLog : worklogList) {
			LocalDate dateLogged = new LocalDate(workLog.getRawTime());
			if (time) {
				Float value = burndownMap.get(dateLogged);
				value = addOrSubstractTime(workLog, value);
				burndownMap.put(dateLogged, value);
			} else {
				if (workLog.getActivity() == null) {
					Float value = burndownMap.get(dateLogged);
					value = addOrSubstractPoints(workLog, value);
					burndownMap.put(dateLogged, value);
				}
			}
		}
		return burndownMap;
	}

	/**
	 * Fill burned map based on worklog list
	 * 
	 * @param worklogList
	 * @param time
	 * @return
	 */
	private Map<LocalDate, Float> fillBurnednMap(List<WorkLog> worklogList,
			boolean time) {
		Map<LocalDate, Float> burndedMap = new LinkedHashMap<LocalDate, Float>();
		for (WorkLog workLog : worklogList) {
			if (!LogType.ESTIMATE.equals(workLog.getType())
					&& !LogType.TASKSPRINTADD.equals(workLog.getType())
					&& !LogType.TASKSPRINTREMOVE.equals(workLog.getType())) {
				LocalDate dateLogged = new LocalDate(workLog.getRawTime());
				if (time) {
					Float value = burndedMap.get(dateLogged);
					// If task reopened then re-add SP
					value = addOrSubstractTime(workLog, value);
					burndedMap.put(dateLogged, value);
				} else {
					if (workLog.getActivity() == null) {
						Float value = burndedMap.get(dateLogged);
						// If task reopened then re-add SP
						value = addOrSubstractPoints(workLog, value);
						burndedMap.put(dateLogged, value);
					}
				}
			}
		}
		return burndedMap;
	}

	/**
	 * Based on event type either add or subtract value for time tracked
	 * projects
	 * 
	 * @param workLog
	 * @param value
	 * @return
	 */
	private Float addOrSubstractTime(WorkLog workLog, Float value) {
		Float result = value;
		Float taskLogged = Utils.getFloatValue(workLog.getActivity());
		if (LogType.ESTIMATE.equals(workLog.getType())) {
			taskLogged = Utils.getFloatValue(PeriodHelper.inFormat(workLog
					.getMessage()));
			taskLogged *= -1;
		}
		if (value == null) {
			result = taskLogged;
		} else {
			result += taskLogged;
		}
		return result;
	}

	private Float addOrSubstractPoints(WorkLog workLog, Float value) {
		Float result = value;
		Integer taskStoryPoints = workLog.getTask().getStory_points();
		if (LogType.REOPEN.equals(workLog.getType())
				|| LogType.TASKSPRINTADD.equals(workLog.getType())) {
			taskStoryPoints *= -1;
		}
		if (LogType.ESTIMATE.equals(workLog.getType())) {
			try {
				taskStoryPoints = -1 * Integer.valueOf(workLog.getMessage());
			} catch (NumberFormatException e) {
				LOG.debug(workLog.toString()
						+ ": No story points in estimate change "
						+ workLog.getTask());
			}
		}
		if (value == null) {
			result = new Float(taskStoryPoints);
		} else {
			result += new Float(taskStoryPoints);
		}
		return result;
	}

	/**
	 * Checks if currently logged in user have privileges to change anything in
	 * project
	 * 
	 * @param task
	 * @return
	 */
	private boolean canEdit(Project project) {
		Project repo_project = projSrv.findById(project.getId());
		if (repo_project == null) {
			return false;
		}
		Account currentAccount = Utils.getCurrentAccount();
		return (repo_project.getAdministrators().contains(currentAccount)
				|| repo_project.getParticipants().contains(currentAccount) || Roles
					.isAdmin());
	}
}
