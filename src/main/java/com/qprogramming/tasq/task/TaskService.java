package com.qprogramming.tasq.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepo;
	
	@Autowired
	private SubTaskRepository subtaskRepo;

	public Task save(Task task) {
		return taskRepo.save(task);
	}
	public SubTask save(SubTask task) {
		return subtaskRepo.save(task);
	}

	public List<Task> findAllByProject(Project project) {
		return taskRepo.findAllByProject(project);

	}

	public List<Task> findAll() {
		return taskRepo.findAll();
	}

	public List<Task> findByProjectAndState(Project project, TaskState state) {
		return taskRepo.findByProjectAndState(project, state);
	}
	
	public List<Task> findByProjectAndOpen(Project project) {
		return taskRepo.findByProjectAndStateNot(project, TaskState.CLOSED);
	}

	/**
	 * @param id
	 * @return
	 */
	public Task findById(String id) {
		return taskRepo.findById(id);
	}

	public List<Task> findByAssignee(Account assignee) {
		return taskRepo.findByAssignee(assignee);
	}

	public List<Task> findAllByUser(Account account) {
		return taskRepo.findAllByProjectParticipants_Id(account.getId());
	}

	public void delete(Task task) {
		taskRepo.delete(task);
	}

	public List<Task> findAllBySprint(Sprint sprint) {
		return taskRepo.findByProjectAndSprintsId(sprint.getProject(), sprint.getId());
	}
	
	public List<SubTask> findSubtasks(Task task){
		return subtaskRepo.findByTaskId(task.getId());
	}
	public SubTask findSubTaskById(String id) {
		return subtaskRepo.findById(id);
	}

}
