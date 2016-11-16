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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
public class CommentsController {

    private CommentService commentSrv;
    private TaskService taskSrv;
    private WorkLogService wlSrv;
    private MessageSource msg;

    @Autowired
    public CommentsController(CommentService commSrv, TaskService taskSrv,
                              WorkLogService wlSrv, MessageSource msg) {
        this.commentSrv = commSrv;
        this.taskSrv = taskSrv;
        this.wlSrv = wlSrv;
        this.msg = msg;
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/comments", method = RequestMethod.POST)
    public ResponseEntity<?> getComments(@PathVariable(value = "id") String id) {
        Task task = taskSrv.findById(id);
        if (task == null) {
            return ResponseEntity.badRequest().body(msg.getMessage("task.notexists", null, Utils.getCurrentLocale()));
        }
        return ResponseEntity.ok(commentSrv.findByTaskIdOrderByDateDesc(id));
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
        if (!commentSrv.isCommentAllowed(task)) {
            MessageHelper.addWarningAttribute(ra, msg.getMessage(
                    "comment.notallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
        } else {
            if (commentSrv.commentMessageValid(message, ra)) {
                task.addComment(commentSrv.addComment(message, currAccount, task));
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
        if (!commentSrv.isCommentAllowed(task)) {
            MessageHelper.addWarningAttribute(ra, msg.getMessage(
                    "comment.editNotallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
        } else {
            Comment comment = commentSrv.findById(id);
            if (comment == null
                    || !comment.getAuthor().equals(Utils.getCurrentAccount())) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("main.generalError", null, locale));
            } else {
                comment.setMessage(null);
                comment.setDate_edited(null);
                commentSrv.save(comment);
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
        if (!commentSrv.isCommentAllowed(task)) {
            MessageHelper.addWarningAttribute(ra, msg.getMessage(
                    "comment.editNotallowed", new Object[]{((TaskState) task
                            .getState()).getDescription()}, locale));
        } else {
            if (commentSrv.editComment(id, message, ra)) {
                MessageHelper.addSuccessAttribute(ra,
                        msg.getMessage("comment.edited", null, locale));
            }
        }
        return "redirect:" + request.getHeader("Referer");
    }


}
