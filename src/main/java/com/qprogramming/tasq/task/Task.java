package com.qprogramming.tasq.task;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.task.worklog.WorkLog;

@Entity
public class Task {

	@Id
	private String id;

	@Column
	private String name;

	@Column(length = 4000)
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

	public String getId() {
		return id;
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

	public void setId(String id) {
		this.id = id;
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

	public void addWorkLog(WorkLog wl) {
		if (worklog == null) {
			worklog = new LinkedList<WorkLog>();
		}
		worklog.add(wl);
	}

}
