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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class KanbanController {
	private static final Logger LOG = LoggerFactory
			.getLogger(KanbanController.class);
	private TaskService taskSrv;
	private ProjectService projSrv;
	private WorkLogService wlSrv;
	private MessageSource msg;

	@Autowired
	public KanbanController(TaskService taskSrv, ProjectService projSrv,
			WorkLogService wlSrv, MessageSource msg) {
		this.taskSrv = taskSrv;
		this.projSrv = projSrv;
		this.wlSrv = wlSrv;
		this.msg = msg;
	}

	@RequestMapping(value = "{id}/kanban/board", method = RequestMethod.GET)
	public String showBoard(@PathVariable String id, Model model,
			HttpServletRequest request, RedirectAttributes ra) {
		Project project = projSrv.findByProjectId(id);
		if (project != null) {
			if (!project.getParticipants().contains(Utils.getCurrentAccount())
					&& !Roles.isAdmin()) {
				throw new TasqAuthException(msg);
			}
			model.addAttribute("project", project);
			List<Task> taskList = new LinkedList<Task>();
			taskList = taskSrv.findAllByProject(project);
			Collections.sort(taskList, new TaskSorter(
					TaskSorter.SORTBY.PRIORITY, false));
			List<DisplayTask> resultList = taskSrv.convertToDisplay(taskList);
			model.addAttribute("tasks", resultList);
			return "/kanban/board";
		}
		return "";
	}

}
