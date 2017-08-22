package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.CommentsSorter;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.TaskResolution;
import com.qprogramming.tasq.task.worklog.WorkLog;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.Period;

import javax.persistence.*;
import java.util.*;

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

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private Date create_date;

    @Column
    private Date lastUpdate;

    @Column
    private Date finishDate;

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

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "worklogtask", fetch = FetchType.LAZY)
    private Set<WorkLog> worklog;

    @Column
    private Boolean estimated = false;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "task", fetch = FetchType.LAZY)
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
    private Set<Sprint> sprints = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Release release;

    @Column
    private boolean inSprint;

    @Column
    private Integer subtasks;

    @Column
    private String parent;

    @Column(name = "task_order")
    private Long taskOrder;

    @Enumerated(EnumType.STRING)
    private TaskResolution resolution;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreate_date() {
        return Utils.convertDateTimeToString(create_date);
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getRawLastUpdate() {
        return lastUpdate;
    }

    public String getLastUpdate() {
        if (lastUpdate != null) {
            return Utils.convertDateTimeToString(lastUpdate);
        }
        return "";
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDue_date() {
        if (due_date != null) {
            return Utils.convertDateToString(due_date);
        }
        return "";
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public Date getRawDue_date() {
        return due_date;
    }

    public Date getRawCreate_date() {
        return create_date;
    }

    public String getFinishDate() {
        if (finishDate != null) {
            return Utils.convertDateTimeToString(finishDate);
        }
        return "";
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Date getRawFinishDate() {
        return finishDate;
    }

    public String getEstimate() {
        return PeriodHelper.outFormat(estimate);
    }

    public void setEstimate(Period estimate) {
        this.estimate = estimate;
    }

    public Period getRawEstimate() {
        return estimate != null ? estimate : new Period();
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

    public void setLoggedWork(Period loggedWork) {
        this.loggedWork = loggedWork;
    }

    public String getRemaining() {
        return PeriodHelper.outFormat(remaining);
    }

    public void setRemaining(Period remaining) {
        this.remaining = remaining;
    }

    public Period getRawRemaining() {
        return remaining != null ? remaining : new Period();
    }

    public void updateLoggedWork() {
        this.loggedWork = new Period();
        Set<WorkLog> worklg = getRawWorkLog();
        worklg.stream().filter(
                activity -> !LogType.ESTIMATE.equals(activity.getType())).forEach(
                activity -> this.loggedWork = PeriodHelper.plusPeriods(this.loggedWork, activity.getActivity()));
    }

    public Period getRawLoggedWork() {
        return loggedWork == null ? new Period() : loggedWork;
    }

    public void addLoggedWork(Period loggedWork) {
        this.loggedWork = PeriodHelper.plusPeriods(getRawLoggedWork(),
                loggedWork);
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
        List<Comment> comments_list = new ArrayList<>(comments);
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
        return story_points != null ? story_points : 0;
    }

    public void setStory_points(Integer story_points) {
        this.story_points = story_points;
    }

    public Set<Sprint> getSprints() {
        if (sprints == null) {
            sprints = new HashSet<>();
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

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public TaskResolution getResolution() {
        return resolution;
    }

    public void setResolution(TaskResolution resolution) {
        this.resolution = resolution;
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
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result
                + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
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
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
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
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        } else if (!project.equals(other.project)) {
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
        return PeriodHelper.toStandardDuration(result).getMillis() > 0;
    }

    public float getMoreThanEstimate() {
        Period loggedAndLeft = getRawLoggedWork();
        if (PeriodHelper.toStandardDuration(remaining).getMillis() > 0) {
            loggedAndLeft = PeriodHelper.plusPeriods(loggedAndLeft, remaining);
        }
        return PeriodHelper.toStandardDuration(estimate).getMillis() * 100
                / PeriodHelper.toStandardDuration(loggedAndLeft).getMillis();
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
        return StringUtils.isNotBlank(parent);
    }
}
