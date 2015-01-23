package com.qprogramming.tasq.task;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Task extends AbstractTask implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7551953383145553379L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_tasks")
	private Project project;

	@Column
	private Integer story_points;

	@Column
	private Enum<TaskType> type;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Sprint> sprints = new HashSet<Sprint>();

	@Column
	private boolean inSprint;
	
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "task")
	private Set<SubTask> subtasks;

	public Project getProject() {
		return project;
	}

	public Integer getStory_points() {
		return story_points;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setStory_points(Integer story_points) {
		this.story_points = story_points;
	}

	public Enum<TaskType> getType() {
		return type;
	}

	public void setType(Enum<TaskType> type) {
		this.type = type;
	}

	public Set<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(Set<Sprint> sprints) {
		this.sprints = sprints;
	}

	public Set<SubTask> getSubtasks() {
		return subtasks;
	}

	public void setSubtasks(Set<SubTask> subtasks) {
		this.subtasks = subtasks;
	}

	public boolean isInSprint() {
		return inSprint;
	}

	public void setInSprint(boolean inSprint) {
		this.inSprint = inSprint;
	}

	/**
	 * Helpers
	 */

	public void addSprint(Sprint sprint) {
		if (this.sprints == null) {
			this.sprints = new LinkedHashSet<Sprint>();
		}
		this.sprints.add(sprint);
		setInSprint(true);
	}

	public void removeSprint(Sprint sprint) {
		if (this.sprints != null) {
			this.sprints.remove(sprint);
			setInSprint(false);
		}
	}
}
