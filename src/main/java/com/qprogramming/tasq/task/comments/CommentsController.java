/**
 * 
 */
package com.qprogramming.tasq.task.comments;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class CommentsController {

	private static final String NEW_LINE = "\n";

	@Autowired
	private CommentsRepository commRepo;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private ProjectService projServ;

	// @Autowired TODO
	// private WatchedTaskService watchedTaskServ;
	//
	@Autowired
	private WorkLogService wlSrv;

	@Autowired
	private SessionLocaleResolver localeResolver;

	@Autowired
	private MessageSource msg;

	@Transactional
	@RequestMapping(value = "/task/comment", method = RequestMethod.POST)
	public String addComment(@RequestParam(value = "task_id") String id,
			@RequestParam(value = "message") String message,
			HttpServletRequest request, RedirectAttributes ra) {
		Utils.setHttpRequest(request);
		Account currAccount = Utils.getCurrentAccount();
		Locale locale = Utils.getCurrentLocale();
		Task task = taskSrv.findById(id);
		if (!isCommentAllowed(task)) {
			MessageHelper.addWarningAttribute(ra, msg.getMessage(
					"comment.notallowed", new Object[] { ((TaskState) task
							.getState()).getDescription() }, locale));
		} else {
			if (messageValid(message, ra)) {
				Hibernate.initialize(task.getComments());
				Comment comment = new Comment();
				comment.setTask(task);
				comment.setAuthor(currAccount);
				comment.setDate(new Date());
				comment.setMessage(message);
				commRepo.save(comment);
				task.addComment(comment);
				taskSrv.save(task);
				wlSrv.addActivityLog(task, message, LogType.COMMENT);
				// search for watchers and send notifications
				// TODO
				MessageHelper.addSuccessAttribute(ra,
						msg.getMessage("comment.added", null, locale));
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/{task_id}/comment/delete", method = RequestMethod.GET)
	public String deleteComment(@PathVariable String task_id,
			@RequestParam(value = "id") Long id, HttpServletRequest request,
			RedirectAttributes ra) {
		Utils.setHttpRequest(request);
		Locale locale = Utils.getCurrentLocale();
		Task task = taskSrv.findById(task_id);
		if (!isCommentAllowed(task)) {
			MessageHelper.addWarningAttribute(ra, msg.getMessage(
					"comment.editNotallowed", new Object[] { ((TaskState) task
							.getState()).getDescription() }, locale));
		} else {
			Comment comment = commRepo.findById(id);
			if (comment == null
					|| !comment.getAuthor().equals(Utils.getCurrentAccount())) {
				MessageHelper.addErrorAttribute(ra,
						msg.getMessage("main.generalError", null, locale));
			} else {
				comment.setMessage(null);
				comment.setDate_edited(null);
				commRepo.save(comment);
				MessageHelper.addSuccessAttribute(ra,
						msg.getMessage("comment.deleted", null, locale));
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/task/comment/edit", method = RequestMethod.POST)
	public String editComment(@RequestParam(value = "task_id") String task_id,
			@RequestParam(value = "comment_id") Long id,
			@RequestParam(value = "message") String message,
			HttpServletRequest request, RedirectAttributes ra) {
		Utils.setHttpRequest(request);
		Locale locale = Utils.getCurrentLocale();
		Task task = taskSrv.findById(task_id);
		if (!isCommentAllowed(task)) {
			MessageHelper.addWarningAttribute(ra, msg.getMessage(
					"comment.editNotallowed", new Object[] { ((TaskState) task
							.getState()).getDescription() }, locale));
		} else {
			if (messageValid(message, ra)) {
				Comment comment = commRepo.findById(id);
				comment.setMessage(message);
				comment.setDate_edited(new Date());
				commRepo.save(comment);
				MessageHelper.addSuccessAttribute(ra,
						msg.getMessage("comment.edited", null, locale));
			}
		}
		return "redirect:" + request.getHeader("Referer");
	}

	/**
	 * Returns true if message is not empty and doesn't contain HTML tags
	 * 
	 * @param message
	 * @param ra
	 * @return
	 */
	private boolean messageValid(String message, RedirectAttributes ra) {
		Locale locale = Utils.getCurrentLocale();
		if (message == null || message.equals("")) {
			MessageHelper.addErrorAttribute(ra,
					msg.getMessage("comment.empty", null, locale));
			return false;
		} else if (Utils.containsHTMLTags(message)) {
			MessageHelper.addErrorAttribute(ra,
					msg.getMessage("comment.htmlTag", null, locale));
			return false;
		}
		return true;
	}

	/**
	 * Checks if it's allowed to add comment. By default it's not if task is
	 * closed
	 * 
	 * @param task
	 * @return
	 */
	private boolean isCommentAllowed(Task task) {
		return !task.getState().equals(TaskState.CLOSED);
	}

}
