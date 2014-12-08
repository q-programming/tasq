package com.qprogramming.tasq.account;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.AccountSorter;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
@Secured("ROLE_USER")
public class AccountController {
	private static final int DEFAULT_WIDTH_HEIGHT = 150;
	private static final String SORT_BY_NAME = "name";
	private static final Object SORT_BY_EMAIL = "email";
	private static final String SORT_BY_SURNAME = "surname";

	@Autowired
	private AccountService accountSrv;

	@Autowired
	private ProjectService projSrv;

	@Autowired
	private SessionLocaleResolver localeResolver;

	@Autowired
	private MessageSource msg;

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountController.class);

	@RequestMapping(value = "account/current", method = RequestMethod.GET)
	public String getAccount() {
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
			@RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
			@RequestParam(value = "emails", required = false) String emails,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "theme", required = false) String theme,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response) {
		Account account = Utils.getCurrentAccount();
		if (avatarFile.getSize() != 0) {
			try {
				BufferedImage image = ImageIO.read(avatarFile.getInputStream());
				Integer width = image.getWidth();
				Integer height = image.getHeight();
				if (width > DEFAULT_WIDTH_HEIGHT
						|| height > DEFAULT_WIDTH_HEIGHT
						|| avatarFile.getSize() > 100000) {
					MessageHelper.addErrorAttribute(
							ra,
							msg.getMessage("error.file100kb", null,
									Utils.getCurrentLocale()));
					return "redirect:/settings";
				}
				byte[] bytes = avatarFile.getBytes();
				account.setAvatar(bytes);
			} catch (IOException e) {
				LOG.error(e.getLocalizedMessage());
			}
		}
		account.setLanguage(language);
		localeResolver.setLocale(request, response, new Locale(language));
		account.setEmail_notifications(Boolean.parseBoolean(emails));
		account.setTheme(theme);
		accountSrv.update(account);
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("panel.saved", null, Utils.getCurrentLocale()));
		return "redirect:/settings";
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String listUsers(
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "sort", required = false) String sortBy,
			@RequestParam(value = "desc", required = false) String desc,
			Model model) {
		List<Account> accountsList;

		if (name != null && name != "") {
			accountsList = accountSrv.findByNameStartingWith(name);
			accountsList.addAll(accountSrv.findBySurnameStartingWith(name));
		} else {
			accountsList = accountSrv.findAll();
		}

		// Accounts sorting
		boolean descending = Boolean.parseBoolean(desc);
		sortBy = sortBy != null ? sortBy : "";
		if (SORT_BY_NAME.equals(sortBy)) {
			Collections.sort(accountsList, new AccountSorter(
					AccountSorter.SORTBY.NAME, descending));
		} else if (SORT_BY_EMAIL.equals(sortBy)) {
			Collections.sort(accountsList, new AccountSorter(
					AccountSorter.SORTBY.EMAIL, descending));
		} else {
			Collections.sort(accountsList, new AccountSorter(
					AccountSorter.SORTBY.SURNAME, descending));
			sortBy = SORT_BY_SURNAME;
		}

		model.addAttribute("sort", sortBy);
		model.addAttribute("desc", descending);
		model.addAttribute("name", name);
		model.addAttribute("accountsList", accountsList);
		return "user/list";
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String getUser(@RequestParam(value = "id") Long id, Model model,
			RedirectAttributes ra) {
		Account account = accountSrv.findById(id);
		if (account == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("error.noUser", null,
							Utils.getCurrentLocale()));
			return "redirect:/users";
		}
		model.addAttribute("projects", projSrv.findAllByUser(account.getId()));
		model.addAttribute("account", account);
		return "user/details";
	}

	@RequestMapping("/userAvatar")
	public void getCurrentAvatar(HttpServletResponse response,
			HttpServletRequest request) throws IOException {

		response.setContentType("image/png");
		byte[] imageBytes = Utils.getCurrentAccount().getAvatar();

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
	public void getUserAvatar(HttpServletResponse response,
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

	@RequestMapping(value = "/getAccounts", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplayAccount> listAccounts(@RequestParam String term,
			HttpServletResponse response) {
		response.setContentType("application/json");
		List<Account> all_accountr = accountSrv.findAll();
		List<DisplayAccount> result = new ArrayList<DisplayAccount>();
		for (Account account : all_accountr) {
			if (term == null) {
				DisplayAccount s_account = new DisplayAccount(account);
				result.add(s_account);
			} else {
				if (StringUtils.containsIgnoreCase(account.toString(), term)) {
					DisplayAccount s_account = new DisplayAccount(account);
					result.add(s_account);
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "role", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String setRole(@RequestParam(value = "id") Long id,
			@RequestParam(value = "role") Roles role) {
		Account account = accountSrv.findById(id);
		if (account != null) {
			// check if not admin or user
			List<Account> admins = accountSrv.findAdmins();
			if (account.getRole().equals(Roles.ROLE_ADMIN)
					&& admins.size() == 1) {
				return msg.getMessage("role.last.admin", null,
						Utils.getCurrentLocale());
			} else {
				account.setRole(role);
				accountSrv.update(account);
				return "OK";
			}
		}
		return null;
	}
}
