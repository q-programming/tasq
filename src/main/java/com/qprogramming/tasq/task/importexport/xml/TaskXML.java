package com.qprogramming.tasq.task.importexport.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskType;

@XmlRootElement(namespace = "com.qprogramming.tasq.task.importexport.xml.ProjectXML")
@XmlType(propOrder = { "name", "description", "type", "priority", "estimate",
		"story_points", "due_date" })
public class TaskXML {
	private Integer number;
	private String name;
	private String description;
	private String type;
	private String priority;
	private String estimate;
	private String story_points;
	private Date due_date;

	public TaskXML() {
	}

	public TaskXML(Task task) {
		BeanUtils.copyProperties(task, this);
		this.priority = ((TaskPriority) task.getPriority()).getEnum();
		this.type = ((TaskType) task.getType()).getEnum();
		this.story_points = Integer.toString(task.getStory_points());
		this.due_date = task.getRawDue_date();
	}

	@XmlAttribute
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
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

	public Date getDue_date() {
		return due_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

}
