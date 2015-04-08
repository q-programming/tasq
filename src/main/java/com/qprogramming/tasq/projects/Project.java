package com.qprogramming.tasq.projects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.IndexColumn;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskType;

@Entity
public class Project implements Serializable {

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

	@ManyToMany(fetch = FetchType.EAGER)
	@IndexColumn(name = "INDEX_COL")
	@JoinTable(name = "projects_admins")
	private Set<Account> administrators;

	@ManyToMany(fetch = FetchType.EAGER)
	@IndexColumn(name = "INDEX_COL")
	@JoinTable(name = "projects_participants")
	private Set<Account> participants;

	@Column
	private Date startDate;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "project")
	private List<Task> tasks;

	@Column
	private Enum<TaskType> default_type;

	@Column
	private Enum<TaskPriority> default_priority;

	@Enumerated(EnumType.STRING)
	private AgileType agile;

	@Column
	private Boolean timeTracked = false;

	@Column
	private Long defaultAssigneeID;
	
	@Column
	private Long lastTaskNo;

	public enum AgileType {
		KANBAN, SCRUM
	}

	public Project() {
	}

	public Project(String name, Account administrator) {
		setName(name);
		addAdministrator(administrator);
		addParticipant(administrator);
		setStartDate(new Date());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Account> getParticipants() {
		return participants;
	}

	public String getStartDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return sdf.format(startDate);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParticipants(Set<Account> participants) {
		this.participants = participants;
	}

	public void addParticipant(Account account) {
		if (participants == null) {
			participants = new HashSet<Account>();
		}
		participants.add(account);
	}

	public void removeParticipant(Account account) {
		if (participants != null) {
			participants.remove(account);
		}
	}

	public Set<Account> getAdministrators() {
		return administrators;
	}

	public void setAdministrators(Set<Account> administrators) {
		this.administrators = administrators;
	}

	public void addAdministrator(Account account) {
		if (administrators == null) {
			administrators = new HashSet<Account>();
		}
		administrators.add(account);
	}

	public void removeAdministrator(Account account) {
		if (administrators != null) {
			administrators.remove(account);
		}
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public List<Task> getTasks() {
		return tasks == null ? new ArrayList<Task>() : tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Enum<TaskType> getDefault_type() {
		return default_type;
	}

	public void setDefault_type(Enum<TaskType> default_type) {
		this.default_type = default_type;
	}

	public Enum<TaskPriority> getDefault_priority() {
		return default_priority;
	}

	public void setDefault_priority(Enum<TaskPriority> default_priority) {
		this.default_priority = default_priority;
	}

	public AgileType getAgile() {
		return agile;
	}

	public void setAgile(String agile) {
		this.agile = AgileType.valueOf(agile);
	}

	public Boolean getTimeTracked() {
		return timeTracked;
	}

	public void setTimeTracked(Boolean timeBurndown) {
		this.timeTracked = timeBurndown;
	}

	public Long getDefaultAssigneeID() {
		return defaultAssigneeID;
	}

	public void setDefaultAssigneeID(Long defaultAssigneeID) {
		this.defaultAssigneeID = defaultAssigneeID;
	}

	public Long getLastTaskNo() {
		return lastTaskNo==null?0:lastTaskNo;
	}

	public void setLastTaskNo(Long lastTaskNo) {
		this.lastTaskNo = lastTaskNo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agile == null) ? 0 : agile.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
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
		Project other = (Project) obj;
		if (agile != other.agile) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (projectId == null) {
			if (other.projectId != null) {
				return false;
			}
		} else if (!projectId.equals(other.projectId)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		return true;
	}
	
}
