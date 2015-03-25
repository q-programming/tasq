package com.qprogramming.tasq.agile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class KanbanController {
	private static final Logger LOG = LoggerFactory
			.getLogger(KanbanController.class);
	private TaskService taskSrv;
	private ProjectService projSrv;
	private WorkLogService wlSrv;
	private MessageSource msg;
	private ReleaseRepository releaseRepo;

	@Autowired
	public KanbanController(TaskService taskSrv, ProjectService projSrv,
			WorkLogService wlSrv, MessageSource msg,
			ReleaseRepository releaseRepo) {
		this.taskSrv = taskSrv;
		this.projSrv = projSrv;
		this.wlSrv = wlSrv;
		this.msg = msg;
		this.releaseRepo = releaseRepo;
	}

	@RequestMapping(value = "{id}/kanban/board", method = RequestMethod.GET)
	public String showBoard(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canEdit(project)) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllWithoutRelease(project);
			Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ORDER,
					true));
			List<DisplayTask> resultList = taskSrv.convertToDisplay(taskList);
			model.addAttribute("tasks", resultList);
			return "/kanban/board";
		}
		return "";
	}

	@Transactional
	@RequestMapping(value = "/kanban/release", method = RequestMethod.POST)
	public String newRelease(@RequestParam(value = "id") String id,
			@RequestParam(value = "release") String releaseNo,
			@RequestParam(value = "comment", required = false) String comment,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!projSrv.canAdminister(project)) {
				throw new TasqAuthException(msg);
			}
			List<Task> taskList = taskSrv.findAllToRelease(project);
			if(taskList.isEmpty()){
				MessageHelper.addWarningAttribute(
						ra,
						msg.getMessage("agile.newRelease.noTasks", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			Release release = new Release(project, releaseNo, comment);
			release = releaseRepo.save(release);
			for (Task task : taskList) {
				task.setRelease(release);
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "{id}/kanban/reports", method = RequestMethod.GET)
	public String showReport(
			@PathVariable String id,
			@RequestParam(value = "release", required = false) String releaseNo,
			Model model, HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			List<Release> releases = releaseRepo.findAll();
			model.addAttribute("project", project);
			model.addAttribute("releases", releases);
		}
		return "/kanban/reports";
	}

}
