package com.qprogramming.tasq.task;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.projects.Project;

@Entity
public class Task {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String task_id;

	@Column
	private String name;

	@Column
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	private Project project;

	@ManyToOne
	private Account project_admin;

	@Column
	private Date create_date;

	@Column
	private Date last_visit;

	@Column
	private boolean active = false;

	@Column
	private Date start_date;

	@Column
	private Date due_date;

	@Column
	private Integer story_points;

	@Column
	private Long estimate;

	@OneToMany(fetch = FetchType.EAGER)
	private List<WorkLog> worklog;

	public Long getId() {
		return id;
	}

	public String getTask_id() {
		return task_id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Project getProject() {
		return project;
	}

	public Account getProject_admin() {
		return project_admin;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public Date getLast_visit() {
		return last_visit;
	}

	public boolean isActive() {
		return active;
	}

	public Date getStart_date() {
		return start_date;
	}

	public Date getDue_date() {
		return due_date;
	}

	public Integer getStory_points() {
		return story_points;
	}

	public Long getEstimate() {
		return estimate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setProject_admin(Account project_admin) {
		this.project_admin = project_admin;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public void setLast_visit(Date last_visit) {
		this.last_visit = last_visit;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public void setStory_points(Integer story_points) {
		this.story_points = story_points;
	}

	public void setEstimate(Long estimate) {
		this.estimate = estimate;
	}

	public List<WorkLog> getWorklog() {
		return worklog;
	}

	public void setWorklog(List<WorkLog> worklog) {
		this.worklog = worklog;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((task_id == null) ? 0 : task_id.hashCode());
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
		Task other = (Task) obj;
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
		if (task_id == null) {
			if (other.task_id != null)
				return false;
		} else if (!task_id.equals(other.task_id))
			return false;
		return true;
	}

}
