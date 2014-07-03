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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class KanbanController {
	private static final Logger LOG = LoggerFactory
			.getLogger(KanbanController.class);
	@Autowired
	private TaskService taskSrv;

	@Autowired
	private ProjectService projectSrv;

	@Autowired
	private AccountService accSrv;

	@Autowired
	private WorkLogService wlSrv;

	@Autowired
	private MessageSource msg;

	@RequestMapping(value = "/kanban", method = RequestMethod.GET)
	public String listTasks(Model model, HttpServletRequest request) {
		Project project = projectSrv.findUserActiveProject();
		model.addAttribute("project", project);
		// Get active or choosen project
		List<Task> taskList = new LinkedList<Task>();
		taskList = taskSrv.findAllByProject(project);
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID, false));
		model.addAttribute("tasks", taskList);
		return "agile/kanban";
	}

}
