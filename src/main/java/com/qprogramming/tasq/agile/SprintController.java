package com.qprogramming.tasq.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
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
import com.qprogramming.tasq.account.DisplayAccount;
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
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class SprintController {

	private static final String SPACE = " ";

	private static final String NEW_LINE = "\n";

	@Autowired
	ProjectService projSrv;

	@Autowired
	TaskService taskSrv;

	@Autowired
	SprintRepository sprintRepo;

	@Autowired
	WorkLogService wrkLogSrv;

	@Autowired
	private MessageSource msg;

	@RequestMapping(value = "{id}/scrum/board", method = RequestMethod.GET)
	public String showBoard(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!project.getParticipants().contains(Utils.getCurrentAccount())
					&& !Roles.isAdmin()) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			Sprint sprint = sprintRepo.findByProjectIdAndActiveTrue(project
					.getId());
			if (sprint == null) {
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.sprint.noActive", null,
								Utils.getCurrentLocale()));
				return "redirect:/" + project.getProjectId() + "/scrum/backlog";
			}
			List<Task> taskList = new LinkedList<Task>();
			List<DisplayTask> resultList = new LinkedList<DisplayTask>();
			taskList = taskSrv.findAllBySprint(sprint);
			for (Task task : taskList) {
				resultList.add(new DisplayTask(task));
			}
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
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
			if (!project.getParticipants().contains(Utils.getCurrentAccount())
					&& !Roles.isAdmin()) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			List<DisplayTask> resultList = new LinkedList<DisplayTask>();
			List<Task> taskList = taskSrv.findAllByProject(project);
			// Don't show closed tasks in backlog view
			for (Task task : taskList) {
				if (!task.getState().equals(TaskState.CLOSED)) {
					resultList.add(new DisplayTask(task));
				}
			}
			Map<Sprint, List<DisplayTask>> sprint_result = new LinkedHashMap<Sprint, List<DisplayTask>>();

			List<Sprint> sprintList = sprintRepo.findByProjectIdAndFinished(
					project.getId(), false);
			Collections.sort(taskList, new TaskSorter(
					TaskSorter.SORTBY.PRIORITY, true));
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
			model.addAttribute("tasks", taskList);
			model.addAttribute("sprints", sprintList);
		}
		return "/scrum/backlog";
	}

	@RequestMapping(value = "/{id}/scrum/create", method = RequestMethod.POST)
	public String createSprint(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (!project.getAdministrators().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg);
		}
		List<Sprint> sprints = sprintRepo.findByProjectId(project.getId());
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setSprint_no((long) sprints.size() + 1);
		sprintRepo.save(sprint);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.createdSprint", null,
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/{id}/scrum/sprintAssign", method = RequestMethod.POST)
	public String assignSprint(@PathVariable String id,
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "sprintID") Long sprintID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(sprintID);
		Task task = taskSrv.findById(taskID);
		Project project = task.getProject();
		if (!project.getAdministrators().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg);
		}
		Hibernate.initialize(task.getSprints());
		if (sprint.isActive()) {
			wrkLogSrv.addActivityLog(task, null, LogType.TASKSPRINTADD);
		}
		task.addSprint(sprint);
		taskSrv.save(task);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.task2Sprint", new Object[] {
						task.getId(), sprint.getSprintNo() },
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/sprintRemove", method = RequestMethod.POST)
	public String removeFromSprint(
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "sprintID") Long sprintID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Task task = taskSrv.findById(taskID);
		Project project = task.getProject();
		if (!project.getAdministrators().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg);
		}
		Sprint sprint = sprintRepo.findById(sprintID);
		if (sprint.isActive()) {
			wrkLogSrv.addActivityLog(task, null, LogType.TASKSPRINTREMOVE);
		}
		Hibernate.initialize(task.getSprints());
		task.removeSprint(sprint);
		taskSrv.save(task);
		MessageHelper.addSuccessAttribute(ra, msg.getMessage(
				"agile.taskRemoved", new Object[] { task.getId() },
				Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/scrum/delete", method = RequestMethod.GET)
	public String deleteSprint(@RequestParam(value = "id") Long id,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(id);
		Project project = sprint.getProject();
		if (!project.getAdministrators().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg);
		}
		if (sprint != null && !sprint.isActive()) {
			if (canEdit(sprint.getProject())) {
				List<Task> taskList = taskSrv.findAllBySprint(sprint);
				for (Task task : taskList) {
					Hibernate.initialize(task.getSprints());
					task.removeSprint(sprint);
					taskSrv.save(task);
				}
			}
		}
		sprintRepo.delete(sprint);
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
			@RequestParam(value = "sprintEnd") String sprintEnd, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(id);
		Project project = projSrv.findById(projectId);
		Sprint active = sprintRepo.findByProjectIdAndActiveTrue(projectId);
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
						if (task.getStory_points() == 0) {
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
				sprint.setStart_date(Utils.convertStringToDate(sprintStart));
				sprint.setEnd_date(Utils.convertStringToDate(sprintEnd));
				sprint.setActive(true);
				sprintRepo.save(sprint);
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
		Sprint sprint = sprintRepo.findById(id);
		if (sprint != null) {
			Project project = projSrv.findById(sprint.getProject().getId());
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
				sprintRepo.save(sprint);
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/{id}/scrum/burndown", method = RequestMethod.GET, produces = "application/json")
	public String showBurndown(@PathVariable String id,
			@RequestParam(value = "sprint", required = false) Long sprintNo,
			Model model, RedirectAttributes ra) {
		Map<String, Integer> resultsEstimates = new LinkedHashMap<String, Integer>();
		Map<String, Integer> resultsBurned = new LinkedHashMap<String, Integer>();
		Map<String, Integer> resultsIdeal = new LinkedHashMap<String, Integer>();
		Map<LocalDate, Integer> leftMap = new HashMap<LocalDate, Integer>();
		Map<LocalDate, Integer> burnedMap = new HashMap<LocalDate, Integer>();
		Map<LocalDate, Period> timeBurndownMap = new HashMap<LocalDate, Period>();
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (sprintNo != null) {
				Sprint sprint = sprintRepo.findByProjectIdAndSprintNo(
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
			Sprint lastSprint = sprintRepo.findByProjectIdAndActiveTrue(project
					.getId());
			if (lastSprint == null) {
				List<Sprint> sprints = sprintRepo.findByProjectId(project
						.getId());
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
			Sprint sprint;
			if (sprintNo != null) {
				sprint = sprintRepo.findByProjectIdAndSprintNo(project.getId(),
						sprintNo);
			} else {
				sprint = lastSprint;
			}
			// Fill maps based on time or story point driven board
			LocalDate startTime = new LocalDate(sprint.getRawStart_date());
			LocalDate endTime = new LocalDate(sprint.getRawEnd_date());
			int sprintDays = Days.daysBetween(startTime, endTime).getDays() + 1;
			boolean timeTracked = project.getTimeTracked();
			List<WorkLog> wrkList = wrkLogSrv.getSprintEvents(sprint,
					timeTracked);
			if (timeTracked) {
				timeBurndownMap = fillTimeMap(wrkList);
				Period remaining_estimate = sprint.getTotalEstimate();
				Period burned = new Period();
				// Fill ideal burndown
				resultsIdeal.put(startTime.toString(), (int) sprint
						.getTotalEstimate().toStandardDuration()
						.getStandardHours());
				resultsIdeal.put(endTime.toString(), 0);
				// Iterate over sprint days
				for (int i = 0; i < sprintDays; i++) {
					LocalDate date = startTime.plusDays(i);
					Period value = timeBurndownMap.get(date);
					remaining_estimate = PeriodHelper.minusPeriods(
							remaining_estimate, value);
					burned = PeriodHelper.plusPeriods(burned, value);
					if (date.isAfter(LocalDate.now())) {
						resultsEstimates.put(date.toString(), null);
						resultsBurned.put(date.toString(), null);
					} else {
						resultsEstimates.put(date.toString(),
								(int) remaining_estimate.toStandardDuration()
										.getStandardHours());
						resultsBurned.put(date.toString(), (int) burned
								.toStandardDuration().getStandardHours());
					}
				}
			} else {
				leftMap = fillLeftMap(wrkList, false);
				burnedMap = fillBurnednMap(wrkList,false);
				Integer remainingEstimate = sprint.getTotalStoryPoints();
				Integer burned = new Integer(0);
				resultsIdeal.put(startTime.toString(), remainingEstimate);
				resultsIdeal.put(endTime.toString(), 0);
				for (int i = 0; i < sprintDays; i++) {
					LocalDate date = startTime.plusDays(i);
					Integer value = leftMap.get(date);
					Integer valueBurned = burnedMap.get(date);
					value = value == null ? 0 : value;
					valueBurned = valueBurned == null ? 0 : valueBurned;

					remainingEstimate -= value;
					burned += valueBurned;

					if (date.isAfter(LocalDate.now())) {
						resultsEstimates.put(date.toString(), null);
						resultsBurned.put(date.toString(), null);
					} else {
						resultsEstimates
								.put(date.toString(), remainingEstimate);
						resultsBurned.put(date.toString(), burned);
					}
				}
			}
			model.addAttribute("sprint", sprint);
			model.addAttribute("lastSprint", lastSprint);
			model.addAttribute("workLogList",
					DisplayWorkLog.convertToDisplayWorkLogs(wrkList));
			model.addAttribute("project", project);
			model.addAttribute("left", formatResults(resultsEstimates));
			model.addAttribute("burned", formatResults(resultsBurned));
			model.addAttribute("ideal", formatResults(resultsIdeal));
		}
		return "/scrum/burndown";
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
	@RequestMapping(value = "/{id}/scrum/sprint/burndown", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	BurndownChart showBurndownChart(@PathVariable String id,
			@RequestParam(value = "sprint") Long sprintNo) {
		BurndownChart result = new BurndownChart();
		Map<LocalDate, Integer> leftMap = new HashMap<LocalDate, Integer>();
		Map<LocalDate, Integer> burnedMap = new HashMap<LocalDate, Integer>();
		Map<LocalDate, Period> timeBurndownMap = new HashMap<LocalDate, Period>();
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			Sprint sprint = sprintRepo.findByProjectIdAndSprintNo(
					project.getId(), sprintNo);
			if (sprint == null
					|| (sprint.getRawEnd_date() == null & !sprint.isActive())) {
				String message = msg.getMessage("agile.sprint.notStarted",
						new Object[] { sprintNo }, Utils.getCurrentLocale());
				result.setMessage(message);
				return result;
			}
			// Fill maps based on time or story point driven board
			LocalDate startTime = new LocalDate(sprint.getRawStart_date());
			LocalDate endTime = new LocalDate(sprint.getRawEnd_date());
			int sprintDays = Days.daysBetween(startTime, endTime).getDays() + 1;
			boolean timeTracked = project.getTimeTracked();
			List<WorkLog> wrkList = wrkLogSrv.getAllSprintEvents(sprint);
			result.setTimeBurned(fillTimeBurndownMap(wrkList, startTime,
					endTime));
			if (timeTracked) {
				Period remaining_estimate = sprint.getTotalEstimate();
				Period burned = new Period();
				// Fill ideal burndown
				result.createIdeal(startTime.toString(), (int) sprint
						.getTotalEstimate().toStandardDuration()
						.getStandardHours(), endTime.toString());
				// Iterate over sprint days
				for (int i = 0; i < sprintDays; i++) {
					LocalDate date = startTime.plusDays(i);
					Period value = timeBurndownMap.get(date);
					remaining_estimate = PeriodHelper.minusPeriods(
							remaining_estimate, value);
					burned = PeriodHelper.plusPeriods(burned, value);
					if (date.isAfter(LocalDate.now())) {
						result.putToLeft(date.toString(), null);
					} else {
						result.putToLeft(date.toString(),
								(int) remaining_estimate.toStandardDuration()
										.getStandardHours());
					}
				}
			} else {
				leftMap = fillLeftMap(wrkList, false);
				burnedMap = fillBurnednMap(wrkList,false);
				Integer remainingEstimate = sprint.getTotalStoryPoints();
				Integer burned = new Integer(0);
				result.createIdeal(startTime.toString(), remainingEstimate,
						endTime.toString());
				for (int i = 0; i < sprintDays; i++) {
					LocalDate date = startTime.plusDays(i);
					Integer value = leftMap.get(date);
					Integer valueBurned = burnedMap.get(date);
					value = value == null ? 0 : value;
					valueBurned = valueBurned == null ? 0 : valueBurned;
					remainingEstimate -= value;
					burned += valueBurned;
					if (date.isAfter(LocalDate.now())) {
						result.putToLeft(date.toString(), null);
						result.getPointsBurned().put(date.toString(), null);
					} else {
						result.putToLeft(date.toString(), remainingEstimate);
						result.getPointsBurned().put(date.toString(), burned);
					}
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "/getSprints", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplaySprint> showProjectSprints(@RequestParam Long projectID,
			HttpServletResponse response) {
		response.setContentType("application/json");
		List<DisplaySprint> result = new LinkedList<DisplaySprint>();
		List<Sprint> projectSprints = sprintRepo.findByProjectIdAndFinished(
				projectID, false);
		for (Sprint sprint : projectSprints) {
			result.add(new DisplaySprint(sprint));
		}
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
	public @ResponseBody
	boolean checkIfActive(@RequestParam(value = "id") Long sprintID,
			HttpServletResponse response) {
		Sprint sprint = sprintRepo.findById(sprintID);
		return sprint.isActive();
	}

	private Map<String, Integer> fillTimeBurndownMap(List<WorkLog> wrkList,
			LocalDate startTime, LocalDate endTime) {
		int sprintDays = Days.daysBetween(startTime, endTime).getDays() + 1;
		Map<LocalDate, Period> timeBurndownMap = fillTimeMap(wrkList);
		Map<String, Integer> resultsBurned = new LinkedHashMap<String, Integer>();
		Period burned = new Period();
		for (int i = 0; i < sprintDays; i++) {
			LocalDate date = startTime.plusDays(i);
			Period value = timeBurndownMap.get(date);
			burned = PeriodHelper.plusPeriods(burned, value);
			if (date.isAfter(LocalDate.now())) {
				resultsBurned.put(date.toString(), null);
			} else {
				resultsBurned.put(date.toString(), (int) burned
						.toStandardDuration().getStandardHours());
			}
		}
		return resultsBurned;
	}

	/**
	 * Fills burndown map with worklogs in format <Date, Period Burned> Only
	 * events with before present day are added
	 * 
	 * @param worklogList
	 * 
	 * @return
	 **/
	private Map<LocalDate, Period> fillTimeMap(List<WorkLog> worklogList) {
		Map<LocalDate, Period> burndownMap = new LinkedHashMap<LocalDate, Period>();
		for (WorkLog workLog : worklogList) {
			if (workLog.getActivity() != null) {
				LocalDate dateLogged = new LocalDate(workLog.getRawTime());
				Period value = burndownMap.get(dateLogged);
				if (value == null) {
					value = workLog.getActivity();
				} else {
					value = PeriodHelper.plusPeriods(value,
							workLog.getActivity());
				}
				burndownMap.put(dateLogged, value);
			}
		}
		return burndownMap;
	}

	/**
	 * Fils burned story points map based on worklogs
	 * 
	 * @param worklogList
	 *            list of events with task closed event
	 * @return
	 */
	private Map<LocalDate, Integer> fillLeftMap(List<WorkLog> worklogList,
			boolean time) {
		Map<LocalDate, Integer> burndownMap = new LinkedHashMap<LocalDate, Integer>();
		for (WorkLog workLog : worklogList) {
			LocalDate dateLogged = new LocalDate(workLog.getRawTime());
			if (time) {
				Integer value = burndownMap.get(dateLogged);
				value = addOrSubstractTime(workLog, value);
				burndownMap.put(dateLogged, value);
			} else {
				if (workLog.getActivity() == null) {
					Integer value = burndownMap.get(dateLogged);
					value = addOrSubstractPoints(workLog, value);
					burndownMap.put(dateLogged, value);
				}
			}
		}
		return burndownMap;
	}

	private Map<LocalDate, Integer> fillBurnednMap(List<WorkLog> worklogList,
			boolean time) {
		Map<LocalDate, Integer> burndedMap = new LinkedHashMap<LocalDate, Integer>();
		for (WorkLog workLog : worklogList) {
			if (!LogType.ESTIMATE.equals(workLog.getType())
					&& !LogType.TASKSPRINTADD.equals(workLog.getType())
					&& !LogType.TASKSPRINTREMOVE.equals(workLog.getType())) {
				LocalDate dateLogged = new LocalDate(workLog.getRawTime());
				if (time) {
					Integer value = burndedMap.get(dateLogged);
					// If task reopened then re-add SP
					value = addOrSubstractTime(workLog, value);
					burndedMap.put(dateLogged, value);
				} else {
					if (workLog.getActivity() == null) {
						Integer value = burndedMap.get(dateLogged);
						// If task reopened then re-add SP
						value = addOrSubstractPoints(workLog, value);
						burndedMap.put(dateLogged, value);
					}
				}
			}
		}
		return burndedMap;
	}

	// TODO check if estimate left was changed!!!!
	private Integer addOrSubstractTime(WorkLog workLog, Integer value) {
		Integer result = value;
		Integer taskStoryPoints = workLog.getTask().getStory_points();
		if (LogType.REOPEN.equals(workLog.getType())
				|| LogType.TASKSPRINTADD.equals(workLog.getType())) {
			taskStoryPoints *= -1;
		}
		if (value == null) {
			result = taskStoryPoints;
		} else {
			result += taskStoryPoints;
		}
		return result;
	}

	private Integer addOrSubstractPoints(WorkLog workLog, Integer value) {
		Integer result = value;
		Integer taskStoryPoints = workLog.getTask().getStory_points();
		if (LogType.REOPEN.equals(workLog.getType())
				|| LogType.TASKSPRINTADD.equals(workLog.getType())) {
			taskStoryPoints *= -1;
		}
		if (value == null) {
			result = taskStoryPoints;
		} else {
			result += taskStoryPoints;
		}
		return result;
	}

	/**
	 * For purpose of jqPlot charts result formated into Array-like result.
	 * Could be produced as JSON but this way it's quicker
	 * 
	 * @param input
	 * @return
	 */
	private String formatResults(Map<String, Integer> input) {
		StringBuffer result = new StringBuffer();
		String separator = "";
		for (Entry<String, Integer> entry : input.entrySet()) {
			result.append(separator);
			result.append("[\'");
			result.append(entry.getKey());
			result.append("\',");
			result.append(entry.getValue());
			result.append("]");
			separator = ",";
		}
		return result.toString();
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
		Account current_account = Utils.getCurrentAccount();
		return (repo_project.getAdministrators().contains(current_account)
				|| repo_project.getParticipants().contains(current_account) || Roles
					.isAdmin());
	}

	class BurndownChart {
		private Map<String, Integer> left;
		private Map<String, Integer> pointsBurned;
		private Map<String, Integer> ideal;
		private Map<String, Integer> timeBurned;
		private String message;

		public BurndownChart() {
			left = new LinkedHashMap<String, Integer>();
			pointsBurned = new LinkedHashMap<String, Integer>();
			ideal = new LinkedHashMap<String, Integer>();
			timeBurned = new LinkedHashMap<String, Integer>();
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public Map<String, Integer> getLeft() {
			return left;
		}

		public Map<String, Integer> getIdeal() {
			return ideal;
		}

		public void setLeft(Map<String, Integer> left) {
			this.left = left;
		}

		public void setIdeal(Map<String, Integer> ideal) {
			this.ideal = ideal;
		}

		public Map<String, Integer> getPointsBurned() {
			return pointsBurned;
		}

		public Map<String, Integer> getTimeBurned() {
			return timeBurned;
		}

		public void setPointsBurned(Map<String, Integer> pointsBurned) {
			this.pointsBurned = pointsBurned;
		}

		public void setTimeBurned(Map<String, Integer> timeBurned) {
			this.timeBurned = timeBurned;
		}

		public void createIdeal(String startTime, int value, String endTime) {
			ideal.put(startTime, value);
			ideal.put(endTime, 0);
		}

		public void putToLeft(String time, Integer value) {
			left.put(time, value);
		}
	}
}
