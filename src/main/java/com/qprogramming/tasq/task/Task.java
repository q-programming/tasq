package com.qprogramming.tasq.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.persistence.Transient;

import org.joda.time.Period;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.sorters.CommentsSorter;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.worklog.WorkLog;

@Entity
public class Task implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7551953383145553379L;

	@Id
	private String id;

	@Column
	private String name;

	@Column(length = 4000)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_tasks")
	private Project project;

	@ManyToOne
	private Account project_admin;

	@Column
	private Date create_date;

	@Column
	private Date last_visit;

	@Column
	private boolean active = false;

	@Column
	private Date due_date;

	@Column
	private Integer story_points;

	@Column
	private Period estimate;

	@Column
	private Period remaining;

	@Transient
	private Period logged_work;

	@Column
	private Enum<TaskState> state;

	@Column
	private Enum<TaskType> type;

	@Column
	private Enum<TaskPriority> priority;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "task")
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

	@ManyToOne
	@JoinColumn(name = "task_sprint")
	private Sprint sprint;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Sprint> sprints = new HashSet<Sprint>();
	
	@Column
	private boolean inSprint;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Project getProject() {
		return project;
	}

	public Account getProject_admin() {
		return project_admin;
	}

	public String getCreate_date() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return sdf.format(create_date);
	}

	public Date getLast_visit() {
		return last_visit;
	}

	public boolean isActive() {
		return active;
	}

	public String getDue_date() {
		if (due_date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			return sdf.format(due_date);
		}
		return "";
	}

	public Date getRawDue_date() {
		return due_date;
	}

	public Integer getStory_points() {
		return story_points;
	}

	public String getEstimate() {
		return PeriodHelper.outFormat(estimate);
	}

	public Period getRawEstimate() {
		return estimate;
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

	public void setProject(Project project) {
		this.project = project;
	}

	public void setProject_admin(Account project_admin) {
		this.project_admin = project_admin;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public void setLast_visit(Date last_visit) {
		this.last_visit = last_visit;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public void setStory_points(Integer story_points) {
		this.story_points = story_points;
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
		if (worklog==null){
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

	public String getLogged_work() {
		getRawLogged_work();
		return PeriodHelper.outFormat(logged_work);
	}

	public String getRemaining() {
		return PeriodHelper.outFormat(remaining);
	}

	public Period getRawRemaining() {
		return remaining;
	}

	public void setRemaining(Period remaining) {
		this.remaining = remaining;
	}

	public Period getRawLogged_work() {
		logged_work = new Period();
		Set<WorkLog> worklog = getRawWorkLog();
		for (WorkLog activity : worklog) {
			logged_work = PeriodHelper.plusPeriods(logged_work,
					activity.getActivity());
		}
		return logged_work;
	}

	public void setLogged_work(Period logged_work) {
		this.logged_work = logged_work;
	}

	public Enum<TaskState> getState() {
		return state;
	}

	public void setState(Enum<TaskState> state) {
		this.state = state;
	}

	public Enum<TaskType> getType() {
		return type;
	}

	public void setType(Enum<TaskType> type) {
		this.type = type;
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

	public Sprint getSprint() {
		return sprint;
	}

	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}

	public Set<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(Set<Sprint> sprints) {
		this.sprints = sprints;
	}


	public boolean isInSprint() {
		return inSprint;
	}

	public void setInSprint(boolean inSprint) {
		this.inSprint = inSprint;
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
		Task other = (Task) obj;
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

	/**
	 * Helpers
	 */

	public float getPercentage_logged() {
		long estimate_milis = estimate.toStandardDuration().getMillis();
		if (estimate_milis > 0) {
			return getRawLogged_work().toStandardDuration().getMillis() * 100
					/ estimate_milis;
			// task was without estimation time but is estimated type
		} else if (estimate_milis <= 0 & estimated) {
			if (getRawRemaining().toStandardDuration().getMillis() == 0) {
				return 100;
			} else {
				return getRawLogged_work().toStandardDuration().getMillis()
						* 100
						/ getRawRemaining().toStandardDuration().getMillis();
			}
		} else {
			return 0;
		}

	};

	public boolean getLowerThanEstimate() {
		Period loggedAndLeft = PeriodHelper.plusPeriods(getRawLogged_work(),
				remaining);
		Period result = PeriodHelper.minusPeriods(estimate, loggedAndLeft);
		return result.toStandardDuration().getMillis() > 0;
	}

	public float getMoreThanEstimate() {
		Period loggedAndLeft = getRawLogged_work();
		if (remaining.toStandardDuration().getMillis() > 0) {
			loggedAndLeft = PeriodHelper.plusPeriods(loggedAndLeft, remaining);
		}
		return estimate.toStandardDuration().getMillis() * 100
				/ loggedAndLeft.toStandardDuration().getMillis();
	}

	public float getOverCommited() {
		long remaining_milis = remaining.toStandardDuration().getMillis();
		if (remaining_milis > 0) {
			return (remaining_milis * 100)
					/ PeriodHelper.plusPeriods(getRawLogged_work(), remaining)
							.toStandardDuration().getMillis();
		}
		return 0;

	}

	public float getPercentage_left() {
		long estimate_milis = estimate.toStandardDuration().getMillis();
		if (estimate_milis > 0) {
			return getRawRemaining().toStandardDuration().getMillis() * 100
					/ estimate_milis;
		} else {
			return 0;
		}
	}

	public void reduceRemaining(Period activity) {
		remaining = PeriodHelper.minusPeriods(remaining, activity);
		if (remaining.toStandardDuration().getMillis() < 0) {
			remaining = new Period();
		}
	}
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
