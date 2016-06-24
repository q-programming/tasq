package com.qprogramming.tasq.task;

import com.qprogramming.tasq.projects.Project;
import org.apache.commons.lang.StringUtils;

/**
 * Created by jromaniszyn on 24.06.2016.
 */
public class TaskFilter extends Task {
    public static final String OPEN = "OPEN";
    public static final String ALL = "ALL";
    private String byState;
    private String query;

    public TaskFilter(Project project, String state, String query, String priority, String type, String assignee) {
        this.query = query;
        setProject(project);
        if (StringUtils.isNotEmpty(priority)) {
            setPriority(TaskPriority.toPriority(priority));
        }
        if (StringUtils.isNotEmpty(state) && !ALL.equals(state)) {
            this.byState = state;
        }
        if (StringUtils.isNotEmpty(type)) {
            setType(TaskType.toType(type));
        }

    }

    public String getByState() {
        return byState;
    }

    public String getQuery() {
        return query;
    }
}
