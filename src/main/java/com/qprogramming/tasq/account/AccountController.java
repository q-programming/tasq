package com.qprogramming.tasq.account;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
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
			@RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
			@RequestParam(value = "emails", required = false) String emails,
			@RequestParam(value = "language", required = false) String language,
			RedirectAttributes ra, HttpServletRequest request,
			HttpServletResponse response) {
		Account account = Utils.getCurrentAccount();
		if (avatarFile.getSize() != 0) {
			try {
				BufferedImage image = ImageIO.read(avatarFile.getInputStream());
				Integer width = image.getWidth();
				Integer height = image.getHeight();
				if (width > 150 || height > 150
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	/**
	 * Internal class to display it in form of autocomplete
	 * 
	 * @author romanjak
	 * 
	 */
	static class DisplayAccount {
		private String name;
		private String surname;
		private String email;
		private String username;
		private Long id;

		public DisplayAccount(Account account) {
			setId(account.getId());
			setName(account.getName());
			setSurname(account.getSurname());
			setEmail(account.getEmail());
			setUsername(account.getUsername());

		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSurname() {
			return surname;
		}

		public void setSurname(String surname) {
			this.surname = surname;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}
}
