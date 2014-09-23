package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Account.Role;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.SprintSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
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
			model.addAttribute("project", project);
			Sprint sprint = sprintRepo.findByProjectIdAndActive(
					project.getId(), true);
			if (sprint == null) {
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.sprint.noActive", null,
								Utils.getCurrentLocale()));
				return "redirect:/" + project.getProjectId() + "/scrum/backlog";
			}
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllBySprint(sprint);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			model.addAttribute("sprint", sprint);
			model.addAttribute("tasks", taskList);
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
			model.addAttribute("project", project);
			List<Task> resultList = new LinkedList<Task>();
			List<Task> taskList = taskSrv.findAllByProject(project);
			// Don't show closed tasks in backlog view
			for (Task task : taskList) {
				if (!task.getState().equals(TaskState.CLOSED)) {
					resultList.add(task);
				}
			}
			Map<Sprint, List<Task>> sprint_result = new LinkedHashMap<Sprint, List<Task>>();

			List<Sprint> sprintList = sprintRepo.findByProjectIdAndFinished(
					project.getId(), false);
			Collections.sort(taskList, new TaskSorter(
					TaskSorter.SORTBY.PRIORITY, false));
			Collections.sort(sprintList, new SprintSorter());
			// Assign tasks to sprints in order to display them
			for (Sprint sprint : sprintList) {
				List<Task> sprint_tasks = new LinkedList<Task>();
				for (Task task : taskList) {
					Hibernate.initialize(task.getSprints());
					if (task.getSprints().contains(sprint)) {
						sprint_tasks.add(task);
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
		Hibernate.initialize(task.getSprints());
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
	@RequestMapping(value = "/{id}/scrum/sprintRemove", method = RequestMethod.POST)
	public String removeFromSprint(@PathVariable String id,
			@RequestParam(value = "taskID") String taskID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Task task = taskSrv.findById(taskID);
		Sprint sprint = sprintRepo.findById(task.getSprint().getId());
		if (!sprint.isActive()) {
			Hibernate.initialize(task.getSprints());
			task.removeSprint(sprint);
			taskSrv.save(task);
			MessageHelper.addSuccessAttribute(
					ra,
					msg.getMessage("agile.taskRemoved",
							new Object[] { task.getId() },
							Utils.getCurrentLocale()));
		} else {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("agile.cantRemove.active", null,
							Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/scrum/delete", method = RequestMethod.GET)
	public String deleteSprint(@RequestParam(value = "id") Long id,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(id);
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
	@RequestMapping(value = "/scrum/start", method = RequestMethod.POST)
	public String startSprint(@RequestParam(value = "sprintID") Long id,
			@RequestParam(value = "project_id") Long project_id,
			@RequestParam(value = "sprint_start") String sprint_start,
			@RequestParam(value = "sprint_end") String sprint_end, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(id);
		Project project = projSrv.findById(project_id);
		Sprint active = sprintRepo.findByProjectIdAndActive(project_id, true);
		if (sprint != null && !sprint.isActive() && active == null) {
			if (canEdit(sprint.getProject())) {
				sprint.setStart_date(Utils.convertDueDate(sprint_start));
				sprint.setEnd_date(Utils.convertDueDate(sprint_end));
				sprint.setActive(true);
				Period total_estimate = new Period();
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
				}
				sprint.setTotal_estimate(total_estimate);
				sprintRepo.save(sprint);
				MessageHelper.addSuccessAttribute(ra, msg.getMessage(
						"agile.sprint.started",
						new Object[] { sprint.getSprintNo() },
						Utils.getCurrentLocale()));
				wrkLogSrv.addWorkLogNoTask(null, project, LogType.SPRINT_START);
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/scrum/stop", method = RequestMethod.GET)
	public String finishSprint(@RequestParam(value = "id") Long id,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(id);
		if (sprint != null) {
			Project project = projSrv.findById(sprint.getProject().getId());
			if (sprint.isActive() && canEdit(sprint.getProject())) {
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
			Model model) {
		Map<String, Integer> results_estimates = new LinkedHashMap<String, Integer>();
		Map<String, Integer> results_burned = new LinkedHashMap<String, Integer>();
		Map<String, Integer> results_ideal = new LinkedHashMap<String, Integer>();
		Map<LocalDate, Period> burndown_map = new HashMap<LocalDate, Period>();
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			Sprint lastSprint = sprintRepo.findByProjectIdAndActive(
					project.getId(), true);
			Sprint sprint;
			if (sprintNo != null) {
				sprint = sprintRepo.findByProjectIdAndSprintNo(project.getId(),
						sprintNo);
			} else {
				sprint = lastSprint;
			}
			List<Task> taskList = taskSrv.findAllBySprint(sprint);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			LocalDate start_time = new LocalDate(sprint.getRawStart_date());
			LocalDate end_time = new LocalDate(sprint.getRawEnd_date());
			int sprint_days = Days.daysBetween(start_time, end_time).getDays() + 1;
			for (Task task : taskList) {
				Hibernate.initialize(task.getWorklog());
				List<WorkLog> workLog_list = task.getWorklog();
				burndown_map = fillTaskBurndown(burndown_map, start_time,
						end_time, workLog_list);
			}
			// Iterate over sprint days
			Period remaining_estimate = sprint.getTotal_estimate();
			Period burned = new Period();
			// Fill ideal burndown
			results_ideal.put(start_time.toString(), (int) sprint
					.getTotal_estimate().toStandardDuration()
					.getStandardHours());
			results_ideal.put(end_time.toString(), 0);
			for (int i = 0; i < sprint_days; i++) {
				LocalDate date = start_time.plusDays(i);
				Period value = burndown_map.get(date);
				remaining_estimate = PeriodHelper.minusPeriods(
						remaining_estimate, value);
				burned = PeriodHelper.plusPeriods(burned, value);
				if (date.isAfter(LocalDate.now())) {
					results_estimates.put(date.toString(), null);
					results_burned.put(date.toString(), null);
				} else {
					results_estimates.put(date.toString(),
							(int) remaining_estimate.toStandardDuration()
									.getStandardHours());
					results_burned.put(date.toString(), (int) burned
							.toStandardDuration().getStandardHours());

				}
			}
			model.addAttribute("sprint", sprint);
			model.addAttribute("lastSprint", lastSprint);
			model.addAttribute("project", project);
			model.addAttribute("left", formatResults(results_estimates));
			model.addAttribute("burned", formatResults(results_burned));
			model.addAttribute("ideal", formatResults(results_ideal));
		}
		return "/scrum/burndown";
	}

	/**
	 * Fils burndown map with worklogs from task in format <Date, Period Burned>
	 * Only events with activity ( Period ) are added
	 * 
	 * @param burndown_map
	 *            - entry and exit map
	 * @param start_time
	 *            -sprint start time
	 * @param end_time
	 *            - sprint end time
	 * @param workLog_list
	 *            - task worklogs
	 * @return
	 */
	private Map<LocalDate, Period> fillTaskBurndown(
			Map<LocalDate, Period> burndown_map, LocalDate start_time,
			LocalDate end_time, List<WorkLog> workLog_list) {
		for (WorkLog worklog : workLog_list) {
			LocalDate date_logged = new LocalDate(worklog.getRawTime());
			if ((date_logged.isAfter(start_time) && date_logged
					.isBefore(end_time))
					|| (date_logged.equals(start_time) || date_logged
							.equals(end_time))) {
				Period value = burndown_map.get(date_logged);
				if (value == null) {
					burndown_map.put(date_logged, worklog.getActivity());
				} else {
					value = PeriodHelper.plusPeriods(value,
							worklog.getActivity());
					burndown_map.put(date_logged, value);
				}
			}
		}
		return burndown_map;
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
				|| repo_project.getParticipants().contains(current_account) || current_account
				.getRole().equals(Role.ROLE_ADMIN));
	}
}
