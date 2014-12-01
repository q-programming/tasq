package com.qprogramming.tasq.task.link;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskLinkRepository extends JpaRepository<TaskLink, Integer> {

	List<TaskLink> findByLinkType(TaskLinkType type);

	List<TaskLink> findByTaskA(String taskA);

	List<TaskLink> findByTaskB(String taskB);

	TaskLink findByTaskAAndLinkType(String taskA, TaskLinkType type);

	TaskLink findByTaskBAndLinkType(String taskB, TaskLinkType type);
}
