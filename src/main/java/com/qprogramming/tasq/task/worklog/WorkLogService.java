/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.events.EventsService;

/**
 * @author romanjak
 * @date 28 maj 2014
 */
@Service
public class WorkLogService {

	@Autowired
	private WorkLogRepository wlRepo;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private ProjectService projSrv;

	@Autowired
	private EventsService eventSrv;

	@Transactional
	public void addTimedWorkLog(Task task, String msg, Date when,
			Period remaining, Period activity, LogType type) {
		Task loggedTask = taskSrv.findById(task.getId());
		if (loggedTask != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(loggedTask);
			wl.setProject_id(loggedTask.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTimeLogged(new Date());
			wl.setTime(when);
			wl.setType(type);
			wl.setMessage(msg);
			wl = wlRepo.save(wl);
			wl.setActivity(activity);
			Hibernate.initialize(loggedTask.getWorklog());
			loggedTask.addWorkLog(wl);
			if (remaining == null) {
				loggedTask.reduceRemaining(activity);
			} else {
				loggedTask.setRemaining(remaining);
			}
			loggedTask.addLoggedWork(activity);
			taskSrv.save(checkState(loggedTask));
			eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), when);
		}
	}

	@Transactional
	public void addDatedWorkLog(Task task, String msg, Date when, LogType type) {
		Task loggedTask = taskSrv.findById(task.getId());
		if (loggedTask != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(loggedTask);
			wl.setProject_id(loggedTask.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTimeLogged(new Date());
			wl.setTime(when);
			wl.setType(type);
			wl.setMessage(msg);
			wl = wlRepo.save(wl);
			Hibernate.initialize(loggedTask.getWorklog());
			loggedTask.addWorkLog(wl);
			taskSrv.save(loggedTask);
			eventSrv.addWatchEvent(wl, msg, when);
		}
	}

	/**
	 * @param task
	 * @param string
	 * @param status
	 */
	@Transactional
	public void addActivityLog(Task task, String msg, LogType type) {
		Task loggedTask = taskSrv.findById(task.getId());
		if (loggedTask != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(loggedTask);
			wl.setProject_id(loggedTask.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTime(new Date());
			wl.setTimeLogged(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl = wlRepo.save(wl);
			Hibernate.initialize(loggedTask.getWorklog());
			loggedTask.addWorkLog(wl);
			taskSrv.save(task);
			eventSrv.addWatchEvent(wl, msg, new Date());
		}
	}

	@Transactional
	public void addActivityPeriodLog(Task task, String msg, Period activity,
			LogType type) {
		Task loggedTask = taskSrv.findById(task.getId());
		if (loggedTask != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(loggedTask);
			wl.setProject_id(loggedTask.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTime(new Date());
			wl.setTimeLogged(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl.setActivity(activity);
			wl = wlRepo.save(wl);
			Hibernate.initialize(loggedTask.getWorklog());
			loggedTask.addWorkLog(wl);
			taskSrv.save(loggedTask);
			eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), new Date());
		}
	}

	/**
	 * @param task
	 * @param outFormat
	 * @param log_work
	 * @param log
	 */
	@Transactional
	public void addNormalWorkLog(Task task, String msg, Period activity,
			LogType type) {
		Task loggedTask = taskSrv.findById(task.getId());
		if (loggedTask != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(loggedTask);
			wl.setProject_id(loggedTask.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTimeLogged(new Date());
			wl.setTime(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl.setActivity(activity);
			wl = wlRepo.save(wl);
			Hibernate.initialize(loggedTask.getWorklog());
			loggedTask.addWorkLog(wl);
			loggedTask.reduceRemaining(activity);
			loggedTask.addLoggedWork(activity);
			if (!type.equals(LogType.ESTIMATE)) {
				taskSrv.save(checkState(loggedTask));
			} else {
				taskSrv.save(loggedTask);
			}
			eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), new Date());
		}
	}

	/**
	 * Add worklog without task ( for example for project only )
	 * 
	 * @param task
	 * @param outFormat
	 * @param log_work
	 * @param log
	 */
	public void addWorkLogNoTask(String msg, Project project, LogType type) {
		WorkLog wl = new WorkLog();
		wl.setProject_id(project.getId());
		wl.setAccount(Utils.getCurrentAccount());
		wl.setTimeLogged(new Date());
		wl.setTime(new Date());
		wl.setType(type);
		wl.setMessage(msg);
		wl = wlRepo.save(wl);
	}

	public List<DisplayWorkLog> getProjectEvents(Project project) {
		List<WorkLog> workLogs = wlRepo.findByProjectId(project.getId());
		Collections.sort(workLogs, new WorkLogSorter(true));
		return packIntoDisplay(workLogs);
	}

	/**
	 * Returns all sprint events. If timeTracked will be set to true, only
	 * events with logged activity ( time ) will be returned, otherwise only
	 * events which were closing task will be returned
	 * 
	 * @param sprint
	 *            sprint for which all events must be fetched
	 * @param timeTracked
	 *            if true, only events with logged activity ( time )
	 * @return
	 */
	public List<WorkLog> getSprintEvents(Sprint sprint, boolean timeTracked) {
		LocalDate start = new LocalDate(sprint.getRawStart_date()).minusDays(1);
		LocalDate end = new LocalDate(sprint.getRawEnd_date()).plusDays(1);

		if (timeTracked) {
			return wlRepo
					.findByProjectIdAndTimeBetweenAndActivityNotNullOrderByTimeAsc(
							sprint.getProject().getId(), start.toDate(),
							end.toDate());
		} else {
			List<WorkLog> list = wlRepo
					.findByProjectIdAndTimeBetweenOrderByTimeAsc(sprint
							.getProject().getId(), start.toDate(), end.toDate());
			List<WorkLog> result = new LinkedList<WorkLog>();
			for (WorkLog workLog : list) {
				if (LogType.CLOSED.equals(workLog.getType())
						|| LogType.REOPEN.equals(workLog.getType())
						|| LogType.TASKSPRINTADD.equals(workLog.getType())
						|| LogType.TASKSPRINTREMOVE.equals(workLog.getType())) {
					result.add(workLog);
				}
			}
			return result;
		}
	}

	@Transactional
	public List<WorkLog> getAllSprintEvents(Sprint sprint) {
		DateTime start = new DateTime(sprint.getRawStart_date());
		DateTime end = new DateTime(sprint.getRawEnd_date()).plusDays(1);
		List<WorkLog> list = wlRepo
				.findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
						sprint.getProject().getId(), start.toDate(),
						end.toDate());
		List<WorkLog> result = new LinkedList<WorkLog>();
		// Filterout not important events
		for (WorkLog workLog : list) {
			if (isSprintRelevant(workLog, sprint)) {
				result.add(workLog);
			}
		}

		return result;
	}

	public Page<WorkLog> findByProjectId(Long id, Pageable p) {
		return wlRepo.findByProjectId(id, p);
	}

	public List<WorkLog> findProjectCreateCloseEvents(Project project) {
		List<WorkLog> list = wlRepo.findByProjectIdOrderByTimeAsc(project
				.getId());
		List<WorkLog> result = new LinkedList<WorkLog>();
		for (WorkLog workLog : list) {
			if (LogType.CREATE.equals(workLog.getType())
					|| LogType.REOPEN.equals(workLog.getType())
					|| LogType.CLOSED.equals(workLog.getType())) {
				result.add(workLog);
			}
		}
		return result;
	}

	private Task checkState(Task task) {
		if (task.getState().equals(TaskState.TO_DO)) {
			task.setState(TaskState.ONGOING);
		}
		return task;
	}

	private List<DisplayWorkLog> packIntoDisplay(List<WorkLog> list) {
		List<DisplayWorkLog> result = new LinkedList<DisplayWorkLog>();
		for (WorkLog workLog : list) {
			result.add(new DisplayWorkLog(workLog));
		}
		return result;
	}

	private boolean isSprintRelevant(WorkLog workLog, Sprint sprint) {
		LogType type = (LogType) workLog.getType();
		return workLog.getTask().inSprint(sprint)
				&& (type.equals(LogType.DELETED) || type.equals(LogType.LOG)
						|| type.equals(LogType.TASKSPRINTREMOVE)
						|| type.equals(LogType.TASKSPRINTADD)
						|| type.equals(LogType.ESTIMATE)
						|| type.equals(LogType.CLOSED) || type
							.equals(LogType.REOPEN));
	}
}
