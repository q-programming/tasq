package com.qprogramming.tasq.task;

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
        cb.and(predicate, cb.equal(task.get(Task_.project), taskFilter.getProject().getId()));
        //no subtasks
        cb.and(predicate, task.get(Task_.parent).isNull());
        //state
        if (StringUtils.isNotBlank(taskFilter.getByState())) {
            if (TaskFilter.OPEN.equals(taskFilter.getByState())) {
                predicate = cb.and(predicate, task.get(Task_.state).in(TaskState.TO_DO, TaskState.ONGOING, TaskState.COMPLETE, TaskState.BLOCKED));
            } else {
                predicate = cb.and(predicate, task.get(Task_.state).in(TaskState.valueOf(taskFilter.getByState())));
            }
        }
        //type
        if (taskFilter.getType() != null) {
            predicate = cb.and(predicate, task.get(Task_.type).in(taskFilter.getType()));
        }
        //priority
        if (taskFilter.getPriority() != null) {
            predicate = cb.and(predicate, task.get(Task_.priority).in(taskFilter.getPriority()));
        }
        //query
//        if (StringUtils.isNotBlank(taskFilter.getQuery())) {
//            cb.function("CONTAINS",Boolean.class,task.get(Task_.id), taskFilter.getQuery().toLowerCase())
//            Predicate query_predicate = cb.or(cb.like( + "%"),
//                    cb.like(cb.lower(task.get(Task_.name)), taskFilter.getQuery().toLowerCase() + "%"),
//                    cb.like(cb.lower(task.get(Task_.description)), taskFilter.getQuery().toLowerCase() + "%"));
//            predicate = cb.and(predicate, query_predicate);
//        }
        return predicate;
    }
}
