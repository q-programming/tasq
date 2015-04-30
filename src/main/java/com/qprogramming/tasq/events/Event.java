package com.qprogramming.tasq.events;

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
import com.qprogramming.tasq.support.Utils;
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
	
	@Column
	private String who;

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

	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
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

	public String getDate() {
		return Utils.convertDateTimeToString(date);
	}
	
	public Date getRawDate() {
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
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((logtype == null) ? 0 : logtype.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((who == null) ? 0 : who.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Event other = (Event) obj;
		if (account == null) {
			if (other.account != null) {
				return false;
			}
		} else if (!account.equals(other.account)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (logtype != other.logtype) {
			return false;
		}
		if (task == null) {
			if (other.task != null) {
				return false;
			}
		} else if (!task.equals(other.task)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (who == null) {
			if (other.who != null) {
				return false;
			}
		} else if (!who.equals(other.who)) {
			return false;
		}
		return true;
	};
}
