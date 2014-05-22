package com.qprogramming.tasq.signup;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class SignupController {
	private static final Logger LOG = LoggerFactory
			.getLogger(SignupController.class);

	@Autowired
	private AccountService accountSrv;

	@Autowired
	private UserService userService;

	@Autowired
	private MailMail mailer;

	@Autowired
	private MessageSource msg;

	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
		if (errors.hasErrors()) {
			return null;
		}
		if (!signupForm.isPasswordConfirmed()) {
			errors.reject("error.notMatchedPasswords");
			return null;
		}
		Utils.setHttpRequest(request);
		if (null != accountSrv.findByEmail(signupForm.getEmail())) {
			errors.reject("error.email.notunique");
			return null;
		}
		Account account = accountSrv.save(signupForm.createAccount());
		String confirmlink = Utils.getBaseURL() + "/confirm?id="
				+ account.getUuid();

		String subject = msg.getMessage("signup.register", null,
				Utils.getDefaultLocale());
		String message = msg.getMessage(
				"signup.register.message",
				new Object[] { account.getName(), confirmlink,
						Utils.getBaseURL() }, Utils.getDefaultLocale());
		LOG.debug(confirmlink);
		mailer.sendMail(MailMail.REGISTER, account.getEmail(), subject, message);
		MessageHelper.addSuccessAttribute(ra, msg.getMessage("signup.success",
				null, Utils.getDefaultLocale()));

		return "redirect:/";
	}

	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm(
			@RequestParam(value = "id", required = true) String id,
			RedirectAttributes ra) {
		Account account = accountSrv.findByUuid(id);
		if (account != null) {
			account.setConfirmed(true);
			accountSrv.update(account);
			MessageHelper.addSuccessAttribute(
					ra,
					msg.getMessage("signup.confirmed", null,
							Utils.getDefaultLocale()));
		} else {
			MessageHelper.addErrorAttribute(ra, "Verification error!");
		}
		return "redirect:/";
	}
}
