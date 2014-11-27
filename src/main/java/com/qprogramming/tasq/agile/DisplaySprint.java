package com.qprogramming.tasq.agile;

import java.util.Date;

import org.joda.time.Period;
import org.springframework.beans.BeanUtils;

/**
 * Helper class without project link
 * 
 * @author jakub.romaniszyn
 * 
 */
public class DisplaySprint implements Comparable<DisplaySprint> {
	private String name;
	private Long sprintNo;
	private Date start_date;
	private Date end_date;
	private boolean active;
	private boolean finished;
	private Period totalEstimate;
	private Integer totalStoryPoints;

	public DisplaySprint(Sprint sprint) {
		BeanUtils.copyProperties(sprint, this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSprintNo() {
		return sprintNo;
	}

	public void setSprintNo(Long sprintNo) {
		this.sprintNo = sprintNo;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Period getTotalEstimate() {
		return totalEstimate;
	}

	public void setTotalEstimate(Period totalEstimate) {
		this.totalEstimate = totalEstimate;
	}

	public Integer getTotalStoryPoints() {
		return totalStoryPoints;
	}

	public void setTotalStoryPoints(Integer totalStoryPoints) {
		this.totalStoryPoints = totalStoryPoints;
	}

	@Override
	public int compareTo(DisplaySprint a) {
		if (a.getSprintNo() > getSprintNo()) {
			return -1;
		} else {
			return 1;
		}
	}
}
