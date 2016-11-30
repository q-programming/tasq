package com.qprogramming.tasq.events;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.events.Event.Type;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.*;

@Service
public class EventsService {

    private static final String APPLICATION_NAME = "applicationName";
    public static final String TASK = "task";
    private static final String WL_MESSAGE = "wlMessage";
    private static final String LOG_KEY = "log";
    private static final String CUR_ACCOUNT = "curAccount";
    private static final String EVENT_STR = "eventStr";
    private static final String APPLICATION = "application";
    private static final String ACCOUNT = "account";
    private static final Logger LOG = LoggerFactory.getLogger(EventsService.class);
    private static final String UTF_8 = "UTF-8";
    private static final String EMAIL_TEMP_PATH = "email/";
    private EventsRepository eventsRepo;
    private WatchedTaskService watchSrv;
    private MailMail mailer;
    private MessageSource msg;
    private VelocityEngine velocityEngine;
    private ResourceService resourceSrv;
    private AppService appSrv;
    private String applicationName;

    @Autowired
    public EventsService(EventsRepository eventsRepo, WatchedTaskService watchSrv, MailMail mailer, MessageSource msg,
                         VelocityEngine velocityEngine, ResourceService resourceSrv, AppService appSrv) {
        this.watchSrv = watchSrv;
        this.eventsRepo = eventsRepo;
        this.mailer = mailer;
        this.msg = msg;
        this.velocityEngine = velocityEngine;
        this.resourceSrv = resourceSrv;
        this.appSrv = appSrv;
        applicationName = appSrv.getProperty(AppService.APPLICATION_NAME);
    }

    public Event getById(Long id) {
        return eventsRepo.findById(id);
    }

    /**
     * Returns list of all events for currently logged account
     *
     * @return
     */
    public List<Event> getEvents() {
        List<Event> events = eventsRepo.findByAccountIdOrderByDateDesc(Utils.getCurrentAccount().getId());
        return events != null ? events : new LinkedList<>();
    }

    /**
     * Returns list of all events for task
     *
     * @return
     */
    public List<Event> getTaskEvents(String task) {
        List<Event> events = eventsRepo.findByTask(task);
        return events != null ? events : new LinkedList<>();
    }

    /**
     * Returns list of all events for currently logged account with Pageable
     *
     * @param page pageable object
     * @return page with events
     */
    public Page<Event> getEvents(Pageable page) {
        return eventsRepo.findByAccountId(Utils.getCurrentAccount().getId(), page);
    }

    /**
     * Returns list of all unread events for currently logged account
     *
     * @return list of all unread events
     */
    public List<Event> getUnread() {
        List<Event> unread = eventsRepo.findByAccountIdAndUnreadTrue(Utils.getCurrentAccount().getId());
        return unread != null ? unread : new LinkedList<>();
    }

