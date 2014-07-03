package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.SprintSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;

@Controller
public class SprintController {

	@Autowired
	ProjectService projSrv;

	@Autowired
	TaskService taskSrv;

	@Autowired
	SprintRepository sprintRepo;

	@Autowired
	private MessageSource msg;

	@RequestMapping(value = "{id}/scrum/board", method = RequestMethod.GET)
	public String listTasks(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			model.addAttribute("project", project);
			Sprint sprint = sprintRepo.findByProjectIdAndActive(
					project.getId(), true);
			model.addAttribute("projectID", project.getProjectId());
			if (sprint == null) {
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.sprint.noActive", null,
								Utils.getCurrentLocale()));
				return "redirect:/" + project.getProjectId() + "/scrum/backlog";
			}
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllBySprint(project, sprint);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			model.addAttribute("sprint", sprint.getSprint_no());
			model.addAttribute("tasks", taskList);
			return "/scrum/board";
		}
		return "";
	}

	@RequestMapping(value = "/{id}/scrum/backlog", method = RequestMethod.GET)
	public String showBacklog(@PathVariable String id, Model model,
			HttpServletRequest request) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			model.addAttribute("project", project);
			List<Task> resultList = new LinkedList<Task>();
			List<Task> taskList = taskSrv.findAllByProject(project);
			// Don't show closed tasks in backlog view
			for (Task task : taskList) {
				if (!task.getState().equals(TaskState.CLOSED)) {
					resultList.add(task);
				}
			}
			List<Sprint> sprintList = sprintRepo.findByProjectIdAndFinished(
					project.getId(), false);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID,
					false));
			Collections.sort(sprintList, new SprintSorter());

			model.addAttribute("tasks", taskList);
			model.addAttribute("sprints", sprintList);
		}
		return "/scrum/backlog";
	}

	@RequestMapping(value = "/{id}/scrum/create", method = RequestMethod.POST)
	public String createSprint(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		List<Sprint> sprints = sprintRepo.findByProjectId(project.getId());
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setSprint_no((long) sprints.size() + 1);
		sprintRepo.save(sprint);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.createdSprint", null,
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/{id}/scrum/sprintAssign", method = RequestMethod.POST)
	public String assignSprint(@PathVariable String id,
			@RequestParam(value = "taskID") String taskID,
			@RequestParam(value = "sprintID") Long sprintID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Sprint sprint = sprintRepo.findById(sprintID);
		Task task = taskSrv.findById(taskID);
		task.setSprint(sprint);
		taskSrv.save(task);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("agile.task2Sprint", new Object[] {
						task.getId(), sprint.getSprint_no() },
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/{id}/scrum/sprintRemove", method = RequestMethod.POST)
	public String removeFromSprint(@PathVariable String id,
			@RequestParam(value = "taskID") String taskID, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Task task = taskSrv.findById(taskID);
		Sprint sprint = sprintRepo.findById(task.getSprint().getId());
		if (!sprint.isActive()) {
			task.setSprint(null);
			taskSrv.save(task);
			MessageHelper.addSuccessAttribute(
					ra,
					msg.getMessage("agile.taskRemoved",
							new Object[] { task.getId() },
							Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

}
