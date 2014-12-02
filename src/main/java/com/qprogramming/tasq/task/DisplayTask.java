package com.qprogramming.tasq.task;

import org.joda.time.Period;
import org.springframework.beans.BeanUtils;

import com.qprogramming.tasq.account.DisplayAccount;

public class DisplayTask implements Comparable<DisplayTask> {
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
	private DisplayAccount assignee;
	private Boolean estimated = false;

	public DisplayTask(Task task) {
		BeanUtils.copyProperties(task, this);
		projectID = task.getProject().getProjectId();
		if (task.getAssignee() != null) {
			assignee = new DisplayAccount(task.getAssignee());
		}
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

	public DisplayAccount getAssignee() {
		return assignee;
	}

	public void setAssignee(DisplayAccount assignee) {
		this.assignee = assignee;
	}

	@Override
	public String toString() {
		return getId() + " " + getName();
	}

	/**
	 * Sorts by ID by default
	 */
	@Override
	public int compareTo(DisplayTask a) {
		String a_id = a.getId().split("-")[1];
		String b_id = getId().split("-")[1];
		if (Integer.parseInt(a_id) > Integer.parseInt(b_id)) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DisplayTask other = (DisplayTask) obj;
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
		return true;
	}
}
