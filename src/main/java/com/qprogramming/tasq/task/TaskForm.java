/**
 * 
 */
package com.qprogramming.tasq.task;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.Period;

import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
public class TaskForm {
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String TYPE_NOT_BLANK_MESSAGE = "{error.taskType}";

	private String id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String name;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String project;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String description;

	@NotBlank(message = TYPE_NOT_BLANK_MESSAGE)
	private String type;

	private String estimate;

	private String story_points;

	private String no_estimation;
	
	private String remaining;

	public TaskForm() {
		// TODO Auto-generated constructor stub
	}

	public TaskForm(Task task) {
		setName(task.getName());
		setProject(task.getProject().toString());
		setDescription(task.getDescription());
		setNo_estimation(task.getEstimated().toString());
		setEstimate(task.getEstimate());
		setStory_points(task.getStory_points() != null ? task.getStory_points()
				.toString() : "");
		setType(((TaskType) task.getType()).getEnum());
		setId(task.getId());
		setRemaining(task.getRemaining());
	}

	public Task createTask() throws IllegalArgumentException {
		Task task = new Task();
		task.setName(getName());
		task.setCreate_date(new Date());
		task.setDescription(getDescription());
		task.setState(TaskState.TO_DO);
		task.setType(TaskType.valueOf(getType()));
		boolean estimated = !Boolean.parseBoolean(getNo_estimation());
		if (!"".equals(getStory_points())) {
			task.setStory_points(Integer.parseInt(getStory_points()));
		} else {
			task.setStory_points(0);
		}
		Period p = PeriodHelper.inFormat(getEstimate());
		task.setEstimate(p);
		task.setRemaining(p);
		task.setEstimated(estimated);
		task.setLogged_work(PeriodHelper.inFormat(""));
		task.setOwner(Utils.getCurrentAccount());
		return task;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getNo_estimation() {
		return no_estimation;
	}

	public void setNo_estimation(String no_estimation) {
		this.no_estimation = no_estimation;
	}

	public String getRemaining() {
		return remaining;
	}

	public void setRemaining(String remaining) {
		this.remaining = remaining;
	}
}
