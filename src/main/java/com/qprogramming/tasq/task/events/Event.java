package com.qprogramming.tasq.task.events;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.worklog.LogType;

@Entity
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq_gen")
	@SequenceGenerator(name = "event_seq_gen", sequenceName = "event_id_seq", allocationSize = 1)
	private Long id;

	@Column
	private String task;
	
	@ManyToOne
	private Account account;

	@Enumerated(EnumType.STRING)
	private LogType logtype;

	@Column(length = 4000)
	private String message;

	@Column
	private Date date;

	@Column
	private boolean unread;

	@Enumerated(EnumType.STRING)
	private Type type;

	public enum Type {
		COMMENT, WATCH, SYSTEM
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

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public LogType getLogtype() {
		return logtype;
	}

	public void setLogtype(LogType logtype) {
		this.logtype = logtype;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
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
	};
}
