package com.qprogramming.tasq.projects;

import org.hibernate.validator.constraints.NotBlank;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskType;

public class NewProjectForm {
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String ID_NOT_VALID = "{project.idValid}";

	private Long id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String name;

	@NotBlank(message = ID_NOT_VALID)
	private String project_id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String description;
	
	private String agile;

	public NewProjectForm() {
		// TODO Auto-generated constructor stub
	}

	public NewProjectForm(Project project) {
		setName(project.getName());
		setDescription(project.getDescription());
		setProject_id(project.getProjectId());
	}

	public Project createProject() {
		Project project = new Project(getName(), Utils.getCurrentAccount());
		project.setDescription(getDescription());
		project.setProjectId(getProject_id().toUpperCase());
		project.setDefault_priority(TaskPriority.MAJOR);
		project.setDefault_type(TaskType.TASK);
		project.setAgile(agile);
		return project;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getAgile() {
		return agile;
	}

	public void setAgile(String agile_type) {
		this.agile = agile_type;
	}

}
