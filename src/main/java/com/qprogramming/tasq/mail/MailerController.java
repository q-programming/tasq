package com.qprogramming.tasq.mail;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class MailerController {

	private MailMail mailer;
	private MessageSource msg;

	private static final Logger LOG = LoggerFactory
			.getLogger(MailerController.class);

	@Autowired
	public MailerController(MessageSource msg, MailMail mailer) {
		this.msg = msg;
		this.mailer = mailer;
	}

	@RequestMapping(value = "/inviteUsers", method = RequestMethod.GET)
	public String getUser(@RequestParam(value = "email") String email,
			HttpServletRequest request,RedirectAttributes ra) {
		Account sender = Utils.getCurrentAccount();
		String subject = msg.getMessage("panel.invite.subject", null,
				Utils.getCurrentLocale());
		String link = Utils.getBaseURL() + "/signup";
		String message = msg.getMessage("panel.invite.body", new Object[] {
				sender.toString(), link }, mailer.getDefaultLang());
		LOG.info(email);
		LOG.info(subject);
		LOG.info(message);
		//if(mailer.sendMail(mailer.NOTIFICATION, email, subject, message)){
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("panel.invite.sent", new Object[] {email},
						Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}
}
