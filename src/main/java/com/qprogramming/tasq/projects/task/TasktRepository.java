package com.qprogramming.tasq.projects.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasktRepository extends JpaRepository<Task, Integer> {

	Task findBynName(String Name);

	Task findById(Long id);
}
