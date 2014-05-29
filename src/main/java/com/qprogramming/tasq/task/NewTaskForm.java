/**
 * 
 */
package com.qprogramming.tasq.task;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.qprogramming.tasq.support.PeriodHelper;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
public class NewTaskForm {
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";

	private Long id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String name;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String project;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String description;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String type;

	private String estimate;

	private String story_points;

	public NewTaskForm() {
		// TODO Auto-generated constructor stub
	}

	public NewTaskForm(Task task) {
		setName(task.getName());
		setDescription(task.getDescription());
	}

	public Task createTask() throws IllegalArgumentException {
		Task task = new Task();
		task.setName(getName());
		task.setCreate_date(new Date());
		task.setDescription(getDescription());
		if (!"".equals(getStory_points())) {
			task.setStory_points(Integer.parseInt(getStory_points()));
		}
		Period p = PeriodHelper.inFormat(getEstimate());
		task.setEstimate(p);
		task.setLogged_work(PeriodHelper.inFormat(""));
		return task;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEstimate() {
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}

	public String getStory_points() {
		return story_points;
	}

	public void setStory_points(String story_points) {
		this.story_points = story_points;
	}
}
