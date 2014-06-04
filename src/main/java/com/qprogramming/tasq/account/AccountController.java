package com.qprogramming.tasq.account;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
@Secured("ROLE_USER")
public class AccountController {

	@Autowired
	AccountService accountSrv;

	@Autowired
	private SessionLocaleResolver localeResolver;

	@Autowired
	private MessageSource msg;

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountController.class);

	@RequestMapping(value = "account/current", method = RequestMethod.GET)
	public String accounts(HttpServletRequest request) {
		Account principal = (Account) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		LOG.info(principal.toString());
		return "redirect:/";
	}

	@RequestMapping(value = "settings", method = RequestMethod.GET)
	public String settings() {
		return "user/settings";
	}

	@Transactional
	@RequestMapping(value = "settings", method = RequestMethod.POST)
	public String saveSettings(
			@RequestParam(value = "emails", required = false) String emails,
			@RequestParam(value = "language") String language,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response) {
		Account account = Utils.getCurrentAccount();
		account.setLanguage(language);
		localeResolver.setLocale(request, response, new Locale(language));
		account.setEmail_notifications(Boolean.parseBoolean(emails));
		accountSrv.update(account);
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("panel.saved", null, Utils.getCurrentLocale()));
		return "redirect:/settings";
	}

	@RequestMapping("/userAvatar")
	public void getImage(HttpServletResponse response,
			HttpServletRequest request) throws IOException {

		response.setContentType("image/png");
		Account auth = (Account) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		byte[] imageBytes = auth.getAvatar();

		if (imageBytes == null || imageBytes.length == 0) {
			HttpSession session = request.getSession();
			ServletContext sc = session.getServletContext();
			InputStream is = new FileInputStream(
					sc.getRealPath("/resources/img/avatar.png"));
			imageBytes = IOUtils.toByteArray(is);
			response.setContentType("image/png");
		}
		response.getOutputStream().write(imageBytes);
		response.getOutputStream().flush();
	}

	@RequestMapping("/userAvatar/{id}")
	public void getImage(HttpServletResponse response,
			HttpServletRequest request, @PathVariable("id") final String id)
			throws IOException {

		Account acc = accountSrv.findById(Long.parseLong(id));
		byte[] imageBytes = acc.getAvatar();
		response.setContentType("image/jpeg");

		if (imageBytes == null || imageBytes.length == 0) {
			HttpSession session = request.getSession();
			ServletContext sc = session.getServletContext();
			InputStream is = new FileInputStream(
					sc.getRealPath("/resources/img/avatar.png"));
			imageBytes = IOUtils.toByteArray(is);
			response.setContentType("image/png");
		}
		response.getOutputStream().write(imageBytes);
		response.getOutputStream().flush();
	}

}
