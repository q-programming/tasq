package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.comments.CommentService;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private TaskRepository taskRepo;
    private AppService appSrv;
    private AgileService sprintSrv;
    private AccountService accountSrv;
    private TaskLinkService linkSrv;
    private WorkLogService wlSrv;
    private CommentService comSrv;
    private MessageSource msg;

    @Autowired
    public TaskService(TaskRepository taskRepo, AppService appSrv, AgileService sprintSrv, AccountService accountSrv,
                       MessageSource msg, WorkLogService wlSrv, CommentService comSrv, TaskLinkService linkSrv) {
        this.taskRepo = taskRepo;
        this.appSrv = appSrv;
        this.sprintSrv = sprintSrv;
        this.accountSrv = accountSrv;
        this.msg = msg;
        this.linkSrv = linkSrv;
        this.wlSrv = wlSrv;
        this.comSrv = comSrv;
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
        Task subtask = save(subTask);
        save(parentTask);
        return subtask;
    }

    public String createSubId(String id, String subId) {
        return id + "/" + subId;
    }


    public ResultData deleteTask() {


        return null;
    }


    public ResultData checkTaskCanOperated(Task task, boolean remove) {
        List<Account> accounts = accountSrv.findAll();
        List<Account> workingAccounts = accounts.stream().filter(x -> x.getActive_task() != null && x.getActive_task().length > 0
                && x.getActive_task()[0].equals(task.getId())).collect(Collectors.toList());
        if (workingAccounts.size() > 0) {
            Account currentAccount = Utils.getCurrentAccount();
            if (workingAccounts.size() > 1 || !workingAccounts.get(0).equals(currentAccount)) {
                return new ResultData(ResultData.ERROR, msg.getMessage("task.changeState.change.working",
                        new Object[]{String.join(",", workingAccounts.stream().map(Account::toString).collect(Collectors.toList()))}, Utils.getCurrentLocale()));
            }
            if (remove) {
                currentAccount.clearActive_task();
            }
            accountSrv.update(currentAccount);
        }
        return new ResultData(ResultData.OK, null);
    }

    public void deleteFiles(Task task) throws IOException {
        File folder = new File(getTaskDirectory(task));
        FileUtils.deleteDirectory(folder);
    }

    /**
     * Checks if state should be changed to ongoing and saves task
     *
     * @param task
     * @return
     */
    public Task checkStateAndSave(Task task) {
        if (task.getState().equals(TaskState.TO_DO)) {
            task.setState(TaskState.ONGOING);
            changeState(TaskState.TO_DO, TaskState.ONGOING, task);
        }
        return save(task);
    }

    /**
     * private method to remove task and all potential links
     *
     * @param task
     * @return
     */
    public void removeTaskRelations(Task task) {
        linkSrv.deleteTaskLinks(task);
        task.setOwner(null);
        task.setAssignee(null);
        task.setProject(null);
        task.setTags(null);
        wlSrv.deleteTaskWorklogs(task);
        Set<Comment> comments = comSrv.findByTaskIdOrderByDateDesc(task.getId());
        comSrv.delete(comments);
    }
    /**
     * Adds event about state changed
     *
     * @param newState
     * @param oldState
     * @param task
     */
    public void changeState(TaskState oldState, TaskState newState, Task task) {
        wlSrv.addActivityLog(task, Utils.changedFromTo(oldState.getDescription(), newState.getDescription()), LogType.STATUS);
    }

}
