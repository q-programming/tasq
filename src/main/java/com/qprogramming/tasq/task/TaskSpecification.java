package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account_;
import com.qprogramming.tasq.projects.Project_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * Created by jromaniszyn on 24.06.2016.
 */
public class TaskSpecification implements Specification<Task> {
    private final TaskFilter taskFilter;

    public TaskSpecification(TaskFilter task) {
        this.taskFilter = task;
    }

    @Override
    public Predicate toPredicate(Root<Task> task, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        //project
        predicate = cb.and(predicate, cb.equal(task.get(Task_.project).get(Project_.id), taskFilter.getProject().getId()));
//      no subtasks
        predicate = cb.and(predicate, task.get(Task_.parent).isNull());
        if (StringUtils.isNotBlank(taskFilter.getByState())) {
            if (TaskFilter.OPEN.equals(taskFilter.getByState())) {
                predicate = cb.and(predicate, task.get(Task_.state).in(TaskState.TO_DO, TaskState.ONGOING, TaskState.COMPLETE, TaskState.BLOCKED));
            } else {
                predicate = cb.and(predicate, task.get(Task_.state).in(TaskState.valueOf(taskFilter.getByState())));
            }
        }
        if (taskFilter.getType() != null) {
            predicate = cb.and(predicate, task.get(Task_.type).in(taskFilter.getType()));
        }
        if (taskFilter.getPriority() != null) {
            predicate = cb.and(predicate, task.get(Task_.priority).in(taskFilter.getPriority()));
        }
        if (taskFilter.getAssignee() != null) {
            predicate = cb.and(predicate, task.get(Task_.assignee).get(Account_.id).in(taskFilter.getAssignee().getId()));
        }
        return predicate;
    }
}
