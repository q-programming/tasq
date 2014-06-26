/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;

/**
 * @author romanjak
 * @date 28 maj 2014
 */
@Service
public class WorkLogService {

	@Autowired
	WorkLogRepository wlRepo;

	@Autowired
	TaskService taskSrv;

	@Autowired
	ProjectService projSrv;

	@Transactional
	public void addTimedWorkLog(Task task, String msg, Date when,
			Period remaining, Period activity, LogType type) {
		task = taskSrv.findById(task.getId());
		if (task != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(task);
			wl.setProject_id(task.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTimeLogged(new Date());
			wl.setTime(when);
			wl.setType(type);
			wl.setMessage(msg);
			wl.setActivity(activity);
			wl = wlRepo.save(wl);
			Hibernate.initialize(task.getWorklog());
			task.addWorkLog(wl);
			if (remaining == null) {
				task.reduceRemaining(activity);
			} else {
				task.setRemaining(remaining);
			}
			taskSrv.save(checkState(task));
		}
	}

	/**
	 * @param task
	 */
	public void findAllByTask(Task task) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param task
	 * @param string
	 * @param status
	 */
	@Transactional
	public void addActivityLog(Task task, String msg, LogType type) {
		task = taskSrv.findById(task.getId());
		if (task != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(task);
			wl.setProject_id(task.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTime(new Date());
			wl.setTimeLogged(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl = wlRepo.save(wl);
			Hibernate.initialize(task.getWorklog());
			task.addWorkLog(wl);
			taskSrv.save(task);
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
		task = taskSrv.findById(task.getId());
		if (task != null) {
			WorkLog wl = new WorkLog();
			wl.setTask(task);
			wl.setProject_id(task.getProject().getId());
			wl.setAccount(Utils.getCurrentAccount());
			wl.setTimeLogged(new Date());
			wl.setTime(new Date());
			wl.setType(type);
			wl.setMessage(msg);
			wl.setActivity(activity);
			wl = wlRepo.save(wl);
			Hibernate.initialize(task.getWorklog());
			task.addWorkLog(wl);
			task.reduceRemaining(activity);
			taskSrv.save(checkState(task));
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

	public List<WorkLog> getProjectEvents(Project project) {
		return wlRepo.findByProjectId(project.getId());
	}

	private Task checkState(Task task) {
		if (task.getState().equals(TaskState.TO_DO)) {
			task.setState(TaskState.ONGOING);
		}
		return task;
	}

}
