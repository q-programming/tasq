package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.comments.CommentService;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.io.FileUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger LOG = LoggerFactory.getLogger(TaskService.class);
    private TaskRepository taskRepo;
    private AppService appSrv;
    private AccountService accountSrv;
    private TaskLinkService linkSrv;
    private WorkLogService wlSrv;
    private CommentService comSrv;
    private MessageSource msg;
    private WatchedTaskService watchSrv;
    private LastVisitedService visitedSrv;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TaskService(TaskRepository taskRepo, AppService appSrv, AccountService accountSrv,
                       MessageSource msg, WorkLogService wlSrv, CommentService comSrv, TaskLinkService linkSrv, WatchedTaskService watchSrv, LastVisitedService visitedSrv) {
        this.taskRepo = taskRepo;
        this.appSrv = appSrv;
        this.accountSrv = accountSrv;
        this.msg = msg;
        this.linkSrv = linkSrv;
        this.wlSrv = wlSrv;
        this.comSrv = comSrv;
        this.watchSrv = watchSrv;
        this.visitedSrv = visitedSrv;
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
    public List<DisplayTask> convertToDisplay(List<Task> list, boolean tags, boolean subTaskPercentage) {
        List<DisplayTask> resultList = new LinkedList<>();
        for (Task task : list) {
            Set<Tag> tagsList = new HashSet<>();
            if (tags) {
                Hibernate.initialize(task.getTags());
                tagsList = task.getTags();
            }
            if (subTaskPercentage && task.getSubtasks() > 0) {
                List<Task> subtasks = findSubtasks(task);
                addSubtaskTimers(task, subtasks);
            }
            DisplayTask displayTask = new DisplayTask(task);
            displayTask.setTagsFromTask(tagsList);
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
        Hibernate.initialize(parentTask.getSubtasks());
        Task subtask = save(subTask);
        save(parentTask);
        return subtask;
    }

    public String createSubId(String id, String subId) {
        return id + "/" + subId;
    }

    private void clearActiveTimersForTask(Task task) {
        List<Account> withActiveTask = accountSrv.findAllWithActiveTask(task.getId());
        withActiveTask.stream().forEach(Account::clearActiveTask);
        accountSrv.update(withActiveTask);
    }

    /**
     * Deletes task with all it's subtasks and relations. It can be forced to zero all active tasks and just removed
     * or if force is set to false , all tasks and subtaks will be checked if somebody is not working on them
     *
     * @param task  task to be deleted
     * @param force if set to true, no check of active tasks will be made
     * @return {@link com.qprogramming.tasq.support.ResultData}
     */
    public ResultData deleteTask(Task task, boolean force) {
        ResultData resultData;
        if (force) {
            clearActiveTimersForTask(task);
        } else {
            resultData = checkTaskCanOperated(task, true);
            if (resultData.code.equals(ResultData.Code.ERROR)) {
                return resultData;
            }
        }
        //check it's subtasks
        List<Task> subtasks = findSubtasks(task.getId());
        for (Task subtask : subtasks) {
            if (force) {
                clearActiveTimersForTask(subtask);
            } else {
                resultData = checkTaskCanOperated(subtask, true);
                if (ResultData.Code.ERROR.equals(resultData.code)) {
                    return resultData;
                }
            }
        }
        //all ok proceed with removal
        subtasks.forEach(this::removeTaskRelations);
        deleteAll(subtasks);
        try {
            deleteFiles(task);
        } catch (IOException e) {
            String message = msg.getMessage("error.task.delete.files", new Object[]{task.getId(), e.getMessage()}, Utils.getCurrentLocale());
            LOG.error(message + " Exception {}", e.getMessage());
            LOG.debug("{}", e);
            return new ResultData(ResultData.Code.ERROR, message);
        }
        removeTaskRelations(task);
        Task purged = save(purgeTask(task));
        delete(purged);
        return new ResultData(ResultData.Code.OK, null);
    }


    /**
     * Check if task can be operated on. If for example there are other users still working on it
     *
     * @param task   task to be checked
     * @param remove if it's remove operation
     * @return {@link ResultData} with validation results
     */
    public ResultData checkTaskCanOperated(Task task, boolean remove) {
        List<Account> accounts = accountSrv.findAllWithActiveTask(task.getId());
        if (!accounts.isEmpty()) {
            Account currentAccount = Utils.getCurrentAccount();
            if (accounts.size() > 1 || !accounts.get(0).equals(currentAccount)) {
                return new ResultData(ResultData.Code.ERROR, msg.getMessage("task.changeState.change.working",
                        new Object[]{task.getId(), String.join(",", accounts.stream().map(Account::toString).collect(Collectors.toList()))}, Utils.getCurrentLocale()));
            }
            if (remove) {
                currentAccount.clearActiveTask();
                accountSrv.update(currentAccount);
            }
        }
        return new ResultData(ResultData.Code.OK, null);
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
        watchSrv.deleteWatchedTask(task.getId());
        visitedSrv.delete(task);

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

    /**
     * Nuke Task - set everything to null
     */
    private Task purgeTask(Task task) {
        Task zerotask = new Task();
        zerotask.setId(task.getId());
        return zerotask;
    }

    /**
     * It's crucial that passed task MUST be detached from session , otherwise it's original value will be overwritten
     *
     * @param task
     * @param subtasks
     * @return
     */
    public Task addSubtaskTimers(Task task, List<Task> subtasks) {
        getEntitymanager().detach(task);
        for (Task subtask : subtasks) {
            task.setEstimate(PeriodHelper.plusPeriods(task.getRawEstimate(), subtask.getRawEstimate()));
            task.setLoggedWork(PeriodHelper.plusPeriods(task.getRawLoggedWork(), subtask.getRawLoggedWork()));
            task.setRemaining(PeriodHelper.plusPeriods(task.getRawRemaining(), subtask.getRawRemaining()));
        }
        return task;
    }

    /**
     * It's crucial that passed task MUST be detached from session , otherwise it's original value will be overwritten
     *
     * @param task
     * @return
     */
    public Task addSubtaskTimers(Task task) {
        List<Task> subtasks = findSubtasks(task);
        return addSubtaskTimers(task, subtasks);
    }


    protected EntityManager getEntitymanager() {
        return entityManager;
    }

    public String printID(String taskID) {
        return "[ " + taskID + " ]";
    }
}
