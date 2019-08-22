package com.qprogramming.tasq.agile;

import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.WorkLog;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qprogramming.tasq.agile.AgileData.ALL;
import static com.qprogramming.tasq.agile.AgileData.CLOSED;

@Service
public class AgileService {

    private SprintRepository sprintRepo;
    private ReleaseRepository releaseRepo;
    private TaskService taskSrv;

    @Autowired
    public AgileService(SprintRepository sprintRepo, ReleaseRepository releaseRepo, TaskService taskSrv) {
        this.sprintRepo = sprintRepo;
        this.releaseRepo = releaseRepo;
        this.taskSrv = taskSrv;
    }

    public Sprint findByProjectIdAndActiveTrue(Long id) {
        return sprintRepo.findByProjectIdAndActiveTrue(id);
    }

    public List<Sprint> findByProjectIdAndFinished(String id, boolean finished) {
        return sprintRepo.findByProjectProjectIdAndFinished(id, finished);
    }

    public List<Sprint> findByProjectId(Long id) {
        return sprintRepo.findByProjectId(id);
    }

    public Sprint findById(Long sprintID) {
        return sprintRepo.findById(sprintID);
    }

    public Sprint save(Sprint sprint) {
        return sprintRepo.save(sprint);
    }

    public void delete(Sprint sprint) {
        sprintRepo.delete(sprint);
    }

    public Sprint findByProjectIdAndSprintNo(Long id, Long sprintNo) {
        return sprintRepo.findByProjectIdAndSprintNo(id, sprintNo);
    }

    public List<DisplaySprint> convertToDisplay(List<Sprint> projectSprints) {
        return projectSprints.stream().map(DisplaySprint::new).collect(Collectors.toCollection(LinkedList::new));
    }

    public Map<String, Float> fillTimeBurndownMap(List<WorkLog> wrkList, DateTime startTime, DateTime endTime) {
        int sprintDays = Days.daysBetween(startTime, endTime).getDays() + 1;
        Map<LocalDate, Period> timeBurndownMap = fillTimeMap(wrkList);
        Map<String, Float> resultsBurned = new LinkedHashMap<String, Float>();
        for (int i = 0; i < sprintDays; i++) {
            LocalDate date = new LocalDate(startTime.plusDays(i));
            if (date.isAfter(LocalDate.now())) {
                resultsBurned.put(date.toString(), 0f);
            } else {
                Period value = timeBurndownMap.get(new LocalDate(date));
                if (value != null) {
                    Float result = Utils.getFloatValue(value);
                    resultsBurned.put(date.toString(), result);
                } else {
                    resultsBurned.put(date.toString(), 0f);
                }
            }
        }
        return resultsBurned;
    }

    /**
     * Fills time map with worklogs in format <Date, Period Burned> Only events
     * with before present day are added
     *
     * @param worklogList
     * @return
     **/
    private Map<LocalDate, Period> fillTimeMap(List<WorkLog> worklogList) {
        Map<LocalDate, Period> burndownMap = new LinkedHashMap<LocalDate, Period>();
        for (WorkLog workLog : worklogList) {
            if (workLog.getActivity() != null) {
                LocalDate dateLogged = new LocalDate(workLog.getRawTime());
                Period value = burndownMap.get(dateLogged);
                if (value == null) {
                    value = workLog.getActivity();
                } else {
                    value = PeriodHelper.plusPeriods(value, workLog.getActivity());
                }
                burndownMap.put(dateLogged, value);
            }
        }
        return burndownMap;
    }

    public Release findByProjectIdAndRelease(Long id, String releaseNo) {
        return releaseRepo.findByProjectIdAndRelease(id, releaseNo);
    }

    public List<Release> findReleaseByProjectIdOrderByDateDesc(Long id) {
        return releaseRepo.findByProjectIdOrderByEndDateDesc(id);
    }

    public Release save(Release release) {
        return releaseRepo.save(release);
    }

    /**
     * Fetches last release from project
     *
     * @param id
     * @return
     */
    public Release findLastReleaseByProjectId(Long id) {
        List<Release> list = findReleaseByProjectIdOrderByDateDesc(id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public boolean taskInActiveSprint(Task task) {
        if (!task.isInSprint()) {
            return false;
        } else {

            for (Sprint sprint : task.getSprints()) {
                if (sprint.isActive()) {
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Set tasks based if task was finished in endTime or not. If was finished before
     * , it will be moved to CLOSED tasks, otherwise it will be in ALL
     *
     * @param endTime - end time before tasks should be finished
     * @param tasks   - task liest
     */
    public void setTasksByStatus(AgileData data, DateTime endTime, List<Task> tasks) {
        for (Task task : tasks) {
            DateTime finishDate = null;
            if (task.getRawFinishDate() != null) {
                finishDate = new DateTime(task.getRawFinishDate());
            }
            taskSrv.addSubtaskTimers(task);
            if (task.getState().equals(TaskState.CLOSED)
                    && (finishDate != null && endTime.isAfter(finishDate))) {
                data.getTasks().get(CLOSED)
                        .add(new DisplayTask(task));
            } else {
                data.getTasks().get(ALL)
                        .add(new DisplayTask(task));
            }
        }
    }
}
