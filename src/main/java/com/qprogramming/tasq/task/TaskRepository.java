package com.qprogramming.tasq.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.projects.Project;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

	Task findByName(String name);

	Task findById(String id);

	List<Task> findAllByProject(Project project);
	
	List<Task> findAllByProjectAndParentIsNull(Project project);

	List<Task> findAllByProjectParticipants_Id(Long id);

	List<Task> findByProjectAndStateAndParentIsNullAndReleaseIsNull(Project project, TaskState state);
	
	List<Task> findByProjectAndStateAndParentIsNull(Project project, TaskState state);

	List<Task> findByAssignee(Account account);

	List<Task> findByProjectAndSprintsId(Project project, Long id);

	List<Task> findByProjectAndStateNot(Project project, TaskState closed);
	
	List<Task> findByProjectAndStateNotAndParentIsNull(Project project, TaskState closed);
	
	List<Task> findByParent(String id);

	List<Task> findById(List<String> taskIDs);

	List<Task> findByProjectId(Long project);

	List<Task> findByProjectAndParentIsNullAndReleaseIsNull(Project project);

}
