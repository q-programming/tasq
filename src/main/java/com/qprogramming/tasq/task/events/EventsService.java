package com.qprogramming.tasq.task.events;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;

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

	/**
	 * Returns list of all events for currently logged account
	 * 
	 * @return
	 */
	public List<Event> getEvents() {
		List<Event> events = eventsRepo.findByAccountId(Utils
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
	public void addWatchEvent(String taskID, LogType type, String string,
			Date when) {
		WatchedTask task = watchSrv.getByTask(taskID);
		for (Account account : task.getWatchers()) {
			if (account != Utils.getCurrentAccount()) {
				Event event = new Event();
				event.setTask(taskID);
				event.setAccount(account);
				event.setUnread(true);
				event.setLogtype(type);
				event.setDate(when);
				event.setType(Event.Type.WATCH);
				event.setMessage(string);
				eventsRepo.save(event);
			}
		}
	}

}
