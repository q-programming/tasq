package com.qprogramming.tasq.task.link;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskLinkRepository extends JpaRepository<TaskLink, Integer> {

	List<TaskLink> findByLinkType(TaskLinkType type);

	List<TaskLink> findByTaskA(String taskA);

	List<TaskLink> findByTaskB(String taskB);

	TaskLink findByTaskAAndTaskBAndLinkType(String taskA, String taskB,
			TaskLinkType type);

	TaskLink findByTaskBAndTaskAAndLinkType(String taskB, String taskA,
			TaskLinkType type);
}
