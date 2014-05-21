package com.qprogramming.tasq.account;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Secured("ROLE_USER")
public class AccountController {

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountController.class);

	@Autowired
	private AccountRepository userRepository;

	@RequestMapping(value = "account/current", method = RequestMethod.GET)
	public String accounts(HttpServletRequest request) {
		Account principal = (Account) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		LOG.info(principal.toString());
		return "redirect:/" ;
	}
}
