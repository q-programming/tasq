package com.qprogramming.tasq.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.projects.Project;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepo;

	public Task save(Task task) {
		return taskRepo.save(task);
	}

	public List<Task> findAllByProject(Project project) {
		return taskRepo.findAllByProject(project);

	}

	public List<Task> findAll() {
		return taskRepo.findAll();
	}

	public List<Task> findAllbyUser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param id
	 * @return
	 */
	public Task findById(String id) {
		return taskRepo.findById(id);
	}
}
