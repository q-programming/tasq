package com.qprogramming.tasq.task.worklog;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.Period;
import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.task.DisplayTask;

public class DisplayWorkLog {

	private Long id;
	private String time;
	private String timeLogged;
	private DisplayAccount account;
	private Enum<LogType> type;
	private Period activity;
	private String message;
	private Long projectId;
	private DisplayTask task;

	public DisplayWorkLog() {
		// TODO Auto-generated constructor stub
	}

	public DisplayWorkLog(WorkLog wl) {
		BeanUtils.copyProperties(wl, this);
		setAccount(new DisplayAccount(wl.getAccount()));
		if (wl.getTask() != null) {
			setTask(new DisplayTask(wl.getTask()));
		}
	}

	public Long getId() {
		return id;
	}

	public String getTime() {
		return time;
	}

	public String getTimeLogged() {
		return timeLogged;
	}

	public DisplayAccount getAccount() {
		return account;
	}

	public Enum<LogType> getType() {
		return type;
	}

	public Period getActivity() {
		return activity;
	}

	public String getMessage() {
		return message;
	}

	public Long getProjectId() {
		return projectId;
	}

	public DisplayTask getTask() {
		return task;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setTimeLogged(String timeLogged) {
		this.timeLogged = timeLogged;
	}

	public void setAccount(DisplayAccount account) {
		this.account = account;
	}

	public void setType(Enum<LogType> type) {
		this.type = type;
	}

	public void setActivity(Period activity) {
		this.activity = activity;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setTask(DisplayTask task) {
		this.task = task;
	}

	/**
	 * Static method to return list of more lightweight form of WorkLog
	 * 
	 * @param list
	 * @return
	 */
	public static List<DisplayWorkLog> convertToDisplayWorkLogs(
			List<WorkLog> list) {
		List<DisplayWorkLog> result = new LinkedList<DisplayWorkLog>();
		for (WorkLog workLog : list) {
			result.add(new DisplayWorkLog(workLog));
		}
		return result;
	}

}
