package com.qprogramming.tasq.task;

import com.qprogramming.tasq.projects.Project;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by jromaniszyn on 24.06.2016.
 */
@StaticMetamodel(Task.class)
public class Task_ {

    public static volatile SingularAttribute<Task, String> id;
    public static volatile SingularAttribute<Task, String> name;
    public static volatile SingularAttribute<Task, String> description;
    public static volatile SingularAttribute<Task, TaskState> state;
    public static volatile SingularAttribute<Task, String> assignee;
    public static volatile SingularAttribute<Task, TaskPriority> priority;
    public static volatile SingularAttribute<Task, TaskType> type;
    public static volatile SingularAttribute<Task, String> project;
    public static volatile SingularAttribute<Task, String> parent;
}
