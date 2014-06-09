package com.qprogramming.tasq.projects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.Task;

@Entity
public class Project implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 143468447150832121L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_seq_gen")
	@SequenceGenerator(name = "project_seq_gen", sequenceName = "project_id_seq", allocationSize = 1)
	private Long id;

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
	
	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy = "project")
	private List<Task> tasks;

	public Project() {
		// TODO Auto-generated constructor stub
	}

	public Project(String name, Account administrator) {
		setName(name);
		setAdministrator(administrator);
		addParticipant(administrator);
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
	public void addParticipant(Account account){
		if (participants == null){
			participants = new LinkedList<Account>();
		}
		participants.add(account);
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

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
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
