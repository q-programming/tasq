package com.qprogramming.tasq.home;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RequestedLinkAdvice {

	/**
	 * Add requestedLink to model for purpose of marking active page
	 * 
	 * @param request
	 * @return
	 */
	@ModelAttribute("requestedLink")
	public String link(HttpServletRequest request) {
		return request.getRequestURI();
	}

}
