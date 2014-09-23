package com.qprogramming.tasq.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	Task findByName(String Name);

	Task findById(String id);

	List<Task> findAllByProject(Project project);
	
	List<Task> findAllByProjectParticipants_Id(Long id);

	List<Task> findByProjectAndState(Project project, TaskState state);
	
	List<Task> findByAssignee(Account account);
	
	List<Task> findByProjectAndSprint(Project project,Sprint sprint);

	List<Task> findByProjectAndSprintsId(Project project, Long id);
	
}
