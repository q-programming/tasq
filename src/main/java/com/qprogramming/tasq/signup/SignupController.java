package com.qprogramming.tasq.signup;

import java.io.File;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqException;
import com.qprogramming.tasq.manage.AppService;
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
	private MessageSource msg;
	private ThemeService themeSrv;
	private AppService appSrv;

	@Autowired
	public SignupController(AccountService accountSrv, MessageSource msg, ThemeService themeSrv, AppService appSrv) {
		this.accountSrv = accountSrv;
		this.msg = msg;
		this.themeSrv = themeSrv;
		this.appSrv = appSrv;
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
			// set base url
			Utils.setHttpRequest(request);
			String url = Utils.getBaseURL();
			appSrv.setProperty(AppService.URL, url);
		}
		account.setTheme(themeSrv.getDefault());
		account = accountSrv.save(account, true);
		// copy default avatar
		File userAvatar = new File(getAvatar(account.getId()));
		Utils.copyFile(sc, "/resources/img/avatar.png", userAvatar);
		if (!accountSrv.sendConfirmationLink(account)) {
			throw new TasqException(msg.getMessage("error.email.sending", null, Utils.getCurrentLocale()));
		}
		MessageHelper.addSuccessAttribute(ra, msg.getMessage("signup.success", null, Utils.getDefaultLocale()));
		return "redirect:/";
	}

	@RequestMapping(value = "/confirm", method = RequestMethod.GET)
	public String confirm(@RequestParam(value = "id", required = true) String id, RedirectAttributes ra,
			HttpServletRequest request) throws ServletException {
		Account account = accountSrv.findByUuid(id);
		if (account != null) {
			account.setConfirmed(true);
			accountSrv.update(account);
			MessageHelper.addSuccessAttribute(ra, msg.getMessage("signup.confirmed", null, Utils.getDefaultLocale()));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (!(authentication instanceof AnonymousAuthenticationToken)) {
				request.logout();
			}
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

	@Transactional(rollbackFor = TasqException.class)
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
			if (!accountSrv.sendResetLink(account)) {
				throw new TasqException(msg.getMessage("error.email.sending", null, Utils.getCurrentLocale()));
			}
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
