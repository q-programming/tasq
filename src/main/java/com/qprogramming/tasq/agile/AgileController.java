package com.qprogramming.tasq.agile;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
	public @ResponseBody boolean saveOrder(@RequestParam(value = "ids[]") String[] ids,
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
		return true;
	}

}
