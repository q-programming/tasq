package com.qprogramming.tasq.agile;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
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
}
