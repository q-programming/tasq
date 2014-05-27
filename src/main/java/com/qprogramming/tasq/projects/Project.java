package com.qprogramming.tasq.projects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.TableGenerator;

import com.qprogramming.tasq.account.Account;

@Entity
public class Project {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="generatorName")  
	@TableGenerator(name="generatorName", allocationSize=1)  
	private Long id;

	@Column
	private Long task_count = 0L;

	@Column(unique = true)
	private String projectId;

	@Column(unique = true)
	private String name;

	@Column(length = 4000)
	private String description;

	@ManyToOne
	private Account administrator;

	@ManyToMany
	private List<Account> participants;

	@Column
	private Date startDate;

	@Column
	private Date lastVisit;

	@Column
	private boolean active = false;

	public Project() {
		// TODO Auto-generated constructor stub
	}

	public Project(String name, Account administrator) {
		setName(name);
		setAdministrator(administrator);
		setStartDate(new Date());
		setLastVisit(new Date());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Account getAdministrator() {
		return administrator;
	}

	public List<Account> getParticipants() {
		return participants;
	}

	public String getStartDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");
		return sdf.format(startDate);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAdministrator(Account administrator) {
		this.administrator = administrator;
	}

	public void setParticipants(List<Account> participants) {
		this.participants = participants;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getTask_count() {
		return task_count;
	}

	public void setTask_count(Long task_count) {
		this.task_count = task_count;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
