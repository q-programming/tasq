package com.qprogramming.tasq.task;

import org.joda.time.Period;
import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.account.Account;

public class DisplayTask implements Comparable<DisplayTask>{
	private String id;
	private String name;
	private String projectID;
	private Integer story_points;
	private Period estimate;
	private Period remaining;
	private Period logged_work;
	private Enum<TaskState> state;
	private Enum<TaskType> type;
	private Enum<TaskPriority> priority;
	private Account assignee;
	private Boolean estimated = false;


	public Account getAssignee() {
		return assignee;
	}

	public void setAssignee(Account assignee) {
		this.assignee = assignee;
	}

	public DisplayTask(Task task) {
		BeanUtils.copyProperties(task, this);
		projectID = task.getProject().getProjectId();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProjectID() {
		return projectID;
	}

	public Integer getStory_points() {
		return story_points;
	}

	public Period getEstimate() {
		return estimate;
	}

	public Period getRemaining() {
		return remaining;
	}

	public Period getLogged_work() {
		return logged_work;
	}

	public Enum<TaskState> getState() {
		return state;
	}

	public Enum<TaskType> getType() {
		return type;
	}

	public Enum<TaskPriority> getPriority() {
		return priority;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public void setStory_points(Integer story_points) {
		this.story_points = story_points;
	}

	public void setEstimate(Period estimate) {
		this.estimate = estimate;
	}

	public void setRemaining(Period remaining) {
		this.remaining = remaining;
	}

	public void setLogged_work(Period logged_work) {
		this.logged_work = logged_work;
	}

	public void setState(Enum<TaskState> state) {
		this.state = state;
	}

	public void setType(Enum<TaskType> type) {
		this.type = type;
	}

	public void setPriority(Enum<TaskPriority> priority) {
		this.priority = priority;
	}

	public Boolean getEstimated() {
		return estimated;
	}

	public void setEstimated(Boolean estimated) {
		this.estimated = estimated;
	}

	/**
	 * Sorts by ID by default
	 */
	@Override
	public int compareTo(DisplayTask a) {
		String a_id = a.getId().split("-")[1];
		String b_id = getId().split("-")[1];
		if (Integer.parseInt(a_id) > Integer.parseInt(b_id)) {
			return  1;
		} else {
			return -1;
		}
	}
}
