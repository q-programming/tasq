package com.qprogramming.tasq.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
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
			// TODO check if any active sprints, if not redirect to backlog
			// instead
			if (project.getAgile_type().equals(Project.AgileType.KANBAN)) {
				return "/kanban/board";
			} else if (project.getAgile_type().equals(Project.AgileType.SCRUM)) {
				// /TODO check for active
				return "redirect:/" + project.getProjectId() + "/scrum/board";
			}
		}
		return "";
	}

	@RequestMapping(value = "boards", method = RequestMethod.GET)
	public String listBoards(Model model) {
		List<Project> projects;
		if (Roles.isAdmin()) {
			projects = projSrv.findAll();
		} else {
			projects = projSrv.findAllByUser();
		}
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
						.getActive_project(), true));
		model.addAttribute("projects", projects);
		return "agile/list";
	}

	@RequestMapping(value = "/agile/order", method = RequestMethod.POST)
	public @ResponseBody
	boolean saveOrder(@RequestParam(value = "ids[]") String[] ids,
			@RequestParam(value = "project") Long project,
			HttpServletResponse response) {
		long order = 0;
		List<Task> allTasks = taskSrv.findAllByProjectId(project);
		Map<String, Task> map = new HashMap<String, Task>();
		for (Task i : allTasks) {
			map.put(i.getId(), i);
		}
		List<Task> taskList = new ArrayList<Task>();
		for (String taskID : Arrays.asList(ids)) {
			Task task = map.get(taskID);
			task.setTaskOrder(order);
			taskList.add(task);
			order++;
		}
		taskSrv.save(taskList);
		return true;
	}

}
