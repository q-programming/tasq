/**
 * 
 */
package com.qprogramming.tasq.task;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.ProjectService;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Controller
public class TaskController {

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private ProjectService projectSrv;

	@RequestMapping(value = "task/create", method = RequestMethod.GET)
	public NewTaskForm startTaskCreate(Model model) {
		model.addAttribute("projects", projectSrv.findAllByUser());
		return new NewTaskForm();
	}

	@RequestMapping(value = "task/create", method = RequestMethod.POST)
	public String createTask(
			@Valid @ModelAttribute("newTaskForm") NewTaskForm newProjectForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
		if (errors.hasErrors()) {
			return null;
		}

		return "tasks/list";
	}

}
