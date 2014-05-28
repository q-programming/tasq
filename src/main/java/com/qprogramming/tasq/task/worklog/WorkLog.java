/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.qprogramming.tasq.account.Account;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Entity
public class WorkLog {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private Date time;

	@ManyToOne
	private Account account;

	@Enumerated(EnumType.STRING)
	private LogType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");
		return sdf.format(time);
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}
}
