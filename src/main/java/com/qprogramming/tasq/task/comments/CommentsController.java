/**
 *
 */
package com.qprogramming.tasq.task.comments;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Locale;

@Controller
public class CommentsController {

    private CommentsRepository commRepo;
    private TaskService taskSrv;
    private WorkLogService wlSrv;
    private MessageSource msg;

    @Autowired
    public CommentsController(CommentsRepository commRepo, TaskService taskSrv,
                              WorkLogService wlSrv, MessageSource msg) {
        this.commRepo = commRepo;
        this.taskSrv = taskSrv;
        this.wlSrv = wlSrv;
        this.msg = msg;
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/comments", method = RequestMethod.POST)
    public ResponseEntity<?> getComments(@PathVariable(value = "id") String id, HttpServletRequest request, RedirectAttributes ra) {
        Task task = taskSrv.findById(id);
        if (task == null) {
            return ResponseEntity.badRequest().body(msg.getMessage("task.notexists", null, Utils.getCurrentLocale()));
        }
        return ResponseEntity.ok(commRepo.findByTaskIdOrderByDateDesc(id));
    }


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
                    "comment.notallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
        } else {
            if (messageValid(message, ra)) {
                Hibernate.initialize(task.getComments());
                Comment comment = new Comment();
                comment.setTask(task);
                comment.setAuthor(currAccount);
                comment.setDate(new Date());
                comment.setMessage(message);
                comment = commRepo.save(comment);
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

    @RequestMapping(value = "/task/{taskId}/comment/delete", method = RequestMethod.GET)
    public String deleteComment(@PathVariable String taskId,
                                @RequestParam(value = "id") Long id, HttpServletRequest request,
                                RedirectAttributes ra) {
        Utils.setHttpRequest(request);
        Locale locale = Utils.getCurrentLocale();
        Task task = taskSrv.findById(taskId);
        if (!isCommentAllowed(task)) {
            MessageHelper.addWarningAttribute(ra, msg.getMessage(
                    "comment.editNotallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
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
    public String editComment(@RequestParam(value = "task_id") String taskId,
                              @RequestParam(value = "comment_id") Long id,
                              @RequestParam(value = "message") String message,
                              HttpServletRequest request, RedirectAttributes ra) {
        Utils.setHttpRequest(request);
        Locale locale = Utils.getCurrentLocale();
        Task task = taskSrv.findById(taskId);
        if (!isCommentAllowed(task)) {
            MessageHelper.addWarningAttribute(ra, msg.getMessage(
                    "comment.editNotallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
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
        if (StringUtils.isEmpty(message)) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("comment.empty", null, locale));
            return false;
        }
//        Removed TASQ-250
//        else if (Utils.containsHTMLTags(message)) {
//            MessageHelper.addErrorAttribute(ra,
//                    msg.getMessage("comment.htmlTag", null, locale));
//            return false;
//        }
        else if (message.length() > 4000) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("comment.tooLong", new Object[]{message.length()}, locale));
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
