package com.qprogramming.tasq.agile;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.joda.time.Period;

import com.qprogramming.tasq.projects.Project;

@Entity
public class Sprint implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7624092840001371525L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sprint_seq_gen")
	@SequenceGenerator(name = "sprint_seq_gen", sequenceName = "sprint_id_seq", allocationSize = 1)
	private Long id;

	@Column
	private String name;

	@ManyToOne
	@JoinColumn(name = "project_sprint")
	private Project project;

	@Column
	private Long sprintNo;

	@Column
	private Date start_date;

	@Column
	private Date end_date;

	@Column
	private boolean active;

	@Column
	private boolean finished;
	
	@Column
	private Period totalEstimate;
	
	@Column
	private Integer totalStoryPoints;

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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Long getSprintNo() {
		return sprintNo;
	}

	public void setSprint_no(Long sprint_no) {
		this.sprintNo = sprint_no;
	}

	public Date getRawStart_date() {
		return start_date;
	}

	public String getStart_date() {
		if (start_date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(start_date);
		}
		return "";
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		if (end_date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(end_date);
		}
		return "";
	}

	public Date getRawEnd_date() {
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

	public boolean getFinished() {
		return finished;
	}

	public void finish() {
		this.finished = true;
	}
	public Period getTotalEstimate() {
		return totalEstimate;
	}

	public void setTotalEstimate(Period total_estimate) {
		this.totalEstimate = total_estimate;
	}

	public Integer getTotalStoryPoints() {
		return totalStoryPoints;
	}

	public void setTotalStoryPoints(Integer totalStoryPoints) {
		this.totalStoryPoints = totalStoryPoints;
	}

	@Override
	public String toString() {
		return "Sprint " + sprintNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		Sprint other = (Sprint) obj;
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
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

}
