package com.qprogramming.tasq.task.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.task.watched.WatchedTask;

@Controller
public class EventsController {

	private EventsService eventSrv;

	@Autowired
	public EventsController(EventsService eventSrv) {
		this.eventSrv = eventSrv;
	}

	/**
	 * Get list of all tasks watched by currently logged user
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public String events(Model model) {
		List<Event> events = eventSrv.getEvents();
		model.addAttribute("events", events);
		return "user/events";
	}

	/**
	 * Sets event with id as read
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/events/read", method = RequestMethod.POST)
	@ResponseBody
	public ResultData readEvent(@RequestParam(value = "id") Long id) {
		ResultData result = new ResultData();
		Event event = eventSrv.getById(id);
		event.setUnread(false);
		eventSrv.save(event);
		result.code = ResultData.OK;
		return result;
	}

	@RequestMapping(value = "/events/readAll", method = RequestMethod.POST)
	@ResponseBody
	public ResultData readAllEvents() {
		ResultData result = new ResultData();
		List<Event> events = eventSrv.getEvents();
		for (Event event : events) {
			if (event.isUnread()) {
				event.setUnread(false);
				eventSrv.save(event);
			}
		}
		result.code = ResultData.OK;
		return result;
	}

	/**
	 * Returns int how many accounts is currently watching task with id
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/events/delete", method = RequestMethod.POST)
	@ResponseBody
	public ResultData deleteEvent(@RequestParam(value = "id") Long id) {
		ResultData result = new ResultData();
		Event event = eventSrv.getById(id);
		eventSrv.delete(event);
		result.code = ResultData.OK;
		return result;
	}

	@RequestMapping(value = "/events/deleteAll", method = RequestMethod.POST)
	@ResponseBody
	public ResultData deleteAllEvents() {
		ResultData result = new ResultData();
		List<Event> events = eventSrv.getEvents();
		eventSrv.delete(events);
		result.code = ResultData.OK;
		return result;
	}

}