    /**
     * Add event for each account that is watching task.
     * Based on type, send e-mail if user selected to do so.
     *
     * @param log       - worklog
     * @param wlMessage additional worklog message ( can be empty )
     * @param when      - when event happened,
     */
    public void addWatchEvent(WorkLog log, String wlMessage, Date when) {
        String taskID = log.getTask().getId();
        WatchedTask task = watchSrv.getByTask(taskID);
        if (task != null) {
            for (Account account : task.getWatchers()) {
                if (!account.equals(Utils.getCurrentAccount())) {
                    Event event = new Event();
                    event.setTask(taskID);
                    event.setAccount(account);
                    event.setWho(log.getAccount().toString());
                    event.setUnread(true);
                    event.setLogtype((LogType) log.getType());
                    event.setDate(when);
                    event.setType(getEventType((LogType) log.getType()));
                    event.setMessage(wlMessage);
                    eventsRepo.save(event);
                    if (sendEmail(account, event.getType())) {
                        String baseUrl = appSrv.getProperty(AppService.URL);
                        Locale locale = new Locale(account.getLanguage());
                        String eventStr = msg.getMessage(((LogType) log.getType()).getCode(), null, locale);
                        StringBuilder subject = new StringBuilder("[");
                        subject.append(taskID);
                        subject.append("] ");
                        subject.append(task.getName());
                        String type = task.getType().getCode();
                        StringWriter stringWriter = new StringWriter();
                        VelocityContext context = new VelocityContext();
                        context.put(ACCOUNT, account);
                        context.put(APPLICATION, baseUrl);
                        context.put(APPLICATION_NAME, applicationName);
                        context.put(TASK, task);
                        context.put(WL_MESSAGE, wlMessage);
                        context.put(LOG_KEY, log);
                        context.put(CUR_ACCOUNT, Utils.getCurrentAccount());
                        context.put(EVENT_STR, eventStr);
                        velocityEngine.mergeTemplate(EMAIL_TEMP_PATH + account.getLanguage() + "/task.vm", UTF_8, context, stringWriter);
                        String message = stringWriter.toString();
                        LOG.info(account.getEmail());
                        LOG.info(subject.toString());
                        LOG.info(message);
                        Map<String, Resource> resources = resourceSrv.getBasicResourceMap();
                        resources.put(type, resourceSrv.getTaskTypeIcon(type));
                        resources.put("avatar", resourceSrv.getUserAvatar());
                        mailer.sendMail(MailMail.NOTIFICATION, account.getEmail(), subject.toString(), message,
                                resources);
                        resourceSrv.clean();
                    }
                }
            }
        }
    }

    /**
     * Add event related to project
     *
     * @param account user for which event will be added
     * @param type    type of event
     * @param project project where event happened
     */
    public void addProjectEvent(Account account, LogType type, Project project) {
        Event event = new Event();
        event.setAccount(account);
        event.setWho(Utils.getCurrentAccount().toString());
        event.setUnread(true);
        event.setLogtype(type);
        event.setDate(new Date());
        event.setType(getEventType(type));
        event.setMessage(project.toString());
        eventsRepo.save(event);
        if (sendEmail(account, event.getType())) {
            String baseUrl = appSrv.getProperty(AppService.URL);
            Locale locale = new Locale(account.getLanguage());
            String eventStr = msg.getMessage(type.getCode(), null, locale);
            String subject = msg.getMessage("event.newSystemEvent",
                    new Object[]{Utils.getCurrentAccount(), eventStr}, locale);

            StringWriter stringWriter = new StringWriter();
            VelocityContext context = new VelocityContext();
            context.put(ACCOUNT, account);
            context.put("type", type);
            context.put(APPLICATION, baseUrl);
            context.put(APPLICATION_NAME, applicationName);
            context.put("project", project);
            context.put(CUR_ACCOUNT, Utils.getCurrentAccount());
            velocityEngine.mergeTemplate(EMAIL_TEMP_PATH + account.getLanguage() + "/project.vm", UTF_8, context, stringWriter);
            String message = stringWriter.toString();
            mailer.sendMail(MailMail.NOTIFICATION, account.getEmail(), subject, message,
                    resourceSrv.getBasicResourceMap());
        }

    }

    /**
     * Checks if user have e-mail notification enabled for passed type
     *
     * @param account - account checked for email settings
     * @param type    - type of event
     * @return true if email should be send
     */
    private boolean sendEmail(Account account, Type type) {
        switch (type) {
            case COMMENT:
                return account.getCommentnotification();
            case WATCH:
                return account.getWatchnotification();
            case SYSTEM:
                return account.getSystemnotification();
            default:
                return false;
        }
    }

    public void save(Event event) {
        eventsRepo.save(event);
    }

    public void save(List<Event> events) {
        eventsRepo.save(events);

    }

    public void delete(List<Event> events) {
        eventsRepo.delete(events);
    }

    public void delete(Event event) {
        eventsRepo.delete(event);
    }

    private Type getEventType(LogType type) {
        switch (type) {
            case ASSIGN_PROJ:
                return Type.SYSTEM;
            case REMOVE_PROJ:
                return Type.SYSTEM;
            case COMMENT:
                return Type.COMMENT;
            default:
                return Type.WATCH;
        }
    }

}
