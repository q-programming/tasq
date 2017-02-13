package com.qprogramming.tasq.task.comments;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Created by jromaniszyn on 16.11.2016.
 */
@Service
public class CommentService {

    private CommentsRepository commRepo;
    private MessageSource msg;
    private WorkLogService wlSrv;

    @Autowired
    public CommentService(CommentsRepository commRepo,MessageSource msg,WorkLogService wlSrv) {
        this.commRepo = commRepo;
        this.msg = msg;
        this.wlSrv = wlSrv;
    }


    public Comment save(Comment comment) {
        return commRepo.save(comment);
    }

    public Comment findById(Long id) {
        return commRepo.findById(id);
    }

    public Comment addComment(String message, Account currAccount, Task task) {
        Hibernate.initialize(task.getComments());
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(currAccount);
        comment.setDate(new Date());
        comment.setMessage(message);
        wlSrv.addActivityLog(task, message, LogType.COMMENT);
        return save(comment);
    }

    public boolean editComment(Long id, String message, RedirectAttributes ra) {
        if (commentMessageValid(message, ra)) {
            Comment comment = findById(id);
            if (comment != null) {
                comment.setMessage(message);
                comment.setDate_edited(new Date());
                comment = save(comment);
            }
            return comment != null;
        }
        return false;
    }

    /**
     * Returns true if message is not empty and doesn't contain HTML tags
     *
     * @param message
     * @param ra
     * @return
     */
    public boolean commentMessageValid(String message, RedirectAttributes ra) {
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

    public Set<Comment> findByTaskIdOrderByDateDesc(String id) {
        return commRepo.findByTaskIdOrderByDateDesc(id);
    }

    public void delete(Set<Comment> comments) {
        commRepo.delete(comments);
    }
}
