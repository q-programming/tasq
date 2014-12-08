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
	private String projectID;
	private Date start_date;
	private Date end_date;
	private boolean active;
	private boolean finished;
	private Period totalEstimate;
	private Integer totalStoryPoints;

	public DisplaySprint(Sprint sprint) {
		BeanUtils.copyProperties(sprint, this);
		projectID = sprint.getProject().getProjectId();
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

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	@Override
	public int compareTo(DisplaySprint a) {
		if (a.getSprintNo() > getSprintNo()) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((end_date == null) ? 0 : end_date.hashCode());
		result = prime * result
				+ ((projectID == null) ? 0 : projectID.hashCode());
		result = prime * result
				+ ((sprintNo == null) ? 0 : sprintNo.hashCode());
		result = prime * result
				+ ((start_date == null) ? 0 : start_date.hashCode());
		result = prime * result
				+ ((totalEstimate == null) ? 0 : totalEstimate.hashCode());
		result = prime
				* result
				+ ((totalStoryPoints == null) ? 0 : totalStoryPoints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DisplaySprint other = (DisplaySprint) obj;
		if (end_date == null) {
			if (other.end_date != null) {
				return false;
			}
		} else if (!end_date.equals(other.end_date)) {
			return false;
		}
		if (projectID == null) {
			if (other.projectID != null) {
				return false;
			}
		} else if (!projectID.equals(other.projectID)) {
			return false;
		}
		if (sprintNo == null) {
			if (other.sprintNo != null) {
				return false;
			}
		} else if (!sprintNo.equals(other.sprintNo)) {
			return false;
		}
		if (start_date == null) {
			if (other.start_date != null) {
				return false;
			}
		} else if (!start_date.equals(other.start_date)) {
			return false;
		}
		if (totalEstimate == null) {
			if (other.totalEstimate != null) {
				return false;
			}
		} else if (!totalEstimate.equals(other.totalEstimate)) {
			return false;
		}
		if (totalStoryPoints == null) {
			if (other.totalStoryPoints != null) {
				return false;
			}
		} else if (!totalStoryPoints.equals(other.totalStoryPoints)) {
			return false;
		}
		return true;
	}
}
