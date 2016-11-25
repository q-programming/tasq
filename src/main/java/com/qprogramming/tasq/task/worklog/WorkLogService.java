/**
 *
 */
package com.qprogramming.tasq.task.worklog;

import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author romanjak
 * @date 28 maj 2014
 */
@Service
public class WorkLogService {

    private WorkLogRepository wlRepo;
    private TaskService taskSrv;
    private ProjectService projSrv;
    private EventsService eventSrv;

    @Autowired
    public WorkLogService(WorkLogRepository wlRepo, TaskService taskSrv, ProjectService projSrv,
                          EventsService eventSrv) {
        this.wlRepo = wlRepo;
        this.taskSrv = taskSrv;
        this.projSrv = projSrv;
        this.eventSrv = eventSrv;
    }

    @Transactional
    public void addTimedWorkLog(Task task, String msg, Date when, Period remaining, Period activity, LogType type) {
        Task loggedTask = taskSrv.findById(task.getId());
        if (loggedTask != null) {
            WorkLog wl = new WorkLog();
            wl.setTask(loggedTask);
            wl.setProject_id(loggedTask.getProject().getId());
            wl.setAccount(Utils.getCurrentAccount());
            wl.setTimeLogged(new Date());
            wl.setTime(when);
            wl.setType(type);
            wl.setMessage(msg);
            wl = wlRepo.save(wl);
            wl.setActivity(activity);
            Hibernate.initialize(loggedTask.getWorklog());
            loggedTask.addWorkLog(wl);
            if (remaining == null) {
                loggedTask.reduceRemaining(activity);
            } else {
                loggedTask.setRemaining(remaining);
            }
            loggedTask.addLoggedWork(activity);
            loggedTask.setLastUpdate(new Date());
            checkStateAndSave(loggedTask);
            eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), when);
        }
    }

    @Transactional
    public void addDatedWorkLog(Task task, String msg, Date when, LogType type) {
        Task loggedTask = taskSrv.findById(task.getId());
        if (loggedTask != null) {
            WorkLog wl = new WorkLog();
            wl.setTask(loggedTask);
            wl.setProject_id(loggedTask.getProject().getId());
            wl.setAccount(Utils.getCurrentAccount());
            wl.setTimeLogged(new Date());
            wl.setTime(when);
            wl.setType(type);
            wl.setMessage(msg);
            wl = wlRepo.save(wl);
            Hibernate.initialize(loggedTask.getWorklog());
            loggedTask.addWorkLog(wl);
            loggedTask.setLastUpdate(new Date());
            taskSrv.save(loggedTask);
            eventSrv.addWatchEvent(wl, msg, when);
        }
    }

    /**
     *
     * @param task
     * @param msg
     * @param type
     */
    @Transactional
    public void addActivityLog(Task task, String msg, LogType type) {
        Task loggedTask = taskSrv.findById(task.getId());
        if (loggedTask != null) {
            WorkLog wl = new WorkLog();
            wl.setTask(loggedTask);
            wl.setProject_id(loggedTask.getProject().getId());
            wl.setAccount(Utils.getCurrentAccount());
            wl.setTime(new Date());
            wl.setTimeLogged(new Date());
            wl.setType(type);
            wl.setMessage(msg);
            wl = wlRepo.save(wl);
            Hibernate.initialize(loggedTask.getWorklog());
            loggedTask.addWorkLog(wl);
            loggedTask.setLastUpdate(new Date());
            taskSrv.save(loggedTask);
            eventSrv.addWatchEvent(wl, msg, new Date());
        }
    }

    @Transactional
    public void addActivityPeriodLog(Task task, String msg, Period activity, LogType type) {
        Task loggedTask = taskSrv.findById(task.getId());
        if (loggedTask != null) {
            WorkLog wl = new WorkLog();
            wl.setTask(loggedTask);
            wl.setProject_id(loggedTask.getProject().getId());
            wl.setAccount(Utils.getCurrentAccount());
            wl.setTime(new Date());
            wl.setTimeLogged(new Date());
            wl.setType(type);
            wl.setMessage(msg);
            wl.setActivity(activity);
            wl = wlRepo.save(wl);
            Hibernate.initialize(loggedTask.getWorklog());
            loggedTask.addWorkLog(wl);
            loggedTask.setLastUpdate(new Date());
            taskSrv.save(loggedTask);
            eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), new Date());
        }
    }

    /**
     *
     * @param task
     * @param msg
     * @param activity
     * @param type
     */
    @Transactional
    public void addNormalWorkLog(Task task, String msg, Period activity, LogType type) {
        Task loggedTask = taskSrv.findById(task.getId());
        if (loggedTask != null) {
            WorkLog wl = new WorkLog();
            wl.setTask(loggedTask);
            wl.setProject_id(loggedTask.getProject().getId());
            wl.setAccount(Utils.getCurrentAccount());
            wl.setTimeLogged(new Date());
            wl.setTime(new Date());
            wl.setType(type);
            wl.setMessage(msg);
            wl.setActivity(activity);
            wl = wlRepo.save(wl);
            Hibernate.initialize(loggedTask.getWorklog());
            loggedTask.addWorkLog(wl);
            loggedTask.reduceRemaining(activity);
            loggedTask.addLoggedWork(activity);
            loggedTask.setLastUpdate(new Date());
            if (!type.equals(LogType.ESTIMATE)) {
                checkStateAndSave(loggedTask);
            } else {
                taskSrv.save(loggedTask);
            }
            eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), new Date());
        }
    }

    /**
     * Add worklog without task ( for example for project only )
     * @param msg
     * @param project
     * @param type
     */
    public void addWorkLogNoTask(String msg, Project project, LogType type) {
        WorkLog wl = new WorkLog();
        wl.setProject_id(project.getId());
        wl.setAccount(Utils.getCurrentAccount());
        wl.setTimeLogged(new Date());
        wl.setTime(new Date());
        wl.setType(type);
        wl.setMessage(msg);
        wl = wlRepo.save(wl);
    }

    public List<DisplayWorkLog> getProjectEvents(Project project) {
        List<WorkLog> workLogs = wlRepo.findByProjectId(project.getId());
        Collections.sort(workLogs, new WorkLogSorter(true));
        return packIntoDisplay(workLogs);
    }

    /**
     * Returns all sprint events. If timeTracked will be set to true, only
     * events with logged activity ( time ) will be returned, otherwise only
     * events which were closing task will be returned
     *
     * @param sprint      sprint for which all events must be fetched
     * @return
     */
    @Transactional
    public List<WorkLog> getAllSprintEvents(Sprint sprint) {
        DateTime start = new DateTime(sprint.getRawStart_date());
        DateTime end = new DateTime(sprint.getRawEnd_date()).plusDays(1);
        List<WorkLog> list = wlRepo.findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
                sprint.getProject().getId(), start.toDate(), end.toDate());
        // Filter out not important events
        return list.stream().filter(workLog -> isSprintRelevant(workLog,sprint)).collect(Collectors.toList());
    }

    @Transactional
    public List<WorkLog> getAllReleaseEvents(Release release) {
        DateTime start = release.getStartDate();
        DateTime end = release.getEndDate();
        List<WorkLog> list = wlRepo.findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
                release.getProject().getId(), start.toDate(), end.toDate());
        // Filter out events for task which are part of this release
        return list.stream().filter(workLog -> isReleaseRelevant(workLog, release)).collect(Collectors.toList());
    }

    public Page<WorkLog> findByProjectId(Long id, Pageable p) {
        return wlRepo.findByProjectId(id, p);
    }

    public Page<WorkLog> findByProjectIdIn(Collection<Long> id, Pageable p) {
        return wlRepo.findByProjectIdIn(id, p);
    }

    public List<WorkLog> findProjectCreateCloseEvents(Project project, boolean all) {
        DateTime beginDate = new DateTime().minusDays(30);
        List<WorkLog> list;
        if (all) {
            list = wlRepo.findByProjectIdOrderByTimeAsc(project.getId());
        } else {
            list = wlRepo.findByProjectIdAndTimeBetweenOrderByTimeAsc(project.getId(), beginDate.toDate(), new Date());
        }
        return list.stream().parallel().filter(workLog -> LogType.CREATE.equals(workLog.getType()) || LogType.REOPEN.equals(workLog.getType())
                || LogType.CLOSED.equals(workLog.getType())).collect(Collectors.toCollection(LinkedList::new));
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
        return taskSrv.save(task);
    }

    private List<DisplayWorkLog> packIntoDisplay(List<WorkLog> list) {
        return list.stream().map(DisplayWorkLog::new).collect(Collectors.toCollection(LinkedList::new));
    }

    private boolean isSprintRelevant(WorkLog workLog, Sprint sprint) {
        LogType type = (LogType) workLog.getType();
        return workLog.getTask().inSprint(sprint) && (type.equals(LogType.DELETED) || type.equals(LogType.LOG)
                || type.equals(LogType.TASKSPRINTREMOVE) || type.equals(LogType.TASKSPRINTADD)
                || type.equals(LogType.ESTIMATE) || type.equals(LogType.CLOSED) || type.equals(LogType.REOPEN));
    }

    private boolean isReleaseRelevant(WorkLog workLog, Release release) {
        LogType type = (LogType) workLog.getType();
        return (release.equals(workLog.getTask().getRelease()) || release.isActive())
                && (type.equals(LogType.CREATE) || type.equals(LogType.DELETED) || type.equals(LogType.LOG)
                || type.equals(LogType.STATUS) || type.equals(LogType.CLOSED) || type.equals(LogType.REOPEN));
    }

    public List<DisplayWorkLog> getTaskDisplayEvents(String id) {
        return packIntoDisplay(wlRepo.findByWorklogtaskIdOrderByTimeLoggedDesc(id));

    }

    public List<WorkLog> getTaskEvents(String taskID) {
        return wlRepo.findByWorklogtaskIdOrderByTimeLoggedDesc(taskID);
    }

    /**
     * Adds event about state changed
     *
     * @param newState
     * @param oldState
     * @param task
     */
    public void changeState(TaskState oldState, TaskState newState, Task task) {
        // StringBuilder message = new StringBuilder(Utils.TABLE);
        // message.append(Utils.changedFromTo(null, oldState.getDescription(),
        // newState.getDescription()));
        // message.append(Utils.TABLE_END);
        addActivityLog(task, Utils.changedFromTo(oldState.getDescription(), newState.getDescription()), LogType.STATUS);

    }
    public void deleteTaskWorklogs(Task task){
        List<WorkLog> workLogList = wlRepo.findByWorklogtaskId(task.getId());
        wlRepo.delete(workLogList);
    }

    public void delete(WorkLog log) {
        wlRepo.delete(log);
    }

    public WorkLog findById(Long id) {
        return wlRepo.findById(id);
    }
}
