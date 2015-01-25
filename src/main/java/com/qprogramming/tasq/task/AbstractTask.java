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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.joda.time.Period;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.sorters.CommentsSorter;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractTask {
	@Id
	private String id;

	@Column
	private String name;

	@Column(length = 4000)
	private String description;

	@Column
	private Date create_date;

	@Column
	private boolean active = false;

	@Column
	private Date due_date;

	@Column
	private Period estimate;

	@Column
	private Period remaining;

	@Transient
	private Period logged_work;

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
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return sdf.format(create_date);
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
		Set<WorkLog> worklg = getRawWorkLog();
		for (WorkLog activity : worklg) {
			if (!LogType.ESTIMATE.equals(activity.getType())) {
				logged_work = PeriodHelper.plusPeriods(logged_work,
						activity.getActivity());
			}
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
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
		result = prime * result
				+ ((remaining == null) ? 0 : remaining.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
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
		AbstractTask other = (AbstractTask) obj;
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
		if (priority == null) {
			if (other.priority != null) {
				return false;
			}
		} else if (!priority.equals(other.priority)) {
			return false;
		}
		if (remaining == null) {
			if (other.remaining != null) {
				return false;
			}
		} else if (!remaining.equals(other.remaining)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
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

}
