/**
 * 
 */
package com.qprogramming.tasq.task.worklog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.qprogramming.tasq.account.Account;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Entity
public class WorkLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5421564881978300937L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="generatorName")  
	@TableGenerator(name="generatorName", allocationSize=1)  
	private Long id;

	@Column
	private Date time;

	@ManyToOne
	private Account account;

	@Enumerated(EnumType.STRING)
	private LogType type;
	
	@Column
	private Period activity;
	
	@Column
	private String message;

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
	
	public Date getRawTime(){
		return time;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WorkLog other = (WorkLog) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
