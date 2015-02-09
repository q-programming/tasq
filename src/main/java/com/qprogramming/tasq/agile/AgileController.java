package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
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
}
