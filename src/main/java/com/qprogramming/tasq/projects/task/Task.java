package com.qprogramming.tasq.projects.task;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

	@Column
	@ElementCollection(targetClass = Date.class)
	private List<Date> worklog;

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

	public List<Date> getWorklog() {
		return worklog;
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

	public void setWorklog(List<Date> worklog) {
		this.worklog = worklog;
	}

}
