package com.qprogramming.tasq.projects;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;

@Controller
public class ProjetController {

	@Autowired
	private ProjectService projSrv;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	MessageSource msg;

	@RequestMapping(value = "project", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") Long id, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		project.setLastVisit(new Date());
		// mark it as last visit
		projSrv.save(project);
		List<Task> taskList = taskSrv.findAllByProject(project);
		model.addAttribute("project", project);
		model.addAttribute("tasks", taskList);
		return "project/details";
	}

	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public String listProjects(Model model) {
		List<Project> projects = projSrv.findAllByUser();
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, true));
		model.addAttribute("projects", projects);
		return "project/list";
	}

	@RequestMapping(value = "project/activate", method = RequestMethod.GET)
	public String activate(@RequestParam(value = "id") Long id,
			HttpServletRequest request, RedirectAttributes ra) {
		Project activated_proj = projSrv.activate(id);
		if (activated_proj != null) {
			MessageHelper.addSuccessAttribute(ra, msg.getMessage(
					"project.activated",
					new Object[] { activated_proj.getName() },
					Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "project/create", method = RequestMethod.GET)
	public NewProjectForm startProjectcreate() {
		return new NewProjectForm();
	}

	@RequestMapping(value = "project/create", method = RequestMethod.POST)
	public String createProject(
			@Valid @ModelAttribute("newProjectForm") NewProjectForm newProjectForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
		if (errors.hasErrors()) {
			return null;
		}
		if (newProjectForm.getProject_id().length() > 5) {
			errors.rejectValue("project_id", "project.idValid");
			return null;
		}
		if (newProjectForm.getProject_id().matches(".*\\d.*")) {
			errors.rejectValue("project_id", "project.idValid.letters");
			return null;
		}
		Utils.setHttpRequest(request);
		String name = newProjectForm.getName();
		if (null != projSrv.findByName(name)) {
			MessageHelper.addErrorAttribute(ra, msg.getMessage(
					"project.exists", new Object[] { name },
					Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		String project_id = newProjectForm.getProject_id();
		if (null != projSrv.findByProjectId(project_id)) {
			errors.rejectValue("project_id", "project.idunique",
					new Object[] { project_id }, "");
			return null;
		}

		Project new_project = newProjectForm.createProject();
		new_project = projSrv.save(new_project);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("project.created", new Object[] { name },
						Utils.getCurrentLocale()));
		return "redirect:/project?id=" + new_project.getId();
	}

}
