package com.qprogramming.tasq.task.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
