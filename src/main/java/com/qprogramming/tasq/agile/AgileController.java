package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.TaskSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

@Controller
public class AgileController {

	@Autowired
	ProjectService projSrv;

	@Autowired
	TaskService taskSrv;

	@RequestMapping(value = "/agile/{id}/", method = RequestMethod.GET)
	public String listTasks(@PathVariable Long id, Model model,
			HttpServletRequest request) {
		Project project = projSrv.findById(id);
		if (project != null) {
			model.addAttribute("project", project);
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllByProject(project);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			model.addAttribute("tasks", taskList);
			if (project.getAgile_type().equals(Project.AgileType.KANBAN)) {
				return "agile/kanban";
			} else if (project.getAgile_type().equals(Project.AgileType.SCRUM)) {
				return "agile/scrum";
			}
		}
		return "";
	}
}
