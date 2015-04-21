package com.qprogramming.tasq.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qprogramming.tasq.support.ResultData;

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
	 * Get list of all tasks watched by currently logged user
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/listEvents", method = RequestMethod.GET)
	public @ResponseBody Page<DisplayEvent> eventsPaged(
			@RequestParam(required = false) String term,
			@PageableDefault(size = 25, page = 0, sort = "date", direction = Direction.DESC) Pageable p) {
		Page<Event> events = eventSrv.getEvents(p);
		List<DisplayEvent> eventList = new ArrayList<DisplayEvent>();
		for (Event event : events) {
			eventList.add(new DisplayEvent(event));
		}
		Page<DisplayEvent> result = new PageImpl<DisplayEvent>(eventList, p,
				events.getTotalElements());
		return result;
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
