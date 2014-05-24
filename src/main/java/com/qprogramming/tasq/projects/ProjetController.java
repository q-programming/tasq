package com.qprogramming.tasq.projects;

import java.util.Date;

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

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class ProjetController {

	@Autowired
	private ProjectService projSrv;

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
			return "redirect:" + request.getHeader("Referer");
		}
		project.setLastVisit(new Date());
		//mark it as last visit
		projSrv.save(project);
		model.addAttribute("project", project);
		return "project/details";
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
		Utils.setHttpRequest(request);
		String name = newProjectForm.getName();
		if (null != projSrv.findByName(name)) {
			MessageHelper.addErrorAttribute(ra, msg.getMessage(
					"project.exists", new Object[] { name },
					Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
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
