package com.qprogramming.tasq.projects;

import java.util.Date;

import org.springframework.beans.BeanUtils;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.projects.Project.AgileType;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskType;

public class DisplayProject {

	private Long id;
	private String projectId;
	private String name;
	private String description;
	private Date startDate;
	private Enum<TaskType> default_type;
	private Enum<TaskPriority> default_priority;
	private AgileType agile;
	private Boolean timeTracked = false;
	private Long defaultAssigneeID;
	private DisplayAccount defaultAssignee;
	private Long lastTaskNo;

	public DisplayProject(Project project) {
		BeanUtils.copyProperties(project, this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public void setAgile(AgileType agile) {
		this.agile = agile;
	}

	public Boolean getTimeTracked() {
		return timeTracked;
	}

	public void setTimeTracked(Boolean timeTracked) {
		this.timeTracked = timeTracked;
	}

	public Long getDefaultAssigneeID() {
		return defaultAssigneeID;
	}

	public void setDefaultAssigneeID(Long defaultAssigneeID) {
		this.defaultAssigneeID = defaultAssigneeID;
	}

	public Long getLastTaskNo() {
		return lastTaskNo;
	}

	public void setLastTaskNo(Long lastTaskNo) {
		this.lastTaskNo = lastTaskNo;
	}

	public DisplayAccount getDefaultAssignee() {
		return defaultAssignee;
	}

	public void setDefaultAssignee(DisplayAccount defaultAssignee) {
		this.defaultAssignee = defaultAssignee;
	}

}
