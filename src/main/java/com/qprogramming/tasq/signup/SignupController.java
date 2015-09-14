package com.qprogramming.tasq.signup;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.ThemeService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class SignupController {
	private static final Logger LOG = LoggerFactory.getLogger(SignupController.class);

	@Value("${home.directory}")
	private String tasqRootDir;
	private static final String AVATAR_DIR = "avatar";
	private static final String PNG = ".png";
	private static final String LOGO = "logo";

	private AccountService accountSrv;
	private MailMail mailer;
	private MessageSource msg;
	private VelocityEngine velocityEngine;
	private ResourceService resourceSrv;
	private ThemeService themeSrv;

	@Autowired
	public SignupController(AccountService accountSrv, MessageSource msg, MailMail mailer,
			VelocityEngine velocityEngine, ResourceService resourceSrv, ThemeService themeSrv) {
		this.accountSrv = accountSrv;
		this.msg = msg;
		this.mailer = mailer;
		this.velocityEngine = velocityEngine;
		this.resourceSrv = resourceSrv;
		this.themeSrv = themeSrv;
	}

	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra,
			HttpServletRequest request) {
		if (errors.hasErrors()) {
			return null;
		}
		if (!signupForm.isPasswordConfirmed()) {
			errors.rejectValue("password", "error.notMatchedPasswords");
			return null;
		}
		Utils.setHttpRequest(request);
		if (null != accountSrv.findByEmail(signupForm.getEmail())) {
			errors.rejectValue("email", "error.email.notunique");
			return null;
		}

		if (null != accountSrv.findByUsername((signupForm.getUsername()))) {
			errors.rejectValue("username", "error.username.notunique");
			return null;
		}
		HttpSession session = request.getSession();
		ServletContext sc = session.getServletContext();

		Account account = signupForm.createAccount();
		if (accountSrv.findAll().isEmpty()) {
			// FIRST ACCOUNT EVER, LAUNCH SETUP TASKS
			account.setRole(Roles.ROLE_ADMIN);
			// Copy logo
			File appLogo = new File(getAvatarDir() + LOGO + PNG);
			Utils.copyFile(sc, "/resources/img/logo.png", appLogo);
		}
		account.setTheme(themeSrv.getDefault());
		account = accountSrv.save(account, true);
		// copy default avatar
		File userAvatar = new File(getAvatar(account.getId()));
		Utils.copyFile(sc, "/resources/img/avatar.png", userAvatar);

		String confirmlink = Utils.getBaseURL() + "/confirm?id=" + account.getUuid();
		String subject = msg.getMessage("signup.register", null, Utils.getDefaultLocale());
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("link", confirmlink);
		model.put("application", Utils.getBaseURL());
		String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				"email/" + Utils.getDefaultLocale() + "/register.vm", "UTF-8", model);
		mailer.sendMail(MailMail.REGISTER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
		MessageHelper.addSuccessAttribute(ra, msg.getMessage("signup.success", null, Utils.getDefaultLocale()));

		return "redirect:/";
	}

	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm(@RequestParam(value = "id", required = true) String id, RedirectAttributes ra) {
		Account account = accountSrv.findByUuid(id);
		if (account != null) {
			account.setConfirmed(true);
			accountSrv.update(account);
			MessageHelper.addSuccessAttribute(ra, msg.getMessage("signup.confirmed", null, Utils.getDefaultLocale()));
		} else {
			MessageHelper.addErrorAttribute(ra, "Verification error!");
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/password", method = RequestMethod.GET)
	public PasswordResetForm reset(@RequestParam(value = "id", required = true) String id, RedirectAttributes ra) {
		PasswordResetForm form = new PasswordResetForm();
		form.setId(id);
		return form;
	}

	@Transactional
	@RequestMapping(value = "/password", method = RequestMethod.POST)
	public String resetSubmit(PasswordResetForm form, Errors errors, RedirectAttributes ra) {
		if (!form.isPasswordConfirmed()) {
			errors.rejectValue("password", "error.notMatchedPasswords");
			return null;
		}
		Account account = accountSrv.findByUuid(form.getId());
		if (account != null) {
			UUID uuid = UUID.fromString(form.getId());
			DateTime date = new DateTime(Utils.getTimeFromUUID(uuid));
			DateTime expireDate = date.plusHours(12);
			if (date.isAfter(expireDate)) {
				MessageHelper.addErrorAttribute(ra,
						msg.getMessage("signin.password.token.expired", null, Utils.getDefaultLocale()));
			} else {
				account.setPassword(form.getPassword());
				accountSrv.save(account, true);
				MessageHelper.addSuccessAttribute(ra,
						msg.getMessage("signin.password.success", null, Utils.getDefaultLocale()));
			}
		} else {
			MessageHelper.addErrorAttribute(ra,
					msg.getMessage("signin.password.token.invalid", null, Utils.getDefaultLocale()));
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public String resetPassword() {
		return "signin/resetPassword";
	}

	@Transactional
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam(value = "email", required = true) String email, RedirectAttributes ra,
			HttpServletRequest request) {
		Account account = accountSrv.findByEmail(email);
		if (account == null) {
			MessageHelper.addWarningAttribute(ra,
					msg.getMessage("signin.password.notfound", new Object[] { email }, Utils.getDefaultLocale()));
			return "redirect:" + request.getHeader("Referer");
		} else {
			accountSrv.save(account, false);
			Utils.setHttpRequest(request);
			StringBuilder url = new StringBuilder(Utils.getBaseURL());
			url.append("/");
			url.append("password?id=");
			url.append(account.getUuid());
			String subject = msg.getMessage("singin.password.reset", null, new Locale(account.getLanguage()));
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", account);
			model.put("link", url);
			model.put("application", Utils.getBaseURL());
			String message = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
					"email/" + account.getLanguage() + "/password.vm", "UTF-8", model);
			LOG.info(url.toString());
			mailer.sendMail(MailMail.OTHER, account.getEmail(), subject, message, resourceSrv.getBasicResourceMap());
			MessageHelper.addSuccessAttribute(ra,
					msg.getMessage("singin.password.token.sent", new Object[] { email }, Utils.getDefaultLocale()));
		}
		return "redirect:/";
	}

	private String getAvatarDir() {
		return tasqRootDir + File.separator + AVATAR_DIR + File.separator;
	}

	private String getAvatar(Long id) {
		return getAvatarDir() + id + PNG;
	}
}
