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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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
                Utils.getCurrentAccount().getActiveProject(), true));
        model.addAttribute("projects", projects);
        return "agile/list";
    }

    @RequestMapping(value = "{id}/agile/board", method = RequestMethod.GET)
    public String showBoard(@PathVariable String id, Model model,
                            HttpServletRequest request, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project != null) {
            return "redirect:/" + project.getProjectId() + "/" + project.getAgile().getCode() + "/board";
        }
        return "";
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
            List<Task> result = new LinkedList<>();
            if (sprintID != null) {
                taskList = taskSrv.findAllBySprintId(project, sprintID);
            } else {
                taskList = taskSrv.findByProjectAndOpen(project);
            }
            for (Task task : taskList) {
                if (task.getSubtasks() > 0) {
                    result.addAll(taskSrv.findSubtasks(task).stream().map(this::eliminateHTML).collect(Collectors.toList()));
                } else {
                    result.add(eliminateHTML(task));
                }
            }
            model.addAttribute("tasks", taskSrv.convertToDisplay(result, true));
            model.addAttribute("project", project);
        }
        return "/agile/print";
    }

    private Task eliminateHTML(Task task) {
        String description = task.getDescription().replaceAll("<img[^>]*>", "").replaceAll("<a[^>]*>", "").replaceAll("</a>", "");
        task.setDescription(description);
        return task;
    }
}
