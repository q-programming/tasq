package com.qprogramming.tasq.task;

import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.DisplaySprint;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.sorters.DisplayTaskSorter;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TaskRestController {

    private TaskService taskSrv;
    private WatchedTaskService watchSrv;
    private AgileService sprintSrv;
    private ProjectService projSrv;
    private WorkLogService wlSrv;

    @Autowired
    public TaskRestController(TaskService taskSrv, WatchedTaskService watchSrv, AgileService sprintSrv,
                              ProjectService projSrv, WorkLogService wlSrv) {
        this.taskSrv = taskSrv;
        this.watchSrv = watchSrv;
        this.sprintSrv = sprintSrv;
        this.projSrv = projSrv;
        this.wlSrv = wlSrv;
    }

    @Transactional
    @RequestMapping(value = "/task/getSprints", method = RequestMethod.GET)
    public List<DisplaySprint> getSprints(@RequestParam String taskID) {
        List<Sprint> result = new LinkedList<Sprint>();
        result.addAll(taskSrv.getTaskSprints(taskID));
        return sprintSrv.convertToDisplay(result);
        // return taskSrv.getTaskSprints(taskID);
    }

    /**
     * Returns int how many accounts is currently watching task with id
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/task/watchersCount", method = RequestMethod.GET)
    public int watchersCount(@RequestParam(value = "id") String id) {
        WatchedTask watched = watchSrv.getByTask(id);
        return watched != null ? watched.getWatchers().size() : 0;
    }

    @RequestMapping(value = "/task/getSubTasks", method = RequestMethod.GET)
    public List<DisplayTask> showSubTasks(@RequestParam String taskID, HttpServletResponse response) {
        response.setContentType("application/json");
        List<Task> allSubTasks = taskSrv.findSubtasks(taskID);
        List<DisplayTask> result = allSubTasks.stream().map(DisplayTask::new).collect(Collectors.toList());
        Collections.sort(result, new DisplayTaskSorter(DisplayTaskSorter.SORTBY.ID, true));
        return result;
    }

    /**
     * Returns List of all task for project which paches term criteria
     *
     * @param projectID
     * @param taskID
     * @param term
     * @param response
     * @return
     */
    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
    public List<DisplayTask> showTasks(@RequestParam Long projectID, @RequestParam(required = false) String taskID,
                                       @RequestParam String term, HttpServletResponse response) {
        response.setContentType("application/json");
        Project project = projSrv.findById(projectID);
        List<Task> allTasks = taskSrv.findAllByProject(project);
        if (taskID != null) {
            Task task = taskSrv.findById(taskID);
            allTasks.remove(task);
        }
        List<DisplayTask> result = new ArrayList<DisplayTask>();
        for (Task task : allTasks) {
            if (term == null) {
                result.add(new DisplayTask(task));
            } else {
                if (StringUtils.containsIgnoreCase(task.getName(), term)
                        || StringUtils.containsIgnoreCase(task.getId(), term)) {
                    result.add(new DisplayTask(task));
                }
            }
        }
        return result;
    }

    @RequestMapping(value = "/task/getWorklogs", method = RequestMethod.GET)
    public List<DisplayWorkLog> getWorklogs(@RequestParam String taskID) {
        return wlSrv.getTaskDisplayEvents(taskID);
    }

}
