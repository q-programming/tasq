package com.qprogramming.tasq.task.events;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class EventsController {

	/**
	 * Get list of all tasks watched by currently logged user
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public String events(Model model) {
		return "user/events";
	}
}
