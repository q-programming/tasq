package com.qprogramming.tasq.agile;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class AgileController {

    @Autowired
    ProjectService projSrv;

    @Autowired
    TaskService taskSrv;

    @RequestMapping(value = "boards", method = RequestMethod.GET)
    public String listBoards(Model model) {
        List<Project> projects;
        if (Roles.isAdmin()) {
            projects = projSrv.findAll();
        } else {
            projects = projSrv.findAllByUser();
        }
        Collections.sort(projects, new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
                Utils.getCurrentAccount().getActive_project(), true));
        model.addAttribute("projects", projects);
        return "agile/list";
    }

    @RequestMapping(value = "/agile/order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> saveOrder(@RequestParam(value = "ids[]") String[] ids,
                                             @RequestParam(value = "project") Long project, HttpServletResponse response) {
        int order = 0;
        List<Task> allTasks = taskSrv.findAllByProjectId(project);
        // build map of all tasks
        Map<String, Task> map = new HashMap<>();
        for (Task i : allTasks) {
            map.put(i.getId(), i);
        }
        List<Task> taskList = new LinkedList<>();
        List<Long> newTaskOrder = new LinkedList<>();
        for (String taskID : Arrays.asList(ids)) {
            Task task = map.get(taskID);
            newTaskOrder.add(task.getTaskOrder());
            taskList.add(task);
        }
        Collections.sort(newTaskOrder);
        for (Task task : taskList) {
            task.setTaskOrder(newTaskOrder.get(order));
            order++;
        }
        taskSrv.save(taskList);
        return ResponseEntity.ok(true);
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "{id}/agile/cardsprint", method = RequestMethod.GET)
    public String showBoard(@PathVariable String id, @RequestParam(name = "sprint", required = false) Long sprintID, Model model) {
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            List<Task> taskList;
            if (sprintID != null) {
                taskList = taskSrv.findAllBySprintId(project, sprintID);
            } else {
                taskList = taskSrv.findByProjectAndOpen(project);
            }
            taskList.stream().forEach(task -> task.setDescription(eliminateHTML(task)));
            model.addAttribute("tasks", taskSrv.convertToDisplay(taskList, true));
            model.addAttribute("project", project);
        }
        return "/agile/print";
    }

    private String eliminateHTML(Task task) {
        return task.getDescription().replaceAll("<img[^>]*>", "").replaceAll("<a[^>]*>", "").replaceAll("</a>", "");
    }
}
