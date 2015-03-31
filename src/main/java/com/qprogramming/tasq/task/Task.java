package com.qprogramming.tasq.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.Hibernate;
import org.joda.time.Period;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.CommentsSorter;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;

@Entity
public class Task implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6612220415004910977L;

	@Id
	private String id;

	@Column
	private String name;

	@Column(length = 4000)
	private String description;

	@Column
	private Date create_date;
	
	@Column
	private Date lastUpdate;

	@Column
	private boolean active = false;

	@Column
	private Date due_date;

	@Column
	private Period estimate;

	@Column
	private Period remaining;

	@Column
	private Period loggedWork;

	@Column
	private Enum<TaskState> state;

	@Column
	private Enum<TaskPriority> priority;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "worklogtask")
	private Set<WorkLog> worklog;

	@Column
	private Boolean estimated = false;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "task")
	private Set<Comment> comments;

	@ManyToOne
	@JoinColumn(name = "task_owner")
	private Account owner;

	@ManyToOne
	@JoinColumn(name = "task_assignee")
	private Account assignee;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_tasks")
	private Project project;

	@Column
	private Integer story_points;

	@Column
	private Enum<TaskType> type;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Sprint> sprints = new HashSet<Sprint>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	private Release release;

	@Column
	private boolean inSprint;

	@Column
	private Integer subtasks;

	@Column
	private String parent;
	
	@Column(name="task_order")
	private Long taskOrder;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCreate_date() {
		return Utils.convertDateTimeToString(create_date);
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean isActive() {
		return active;
	}

	public String getDue_date() {
		if (due_date != null) {
			return Utils.convertDateToString(due_date);
		}
		return "";
	}

	public Date getRawDue_date() {
		return due_date;
	}

	public String getEstimate() {
		return PeriodHelper.outFormat(estimate);
	}

	public Period getRawEstimate() {
		return estimate != null ? estimate : new Period();
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public void setEstimate(Period estimate) {
		this.estimate = estimate;
	}

	public List<WorkLog> getWorklog() {
		List<WorkLog> list_worklog = new ArrayList<WorkLog>();
		list_worklog.addAll(getRawWorkLog());
		Collections.sort(list_worklog, new WorkLogSorter(true));
		return list_worklog;
	}

	public void setWorklog(Set<WorkLog> worklog) {
		this.worklog = worklog;
	}

	public Set<WorkLog> getRawWorkLog() {
		if (worklog == null) {
			worklog = new HashSet<WorkLog>();
		}
		return worklog;
	}

	public void addWorkLog(WorkLog wl) {
		if (worklog == null) {
			worklog = new HashSet<WorkLog>();
		}
		worklog.add(wl);
	}

	public String getLoggedWork() {
		return PeriodHelper.outFormat(getRawLoggedWork());
	}

	public String getRemaining() {
		return PeriodHelper.outFormat(remaining);
	}

	public Period getRawRemaining() {
		return remaining != null ? remaining : new Period();
	}

	public void setRemaining(Period remaining) {
		this.remaining = remaining;
	}

	public void updateLoggedWork() {
		this.loggedWork = new Period();
		Set<WorkLog> worklg = getRawWorkLog();
		for (WorkLog activity : worklg) {
			if (!LogType.ESTIMATE.equals(activity.getType())) {
				this.loggedWork = PeriodHelper.plusPeriods(this.loggedWork,
						activity.getActivity());
			}
		}
	}

	public Period getRawLoggedWork() {
		return loggedWork == null ? new Period() : loggedWork;
	}

	public void setLoggedWork(Period loggedWork) {
		this.loggedWork = loggedWork;
	}

	public void addLoggedWork(Period loggedWork) {
		this.loggedWork = PeriodHelper.plusPeriods(getRawLoggedWork(), loggedWork);
	}

	public Enum<TaskState> getState() {
		return state;
	}

	public void setState(Enum<TaskState> state) {
		this.state = state;
	}

	public Boolean isEstimated() {
		return estimated;
	}

	public Boolean getEstimated() {
		return estimated;
	}

	public void setEstimated(Boolean estimated) {
		this.estimated = estimated;
	}

	public List<Comment> getComments() {
		List<Comment> comments_list = new ArrayList<Comment>(comments);
		Collections.sort(comments_list, new CommentsSorter(false));
		return comments_list;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public void addComment(Comment comment) {
		if (comments == null) {
			comments = new HashSet<Comment>();
		}
		comments.add(comment);
	}

	public Account getAssignee() {
		return assignee;
	}

	public void setAssignee(Account assignee) {
		this.assignee = assignee;
	}

	public Account getOwner() {
		return owner;
	}

	public void setOwner(Account owner) {
		this.owner = owner;
	}

	public Enum<TaskPriority> getPriority() {
		return priority;
	}

	public void setPriority(Enum<TaskPriority> priority) {
		this.priority = priority;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getStory_points() {
		return story_points;
	}

	public void setStory_points(Integer story_points) {
		this.story_points = story_points;
	}

	public Set<Sprint> getSprints() {
		if (sprints == null) {
			sprints = new HashSet<Sprint>();
		}
		return sprints;
	}

	public void setSprints(Set<Sprint> sprints) {
		this.sprints = sprints;
	}

	public Release getRelease() {
		return release;
	}

	public void setRelease(Release release) {
		this.release = release;
	}

	public boolean isInSprint() {
		return inSprint;
	}

	public void setInSprint(boolean inSprint) {
		this.inSprint = inSprint;
	}

	public Integer getSubtasks() {
		return subtasks == null ? 0 : subtasks;
	}

	public void setSubtasks(Integer subtasks) {
		this.subtasks = subtasks;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Enum<TaskType> getType() {
		return type;
	}

	public void setType(Enum<TaskType> type) {
		this.type = type;
	}

	public Long getTaskOrder() {
		return taskOrder;
	}

	public void setTaskOrder(Long taskOrder) {
		this.taskOrder = taskOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return getId() + " " + getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Task other = (Task) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (owner == null) {
			if (other.owner != null) {
				return false;
			}
		} else if (!owner.equals(other.owner)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	/**
	 * Helpers
	 */

	public float getPercentage_logged() {
		long estimate_milis = PeriodHelper.toStandardDuration(getRawEstimate())
				.getMillis();
		long remaining_milis = PeriodHelper.toStandardDuration(
				getRawRemaining()).getMillis();
		long logged_milis = PeriodHelper.toStandardDuration(getRawLoggedWork())
				.getMillis();
		if (estimate_milis > 0) {
			return logged_milis * 100 / estimate_milis;
			// task was without estimation time but is estimated type
		} else {
			if (remaining_milis == 0 && logged_milis != 0) {
				return 100;
			} else if (remaining_milis == 0 && logged_milis == 0) {
				return 0;
			} else {
				return logged_milis * 100 / (remaining_milis + logged_milis);
			}
		}
	}

	public boolean getLowerThanEstimate() {
		Period loggedAndLeft = PeriodHelper.plusPeriods(getRawLoggedWork(),
				remaining);
		Period result = PeriodHelper.minusPeriods(estimate, loggedAndLeft);
		return result.toStandardDuration().getMillis() > 0;
	}

	public float getMoreThanEstimate() {
		Period loggedAndLeft = getRawLoggedWork();
		if (PeriodHelper.toStandardDuration(remaining).getMillis() > 0) {
			loggedAndLeft = PeriodHelper.plusPeriods(loggedAndLeft, remaining);
		}
		return estimate.toStandardDuration().getMillis() * 100
				/ loggedAndLeft.toStandardDuration().getMillis();
	}

	public float getOverCommited() {
		long remaining_milis = PeriodHelper.toStandardDuration(remaining)
				.getMillis();
		if (remaining_milis > 0) {
			Period plus = PeriodHelper.plusPeriods(getRawLoggedWork(),
					remaining);
			return (remaining_milis * 100)
					/ PeriodHelper.toStandardDuration(plus).getMillis();
		}
		return 0;

	}

	public float getPercentage_left() {
		long estimate_milis = PeriodHelper.toStandardDuration(getRawEstimate())
				.getMillis();
		if (estimate_milis > 0) {
			return PeriodHelper.toStandardDuration(getRawRemaining())
					.getMillis() * 100 / estimate_milis;
		} else {
			return 0;
		}
	}

	public void reduceRemaining(Period activity) {
		remaining = PeriodHelper.minusPeriods(remaining, activity);
		if (PeriodHelper.toStandardDuration(remaining).getMillis() < 0) {
			remaining = new Period();
		}
	}

	public void addSprint(Sprint sprint) {
		getSprints().add(sprint);
		setInSprint(true);
	}

	public void removeSprint(Sprint sprint) {
		if (this.sprints != null) {
			this.sprints.remove(sprint);
			setInSprint(false);
		}
	}

	/**
	 * Reqieres session and initialize
	 * 
	 * @param sprint
	 * @return
	 */
	public boolean inSprint(Sprint sprint) {
		Hibernate.initialize(getSprints());
		return getSprints().contains(sprint);
	}

	public void addSubTask() {
		this.subtasks = getSubtasks() + 1;
	}

	public boolean isSubtask() {
		return parent != null;
	}
}
