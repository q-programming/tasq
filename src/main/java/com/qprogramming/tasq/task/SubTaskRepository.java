package com.qprogramming.tasq.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Integer> {

	SubTask findByName(String name);

	SubTask findById(String id);

	List<SubTask> findByTaskId(String taskID);

}
