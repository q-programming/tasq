package com.qprogramming.tasq.task.link;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class TaskLinkController {
	@Autowired
	private TaskService taskSrv;

	@Autowired
	private WorkLogService wlSrv;

	@Autowired
	private MessageSource msg;

	@Autowired
	private TaskLinkService linkService;

	@Transactional
	@RequestMapping(value = "/task/link", method = RequestMethod.POST)
	public String linkTasks(@RequestParam(value = "taskA") String A,
			@RequestParam(value = "taskB") String B,
			@RequestParam(value = "link") TaskLinkType linkType,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// get tasks
		Task task = taskSrv.findById(A);
		TaskLink link = linkService.findLink(A, B, linkType);
		String linkTXT = msg.getMessage(linkType.getCode(), null,
				Utils.getCurrentLocale());
		if (link != null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("task.link.error.linked", new Object[] { A,
							linkTXT, B }, Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		linkService.save(new TaskLink(A, B, linkType));
		wlSrv.addWorkLogNoTask(A + " - " + B, task.getProject(),
				LogType.TASK_LINK_DEL);
		MessageHelper.addSuccessAttribute(ra, msg.getMessage(
				"task.link.linked", new Object[] { A, linkTXT, B },
				Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "/task/deletelink", method = RequestMethod.GET)
	public String deleteLinks(@RequestParam(value = "taskA") String A,
			@RequestParam(value = "taskB") String B,
			@RequestParam(value = "link") TaskLinkType linkType,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// get tasks
		Task task = taskSrv.findById(A);
		TaskLink link = linkService.findLink(A, B, linkType);
		String linkTXT = msg.getMessage(linkType.getCode(), null,
				Utils.getCurrentLocale());
		if (link == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("task.link.error.notFound", new Object[] {
							A, linkTXT, B }, Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		linkService.delete(link);
		wlSrv.addWorkLogNoTask(A + " - " + B, task.getProject(),
				LogType.TASK_LINK_DEL);
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("task.link.deleted", new Object[] { A, linkTXT,
						B }, Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

}
