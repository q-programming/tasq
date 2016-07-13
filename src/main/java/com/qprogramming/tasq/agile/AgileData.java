package com.qprogramming.tasq.agile;

import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import org.joda.time.DateTime;

import java.util.*;

public class AgileData {

    public static final String ALL = "ALL";
    public static final String CLOSED = "CLOSED";
    protected Map<String, List<DisplayTask>> tasks;
    protected Map<String, Float> timeBurned;
    private String message;
    private List<DisplayWorkLog> worklogs;
    private String totalTime;

    public AgileData() {
        timeBurned = new LinkedHashMap<>();
        tasks = new HashMap<>();
        tasks.put(CLOSED, new LinkedList<>());
        tasks.put(ALL, new LinkedList<>());
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DisplayWorkLog> getWorklogs() {
        return worklogs;
    }

    public void setWorklogs(List<DisplayWorkLog> worklogs) {
        this.worklogs = worklogs;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Map<String, List<DisplayTask>> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, List<DisplayTask>> tasks) {
        this.tasks = tasks;
    }

    public Map<String, Float> getTimeBurned() {
        return timeBurned;
    }

    public void setTimeBurned(Map<String, Float> timeBurned) {
        this.timeBurned = timeBurned;
    }

    public void addTime(Integer time) {
        totalTime += time;
    }

    /**
     * Set tasks based if task was finished in endTime or not. If was finished before
     * , it will be moved to CLOSED tasks, otherwise it will be in ALL
     *
     * @param endTime - end time before tasks should be finished
     * @param tasks   - task liest
     */
    public void setTasksByStatus(DateTime endTime, List<Task> tasks) {
        for (Task task : tasks) {
            DateTime finishDate = null;
            if (task.getFinishDate() != null) {
                finishDate = new DateTime(task.getFinishDate());
            }
            if (task.getState().equals(TaskState.CLOSED)
                    && (finishDate != null && endTime.isAfter(finishDate))) {
                this.getTasks().get(CLOSED)
                        .add(new DisplayTask(task));
            } else {
                this.getTasks().get(ALL)
                        .add(new DisplayTask(task));
            }
        }
    }

}
