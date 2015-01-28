/**
 * 
 */
package com.qprogramming.tasq.task;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
public class TaskForm {
	private static final Logger LOG = LoggerFactory.getLogger(TaskForm.class);

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String TYPE_NOT_BLANK_MESSAGE = "{error.taskType}";

	private String id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String name;

	private Long project;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	private String description;

	@NotBlank(message = TYPE_NOT_BLANK_MESSAGE)
	private String type;

	private String priority;

	private String estimate;

	private String story_points;

	private String no_estimation;

	private String remaining;

	private String due_date;
	
	private Long addToSprint;
	
	private Long assignee;

	public TaskForm() {
		// TODO Auto-generated constructor stub
	}

	public TaskForm(Task task) {
		setName(task.getName());
		setProject(task.getProject().getId());
		setDescription(task.getDescription());
		setNo_estimation(task.getEstimated().toString());
		setEstimate(task.getEstimate());
		setStory_points(task.getStory_points() != null ? task.getStory_points()
				.toString() : "");
		setType(((TaskType) task.getType()).getEnum());
		setId(task.getId());
		setRemaining(task.getRemaining());
		setDue_date(task.getDue_date());
		setPriority(task.getPriority().toString().toUpperCase());
	}

	public Task createTask() {
		Task task = new Task();
		task = createBaseTask(task);
		if (getStory_points() != null && !"".equals(getStory_points())) {
			task.setStory_points(Integer.parseInt(getStory_points()));
		} else {
			task.setStory_points(0);
		}
		task.setType(TaskType.toType(getType()));
		return task;
	}

	public Task createSubTask() {
		Task subTask = new Task();
		subTask = createBaseTask(subTask);
		subTask.setType(TaskType.toType(getType()));
		return subTask;
	}
	

	private Task createBaseTask(Task task) {
		task.setName(getName());
		task.setCreate_date(new Date());
		if (getDue_date() != null && !"".equals(getDue_date())) {
			task.setDue_date(Utils.convertStringToDate(getDue_date()));
		}
		task.setDescription(getDescription());
		task.setState(TaskState.TO_DO);
		boolean estimated = !Boolean.parseBoolean(getNo_estimation());
		Period p = PeriodHelper.inFormat(getEstimate());
		task.setEstimate(p);
		task.setRemaining(p);
		task.setEstimated(estimated);
		task.setLoggedWork(PeriodHelper.inFormat(""));
		task.setOwner(Utils.getCurrentAccount());
		task.setPriority(TaskPriority.toPriority(getPriority()));
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

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
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

	public void setNumericStory_points(Double story_points) {
		this.story_points = String.valueOf(story_points.intValue());
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

	public String getDue_date() {
		return due_date;
	}

	public void setDue_date(String due_date) {
		this.due_date = due_date;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Long getAddToSprint() {
		return addToSprint;
	}

	public void setAddToSprint(Long addToSprint) {
		this.addToSprint = addToSprint;
	}

	public Long getAssignee() {
		return assignee;
	}

	public void setAssignee(Long assignee) {
		this.assignee = assignee;
	}
}
