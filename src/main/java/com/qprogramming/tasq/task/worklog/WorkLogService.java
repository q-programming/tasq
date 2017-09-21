/**
 *
 */
package com.qprogramming.tasq.task.worklog;

import com.qprogramming.tasq.agile.Release;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.WorkLogSorter;
import com.qprogramming.tasq.task.Task;
import org.apache.commons.lang3.StringUtils;
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
    private EventsService eventSrv;

    @Autowired
    public WorkLogService(WorkLogRepository wlRepo, EventsService eventSrv) {
        this.wlRepo = wlRepo;
        this.eventSrv = eventSrv;
    }

    @Transactional
    public Task addTimedWorkLog(Task loggedTask, String msg, Date when, Period remaining, Period activity, LogType type) {
        WorkLog wl = new WorkLog();
        wl.setTask(loggedTask);
        wl.setProject_id(loggedTask.getProject().getId());
        wl.setAccount(Utils.getCurrentAccount());
        wl.setTimeLogged(new Date());
        wl.setTime(when);
        wl.setType(type);
        wl.setMessage(msg);
        wl.setActivity(activity);
        wl = wlRepo.save(wl);
        Hibernate.initialize(loggedTask.getWorklog());
        loggedTask.addWorkLog(wl);
        if (remaining == null) {
            loggedTask.reduceRemaining(activity);
        } else {
            loggedTask.setRemaining(remaining);
        }
        loggedTask.addLoggedWork(activity);
        loggedTask.setLastUpdate(new Date());
        eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), when);
        return loggedTask;
    }

    @Transactional
    public Task addDatedWorkLog(Task loggedTask, String msg, Date when, LogType type) {
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
        eventSrv.addWatchEvent(wl, msg, when);
        return loggedTask;
    }

    /**
     * @param loggedTask
     * @param msg
     * @param type
     */
    @Transactional
    public void addActivityLog(Task loggedTask, String msg, LogType type) {
        WorkLog wl = new WorkLog();
        wl.setTask(loggedTask);
        wl.setProject_id(loggedTask.getProject().getId());
        wl.setAccount(Utils.getCurrentAccount());
        wl.setTime(new Date());
        wl.setTimeLogged(new Date());
        wl.setType(type);
        if (StringUtils.isNotBlank(msg)) {
            wl.setMessage(msg);
        }
        wl = wlRepo.save(wl);
        Hibernate.initialize(loggedTask.getWorklog());
        loggedTask.addWorkLog(wl);
        loggedTask.setLastUpdate(new Date());
        eventSrv.addWatchEvent(wl, msg, new Date());
    }

    @Transactional
    public void addActivityPeriodLog(Task loggedTask, String msg, Period activity, LogType type) {
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
        eventSrv.addWatchEvent(wl, PeriodHelper.outFormat(activity), new Date());
    }

    /**
     * Add worklog without task ( for example for project only )
     *
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
        wlRepo.save(wl);
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
     * @param sprint sprint for which all events must be fetched
     * @return
     */
    @Transactional
    public List<WorkLog> getAllSprintEvents(Sprint sprint) {
        DateTime start = new DateTime(sprint.getRawStart_date());
        DateTime end = new DateTime(sprint.getRawEnd_date()).plusDays(1);
        List<WorkLog> list = wlRepo.findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
                sprint.getProject().getId(), start.toDate(), end.toDate());
        // Filter out not important events
        return list.stream().filter(workLog -> isSprintRelevant(workLog, sprint)).collect(Collectors.toList());
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

    public List<WorkLog> findProjectCreateCloseLogEvents(Project project) {
        return wlRepo.findByProjectIdOrderByTimeAsc(project.getId())
                .stream()
                .parallel()
                .filter(workLog -> LogType.CREATE.equals(workLog.getType())
                        || LogType.REOPEN.equals(workLog.getType())
                        || LogType.CLOSED.equals(workLog.getType())
                        || LogType.LOG.equals(workLog.getType()))
                .collect(Collectors.toCollection(LinkedList::new));
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


    public void deleteTaskWorklogs(Task task) {
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
