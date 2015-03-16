package com.qprogramming.tasq.task;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;

@Service
public class TaskService {

	
	@Value("${home.directory}")
	private String tasqRootDir;

	@Autowired
	private TaskRepository taskRepo;

	public Task save(Task task) {
		return taskRepo.save(task);
	}

	public List<Task> findAllByProject(Project project) {
		return taskRepo.findAllByProjectAndParentIsNull(project);

	}

	public List<Task> findAll() {
		return taskRepo.findAll();
	}

	public List<Task> findByProjectAndState(Project project, TaskState state) {
		return taskRepo.findByProjectAndStateAndParentIsNull(project, state);
	}

	public List<Task> findByProjectAndOpen(Project project) {
		return taskRepo.findByProjectAndStateNotAndParentIsNull(project,
				TaskState.CLOSED);
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
		return taskRepo.findByProjectAndSprintsId(sprint.getProject(),
				sprint.getId());
	}

	public List<Task> findSubtasks(String taskID) {
		return taskRepo.findByParent(taskID);
	}

	public List<Task> findSubtasks(Task task) {
		return findSubtasks(task.getId());
	}

	public void deleteAll(List<Task> tasks) {
		taskRepo.delete(tasks);
	}

	public String getTaskDirectory(Task task) {
		String dirPath = getTaskDir(task);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
			dir.setWritable(true, false);
			dir.setReadable(true, false);
		}
		return dirPath;
	}

	private String getTaskDir(Task task) {
		return tasqRootDir + File.separator + task.getProject().getProjectId()+ File.separator + task.getId();
	}

	public List<Task> finAllById(List<String> taskIDs) {
		return taskRepo.findById(taskIDs);
	}

	public List<Task> save(List<Task> taskList) {
		 return taskRepo.save(taskList);
	}

	public List<Task> findAllByProjectId(Long project) {
		return taskRepo.findByProjectId(project);
	}
}
