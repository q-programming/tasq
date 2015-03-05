package com.qprogramming.tasq.task.events;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.events.Event.Type;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;

@Service
public class EventsService {

	private EventsRepository eventsRepo;
	private WatchedTaskService watchSrv;
	private MailMail mailer;
	private MessageSource msg;
	private static final Logger LOG = LoggerFactory
			.getLogger(EventsService.class);

	@Autowired
	public EventsService(EventsRepository eventsRepo,
			WatchedTaskService watchSrv, MailMail mailer, MessageSource msg) {
		this.watchSrv = watchSrv;
		this.eventsRepo = eventsRepo;
		this.mailer = mailer;
		this.msg = msg;
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
		List<Event> events = eventsRepo.findByAccountIdOrderByDateDesc(Utils
				.getCurrentAccount().getId());
		return events != null ? events : new LinkedList<Event>();
	}

	/**
	 * Returns list of all events for currently logged account with Pageable
	 * 
	 * @param page
	 * @return
	 */
	public Page<Event> getEvents(Pageable page) {
		return eventsRepo.findByAccountId(Utils
				.getCurrentAccount().getId(), page);
	}

	/**
	 * Returns list of all unread events for currently logged account
	 * 
	 * @return
	 */
	public List<Event> getUnread() {
		List<Event> unread = eventsRepo.findByAccountIdAndUnreadTrue(Utils
				.getCurrentAccount().getId());
		return unread != null ? unread : new LinkedList<Event>();
	}

	/**
	 * Add event for each account that is watching task
	 * 
	 * @param taskID
	 * @param type
	 * @param string
	 * @param when
	 */
	public void addWatchEvent(WorkLog log, String string, Date when) {
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
					event.setMessage(string);
					eventsRepo.save(event);
					if (account.getEmail_notifications()) {
						Locale locale = new Locale(account.getLanguage());
						String eventStr = msg.getMessage(
								((LogType) log.getType()).getCode(), null,
								locale);
						String subject = msg.getMessage(
								"event.newEvent",
								new Object[] { log.getTask().getId(),
										Utils.getCurrentAccount(), eventStr },
								locale);
						String message = msg
								.getMessage("event.newEvent.body",
										new Object[] { account.toString(),
												Utils.getCurrentAccount(),
												eventStr, string,
												log.getTask().getId() }, locale);
						LOG.info(account.getEmail());
						LOG.info(subject);
						LOG.info(message);
						// if(mailer.sendMail(mailer.NOTIFICATION, email,
						// subject,
						// message)){
					}
				}
			}
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
		case COMMENT:
			return Type.COMMENT;
		default:
			return Type.WATCH;
		}

	}
}
