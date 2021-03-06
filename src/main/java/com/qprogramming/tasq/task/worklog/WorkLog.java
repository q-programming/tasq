/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.joda.time.Period;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Entity
public class WorkLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5421564881978300937L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "worklog_seq_gen")
	@SequenceGenerator(name = "worklog_seq_gen", sequenceName = "worklog_id_seq", allocationSize = 1)
	private Long id;

	@Column
	private Date time;

	@Column
	private Date timeLogged;

	@ManyToOne
	private Account account;

	@Column
	private Enum<LogType> type;

	@Column
	private Period activity;

	@Column(columnDefinition = "text")
	private String message;

	@Column
	private Long projectId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worklogtask")
	private Task worklogtask;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTime() {
		return Utils.convertDateTimeToString(time);
	}

	/**
	 * When worklog occured
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * When worklog occured
	 */
	public Date getRawTime() {
		return time;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Period getActivity() {
		return activity;
	}

	public void setActivity(Period activity) {
		this.activity = activity;
	}

	public String getTimeLogged() {
		return Utils.convertDateTimeToString(timeLogged);
	}

	public Date getRawTimeLogged() {
		return timeLogged;
	}

	public void setTimeLogged(Date timeLogged) {
		this.timeLogged = timeLogged;
	}

	public Task getTask() {
		return worklogtask;
	}

	public void setTask(Task task) {
		this.worklogtask = task;
	}

	public Enum<LogType> getType() {
		return type;
	}

	public void setType(Enum<LogType> type) {
		this.type = type;
	}

	public Long getProject_id() {
		return projectId;
	}

	public void setProject_id(Long project_id) {
		this.projectId = project_id;
	}

	public String getFormatedActivity() {
		return PeriodHelper.outFormat(activity);
	}

	public Long getProjectId() {
		return projectId;
	}

	public Task getWorklogtask() {
		return worklogtask;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public void setWorklogtask(Task worklogtask) {
		this.worklogtask = worklogtask;
	}

	@Override
	public String toString() {
		return time + " " + worklogtask.getId() + " " + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		WorkLog other = (WorkLog) obj;
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
		if (time == null) {
			if (other.time != null) {
				return false;
			}
		} else if (!time.equals(other.time)) {
			return false;
		}
		return type == other.type;
	}
}
