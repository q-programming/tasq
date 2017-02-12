package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.task.tag.Tag;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DisplayTask implements Comparable<DisplayTask> {
    private String id;
    private String name;
    private String description;
    private String projectID;
    private Integer story_points;
    private String estimate;
    private String remaining;
    private String loggedWork;
    private Enum<TaskState> state;
    private Enum<TaskType> type;
    private Enum<TaskPriority> priority;
    private DisplayAccount owner;
    private DisplayAccount assignee;
    private Boolean estimated = false;
    private boolean inSprint;
    private Integer subtasks;
    private boolean subtask;
    private String parent;
    private Float percentage;
    private Set<String> tags;

    public DisplayTask(Task task) {
        BeanUtils.copyProperties(task, this);
        projectID = task.getProject().getProjectId();
        if (task.getAssignee() != null) {
            assignee = new DisplayAccount(task.getAssignee());
        }
        if (task.getOwner() != null) {
            owner = new DisplayAccount(task.getOwner());
        }
        this.percentage = task.getPercentage_logged();
        this.subtask = task.isSubtask();
        tags = new HashSet<>();
    }

    /**
     * Static method to return list of more lightweight form of Task
     *
     * @param list
     * @return
     */
    public static List<DisplayTask> convertToDisplayTasks(List<Task> list) {
        List<DisplayTask> result = new LinkedList<DisplayTask>();
        for (Task task : list) {
            result.add(new DisplayTask(task));
        }
        return result;
    }

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

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public Integer getStory_points() {
        return story_points;
    }

    public void setStory_points(Integer story_points) {
        this.story_points = story_points;
    }

    public String getEstimate() {
        return estimate;
    }

    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public String getLoggedWork() {
        return loggedWork;
    }

    public void setLoggedWork(String loggedWork) {
        this.loggedWork = loggedWork;
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

    public Enum<TaskPriority> getPriority() {
        return priority;
    }

    public void setPriority(Enum<TaskPriority> priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getSubtasks() {
        return subtasks;
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

    public boolean isInSprint() {
        return inSprint;
    }

    public void setInSprint(boolean inSprint) {
        this.inSprint = inSprint;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public boolean isSubtask() {
        return subtask;
    }

    public void setSubtask(boolean subtask) {
        this.subtask = subtask;
    }

    public DisplayAccount getOwner() {
        return owner;
    }

    public void setOwner(DisplayAccount owner) {
        this.owner = owner;
    }

    public Set<String> getTags() {
        if (tags == null) {
            tags = new HashSet<String>();
        }
        return tags;
    }

    public void setTagsFromTask(Set<Tag> tags) {
        this.tags.addAll(tags.stream().map(Tag::getName).collect(Collectors.toList()));
    }

    public void addTag(String tag) {
        getTags().add(tag);
    }

    public String getTagsList() {
        String delimiter = "";
        StringBuffer allTags = new StringBuffer();
        for (String tag : this.tags) {
            allTags.append(delimiter);
            allTags.append(tag);
            delimiter = ",";
        }
        //Not able to use join in Java 1.7
        //return String.join(",",allTags);
        return allTags.toString();
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DisplayTask other = (DisplayTask) obj;
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
        return true;
    }
}
