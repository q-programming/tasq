package com.qprogramming.tasq.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Throwables;

/**
 * General error handler for the application.
 */
@ControllerAdvice
class ExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler(value = TasqAuthException.class)
	public ModelAndView authException(Exception exception,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("error/authError");
		modelAndView.addObject("errorMessage",
				Throwables.getRootCause(exception));
		return modelAndView;
	}

	/**
	 * Handle exceptions thrown by handlers.
	 */
	@org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
	public ModelAndView exception(Exception exception, WebRequest request) {
		ModelAndView modelAndView = new ModelAndView("generalError");
		modelAndView.addObject("errorMessage",
				Throwables.getRootCause(exception));
		return modelAndView;
	}
}