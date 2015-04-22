package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
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
public class KanbanController {
	private static final Logger LOG = LoggerFactory
			.getLogger(KanbanController.class);
	private static final String TODO_ONGOING = "<td>To do</td><td>Ongoing</td>";
	private TaskService taskSrv;
	private ProjectService projSrv;
	private WorkLogService wrkLogSrv;
	private MessageSource msg;
	private AgileService agileSrv;

	// private ReleaseRepository releaseRepo;

	@Autowired
	public KanbanController(TaskService taskSrv, ProjectService projSrv,
			WorkLogService wlSrv, MessageSource msg, AgileService agileSrv) {
		this.taskSrv = taskSrv;
		this.projSrv = projSrv;
		this.wrkLogSrv = wlSrv;
		this.msg = msg;
		this.agileSrv = agileSrv;
	}

	@RequestMapping(value = "{id}/kanban/board", method = RequestMethod.GET)
	public String showBoard(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canEdit(project)) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllWithoutRelease(project);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
					true));
			List<DisplayTask> resultList = taskSrv.convertToDisplay(taskList);
			model.addAttribute("tasks", resultList);
			return "/kanban/board";
		}
		return "";
	}

	@Transactional
	@RequestMapping(value = "/kanban/release", method = RequestMethod.POST)
	public String newRelease(@RequestParam(value = "id") String id,
			@RequestParam(value = "release") String releaseNo,
			@RequestParam(value = "comment", required = false) String comment,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canAdminister(project)) {
				throw new TasqAuthException(msg);
			}
			// search if name unique for project
			Release unique = agileSrv.findByProjectIdAndRelease(
					project.getId(), releaseNo);
			if (unique != null) {
				StringBuilder projectName = new StringBuilder("[");
				projectName.append(project.getProjectId());
				projectName.append("] ");
				projectName.append(project.getName());
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.release.exists", new Object[] {
								releaseNo, projectName.toString() },
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}

			List<Task> taskList = taskSrv.findAllToRelease(project);
			if (taskList.isEmpty()) {
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.newRelease.noTasks", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			Release release = new Release(project, releaseNo, comment);
			List<Release> releases = agileSrv
					.findReleaseByProjectIdOrderByDateDesc(project.getId());
			if (!releases.isEmpty()) {
				release.setStartDate(releases.get(releases.size() - 1)
						.getEndDate());
			}
			release = agileSrv.save(release);
			int count = 0;
			for (Task task : taskList) {
				task.setRelease(release);
				count++;
			}
			MessageHelper.addSuccessAttribute(
					ra,
					msg.getMessage("agile.newRelease.success", new Object[] {
							releaseNo, count }, Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "{id}/kanban/reports", method = RequestMethod.GET)
	public String showReport(
			@PathVariable String id,
			@RequestParam(value = "release", required = false) String releaseNo,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			List<Release> releases = agileSrv
					.findReleaseByProjectIdOrderByDateDesc(project.getId());
			model.addAttribute("project", project);
			model.addAttribute("releases", releases);
		}
		return "/kanban/reports";
	}

	@RequestMapping(value = "/getReleases", method = RequestMethod.GET)
	public @ResponseBody List<Release> showProjectSprints(
			@RequestParam Long projectID, HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(projectID);
		List<Release> releases = agileSrv
				.findReleaseByProjectIdOrderByDateDesc(project.getId());
		return releases;
	}

	@RequestMapping(value = "/{id}/release-data", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody KanbanData showBurndownChart(@PathVariable String id,
			@RequestParam(value = "release", required = false) String releaseNo) {
		KanbanData result = new KanbanData();
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			Release release;
			if (releaseNo == null || releaseNo == "") {
				release = agileSrv.findActiveByProjectId(project.getId());
			} else {
				release = agileSrv.findByProjectIdAndRelease(project.getId(),
						releaseNo);
			}
			List<Task> releaseTasks = taskSrv.findAllByRelease(release);
			for (Task task : releaseTasks) {
				if (task.getState().equals(TaskState.CLOSED)) {
					result.getTasks().get(SprintData.CLOSED)
							.add(new DisplayTask(task));
				} else {
					result.getTasks().get(SprintData.ALL)
							.add(new DisplayTask(task));
				}
			}
			// Fill maps based on time or story point driven board
			DateTime startTime = release.getStartDate();
			DateTime endTime = release.getEndDate();
			// TODO Active release ( release is null )
			List<WorkLog> wrkList = wrkLogSrv.getAllReleaseEvents(release);
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
			return fillOpenAndClosed(result, release, wrkList);
		} else {
			return result;
		}
	}

	private KanbanData fillOpenAndClosed(KanbanData result, Release release,
			List<WorkLog> wrkList) {
		KanbanData data = result;
		Map<LocalDate, Integer> openMap = new LinkedHashMap<LocalDate, Integer>();
		Map<LocalDate, Integer> closedMap = new LinkedHashMap<LocalDate, Integer>();
		Map<LocalDate, Integer> progressMap = new LinkedHashMap<LocalDate, Integer>();
		for (WorkLog workLog : wrkList) {
			LocalDate dateLogged = new LocalDate(workLog.getRawTime());
			//TODO refactor
			if (LogType.CREATE.equals(workLog.getType())) {
				Integer value = openMap.get(dateLogged);
				value = value == null ? 0 : value;
				value++;
				openMap.put(dateLogged, value);
			} else if (LogType.CLOSED.equals(workLog.getType())) {
				Integer value = closedMap.get(dateLogged);
				value = value == null ? 0 : value;
				value++;
				closedMap.put(dateLogged, value);
			} else if (LogType.REOPEN.equals(workLog.getType())) {
				Integer openValue = openMap.get(dateLogged);
				openValue = openValue == null ? 0 : openValue;
				openValue++;
				openMap.put(dateLogged, openValue);
				Integer closedValue = closedMap.get(dateLogged);
				closedValue = closedValue == null ? 0 : closedValue;
				closedValue--;
				closedMap.put(dateLogged, closedValue);
			} else if (LogType.STATUS.equals(workLog.getType())) {
				Integer value = progressMap.get(dateLogged);
				value = value == null ? 0 : value;
				// TODO search for other way ?
				if (workLog.getMessage().contains(TODO_ONGOING)) {
					value++;
					progressMap.put(dateLogged, value);
				}
			}
		}
		LocalDate startTime = new LocalDate(release.getStartDate());
		LocalDate endTime = new LocalDate(release.getEndDate());
		data.setStartStop(startTime.toString() + " - " + endTime.toString());
		int releaseDays = Days.daysBetween(startTime, endTime).getDays() + 1;
		Integer open = new Integer(0);
		Integer closed = new Integer(0);
		for (int i = 0; i < releaseDays; i++) {
			LocalDate date = startTime.plusDays(i);
			Integer openValue = openMap.get(date);
			Integer closedValue = closedMap.get(date);
			Integer progressValue = progressMap.get(date);
			openValue = openValue == null ? 0 : openValue;
			closedValue = closedValue == null ? 0 : closedValue;
			progressValue = progressValue == null ? 0 : progressValue;
			open += openValue;
			closed += closedValue;
			progressValue += closed;
			data.putToOpen(date.toString(), open);
			data.putToClosed(date.toString(), closed);
			data.putToInProgress(date.toString(), progressValue);
		}

		// Integer totalPoints = new Integer(0);
		//
		return data;
	}
}
