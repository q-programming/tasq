package com.qprogramming.tasq.agile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.joda.time.DateTime;

import com.qprogramming.tasq.projects.Project;

@Entity
public class Release implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6853317276881656443L;

	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "release_seq_gen")
	@SequenceGenerator(name = "release_seq_gen", sequenceName = "release_id_seq", allocationSize = 1)
	private Long id;

	@Column
	private String release;

	@Column
	private DateTime startDate;

	@Column
	private DateTime endDate;
	
	@Column(columnDefinition = "text")
	private String comment;

	@ManyToOne
	@JoinColumn(name = "project_release")
	private Project project;
	
	@Column
	private boolean active = false;

	public Release() {
		// TODO Auto-generated constructor stub
	}

	public Release(Project project, String release, String comment) {
		this.project = project;
		this.release = release;
		this.endDate = new DateTime();
		this.comment = comment;
		this.active = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String releaseNo) {
		this.release = releaseNo;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public DateTime getStartDate() {
		if(startDate == null){
			startDate = new DateTime(project.getRawStartDate());
		}
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		if (endDate==null){
			endDate = new DateTime();
		}
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return project.getProjectId() + " " + release;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((release == null) ? 0 : release.hashCode());
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
		Release other = (Release) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!project.equals(other.project)) {
			return false;
		}
		if (release == null) {
			if (other.release != null) {
				return false;
			}
		} else if (!release.equals(other.release)) {
			return false;
		}
		return true;
	}

}
