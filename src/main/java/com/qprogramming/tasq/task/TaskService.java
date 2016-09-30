package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private TaskRepository taskRepo;
    private AppService appSrv;
    private AgileService sprintSrv;

    @Autowired
    public TaskService(TaskRepository taskRepo, AppService appSrv, AgileService sprintSrv) {
        this.taskRepo = taskRepo;
        this.appSrv = appSrv;
        this.sprintSrv = sprintSrv;
    }

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
        return taskRepo.findByProjectAndStateNotAndParentIsNull(project, TaskState.CLOSED);
    }

    public List<Task> findAllWithoutRelease(Project project) {
        return taskRepo.findByProjectAndParentIsNullAndReleaseIsNull(project);
    }

    public List<Task> findAllToRelease(Project project) {
        return taskRepo.findByProjectAndStateAndParentIsNullAndReleaseIsNull(project, TaskState.CLOSED);
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

    public List<Task> findAllBySprintId(Project project, Long sprintId) {
        return taskRepo.findByProjectAndSprintsId(project, sprintId);
    }

    public List<Task> findAllByRelease(Release release) {
        return taskRepo.findByProjectAndRelease(release.getProject(), release);
    }

    public List<Task> findAllByRelease(Project project, Release release) {
        return taskRepo.findByProjectAndRelease(project, release);
    }

    public List<Task> findSubtasksForProject(Project project) {
        if (project == null) {
            return taskRepo.findByParentIsNotNull();
        }
        return taskRepo.findByProjectAndParentIsNotNull(project);
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

    public String getTaskDir(Task task) {
        return appSrv.getProperty(AppService.TASQROOTDIR) + File.separator + task.getProject().getProjectId()
                + File.separator + task.getId();
    }

    public List<Task> finAllById(List<String> taskIDs) {
        return taskRepo.findByIdIn(taskIDs);
    }

    public List<Task> save(List<Task> taskList) {
        return taskRepo.save(taskList);
    }

    public List<Task> findAllByProjectId(Long project) {
        return taskRepo.findByProjectId(project);
    }

    /**
     * converts to DisplayTask
     *
     * @param list
     * @param tags if tags should be included (!requires transaction )
     * @return
     */
    public List<DisplayTask> convertToDisplay(List<Task> list, boolean tags) {
        List<DisplayTask> resultList = new LinkedList<DisplayTask>();
        for (Task task : list) {
            DisplayTask displayTask = new DisplayTask(task);
            if (tags) {
                Hibernate.initialize(task.getTags());
                displayTask.setTagsFromTask(task.getTags());
            }
            resultList.add(displayTask);
        }
        return resultList;
    }

    /**
     * Returns all tasks with given tag
     *
     * @param name
     * @return
     */
    public List<Task> findByTag(String name) {
        return taskRepo.findByTagsName(name);
    }

    // @Transactional
    public Set<Sprint> getTaskSprints(String id) {
        Task task = taskRepo.findById(id);
        Hibernate.initialize(task.getSprints());
        return task.getSprints();
    }

    public List<Task> findBySpecification(TaskFilter filter) {
        return taskRepo.findAll(new TaskSpecification(filter));
    }

    /**
     * Creates subtask for which task is parent
     * Must be run within transactional block
     *
     * @param project    task project
     * @param parentTask parent task
     * @param subTask    new subtask
     */
    public Task createSubTask(Project project, Task parentTask, Task subTask) {
        int taskCount = parentTask.getSubtasks();
        taskCount++;
        String taskID = createSubId(parentTask.getId(), String.valueOf(taskCount));
        subTask.setId(taskID);
        subTask.setParent(parentTask.getId());
        subTask.setProject(project);
        parentTask.addSubTask();
        if (sprintSrv.taskInActiveSprint(parentTask)) {
            Sprint active = sprintSrv.findByProjectIdAndActiveTrue(parentTask.getProject().getId());
            subTask.addSprint(active);
        }
        Hibernate.initialize(parentTask.getSubtasks());
        save(subTask);
        return save(parentTask);
    }

    public String createSubId(String id, String subId) {
        return id + "/" + subId;
    }


}
