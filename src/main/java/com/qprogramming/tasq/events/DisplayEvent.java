package com.qprogramming.tasq.events;

import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.events.Event.Type;
import com.qprogramming.tasq.task.worklog.LogType;

public class DisplayEvent {

	private Long id;
	private String task;
	private LogType logtype;
	private String who;
	private String message;
	private String date;
	private boolean unread;
	private Type type;

	public DisplayEvent(Event event) {
		BeanUtils.copyProperties(event, this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public LogType getLogtype() {
		return logtype;
	}

	public void setLogtype(LogType logtype) {
		this.logtype = logtype;
	}

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
