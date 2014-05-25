package com.qprogramming.tasq.projects.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qprogramming.tasq.projects.Project;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	Task findByName(String Name);

	Task findById(Long id);

	List<Task> findAllByProject(Project project);
}
