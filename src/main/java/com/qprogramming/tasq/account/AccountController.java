package com.qprogramming.tasq.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
@Secured("ROLE_USER")
public class AccountController {

	@Value("${home.directory}")
	private String tasqRootDir;

	private static final int DEFAULT_WIDTH_HEIGHT = 150;
	private AccountService accountSrv;
	private ProjectService projSrv;
	private SessionLocaleResolver localeResolver;
	private MessageSource msg;

	@Autowired
	public AccountController(AccountService accountSrv, ProjectService projSrv,
			MessageSource msg, SessionLocaleResolver localeResolver) {
		this.accountSrv = accountSrv;
		this.projSrv = projSrv;
		this.msg = msg;
		this.localeResolver = localeResolver;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(AccountController.class);

	private static final String AVATAR_DIR = "avatar";
	private static final String PNG = ".png";

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
			File file = new File(getAvatar(account.getId()));
			try {
				FileUtils.writeByteArrayToFile(file, avatarFile.getBytes());
			} catch (IOException e) {
				LOG.error(e.getMessage());
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

	@RequestMapping(value = "/getAccounts", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplayAccount> listAccounts(@RequestParam String term,
			HttpServletResponse response) {
		response.setContentType("application/json");
		List<Account> accounts = new LinkedList<Account>();
		if (term == null) {
			accounts = accountSrv.findAll();
		} else {
			accounts = accountSrv.findByNameSurnameContaining(term);
		}
		List<DisplayAccount> result = new ArrayList<DisplayAccount>();
		for (Account account : accounts) {
			DisplayAccount d_account = new DisplayAccount(account);
			result.add(d_account);
		}
		return result;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public @ResponseBody
	Page<DisplayAccount> listUsers(
			@RequestParam(required = false) String term,
			@PageableDefault(size = 25, page = 0, sort = "surname", direction = Direction.ASC) Pageable p) {
		Page<Account> page;
		if (term != null) {
			page = accountSrv.findByNameSurnameContaining(term, p);
		} else {
			page = accountSrv.findAll(p);
		}
		List<DisplayAccount> list = new LinkedList<DisplayAccount>();
		for (Account account : page) {
			list.add(new DisplayAccount(account));
		}
		Page<DisplayAccount> result = new PageImpl<DisplayAccount>(list, p,
				page.getTotalElements());
		return result;
	}

	@RequestMapping(value = "role", method = RequestMethod.POST)
	@ResponseBody
	public ResultData setRole(@RequestParam(value = "id") Long id,
			@RequestParam(value = "role") Roles role) {
		Account account = accountSrv.findById(id);
		if (account != null) {
			// check if not admin or user
			List<Account> admins = accountSrv.findAdmins();
			if (account.getRole().equals(Roles.ROLE_ADMIN)
					&& admins.size() == 1) {
				return new ResultData(ResultData.ERROR, msg.getMessage(
						"role.last.admin", null, Utils.getCurrentLocale()));
			} else {
				String rolemsg = msg.getMessage(role.getCode(), null,
						Utils.getCurrentLocale());
				account.setRole(role);
				accountSrv.update(account);
				String resultMsg = msg.getMessage("role.change.succes",
						new Object[] { account.toString(), rolemsg },
						Utils.getCurrentLocale());
				return new ResultData(ResultData.OK, resultMsg);
			}
		}
		return null;
	}

	private String getAvatarDir() {
		return tasqRootDir + File.separator + AVATAR_DIR + File.separator;
	}

	private String getAvatar(Long id) {
		return getAvatarDir() + id + PNG;
	}
}
