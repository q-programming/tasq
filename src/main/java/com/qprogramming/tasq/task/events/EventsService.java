package com.qprogramming.tasq.task.events;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.account.Account;
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

	@Autowired
	public EventsService(EventsRepository eventsRepo,
			WatchedTaskService watchSrv) {
		this.watchSrv = watchSrv;
		this.eventsRepo = eventsRepo;
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
		for (Account account : task.getWatchers()) {
			if (account != Utils.getCurrentAccount()) {
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
			}
		}
	}
	public void save(Event event){
		eventsRepo.save(event);
	}
	
	public void save(List<Event> events) {
		eventsRepo.save(events);
		
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
