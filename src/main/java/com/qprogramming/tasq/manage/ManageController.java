package com.qprogramming.tasq.manage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.io.Files;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class ManageController {

	private static final String AVATAR_DIR = "avatar";
	private static final String PNG = ".png";
	private static final String LOGO = "logo";
	private static final String SMALL = "small_";

	private static final Logger LOG = LoggerFactory.getLogger(ManageController.class);

	private ThemeService themeSrv;
	private MessageSource msg;
	private ProjectService projSrv;
	private AppService appSrv;
	private MailMail mailer;

	@Autowired
	public ManageController(ThemeService themeSrv, MessageSource msg, ProjectService projSrv, AppService appSrv,
			MailMail mailer) {
		this.themeSrv = themeSrv;
		this.msg = msg;
		this.projSrv = projSrv;
		this.appSrv = appSrv;
		this.mailer = mailer;
	}

	@RequestMapping(value = "manage/tasks", method = RequestMethod.GET)
	public String manageTasks(RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		model.addAttribute("projects", projSrv.findAll());
		return "admin/tasks";
	}

	@RequestMapping(value = "/manage/users", method = RequestMethod.GET)
	public String manageUsers(Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		return "admin/users";
	}

	@RequestMapping(value = "/manage/setdir", method = RequestMethod.POST)
	public String setDir(@RequestParam(value = "dir") String newPath, HttpServletRequest request,
			RedirectAttributes ra) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		String oldPath = appSrv.getProperty(AppService.TASQROOTDIR);
		if (!oldPath.equals(newPath)) {
			File newDir = new File(newPath);
			if (!newDir.mkdirs()) {
				MessageHelper.addErrorAttribute(ra, msg.getMessage("manage.prop.dir.error.create",
						new Object[] { newPath }, Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			} else {
				File oldDir = new File(oldPath);
				try {
					Files.move(oldDir, newDir);
				} catch (IOException e) {
					MessageHelper.addErrorAttribute(ra, msg.getMessage("manage.prop.dir.error.move",
							new Object[] { newPath }, Utils.getCurrentLocale()));
					LOG.error(e.getMessage());
					return "redirect:" + request.getHeader("Referer");
				}
				appSrv.setProperty(AppService.TASQROOTDIR, newPath);
			}
		}
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("manage.prop.dir.success", new Object[] { newPath }, Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/manage/seturl", method = RequestMethod.POST)
	public String setUrl(@RequestParam(value = "url") String url, HttpServletRequest request, RedirectAttributes ra) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		appSrv.setProperty(AppService.URL, url);
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("manage.prop.url.success", new Object[] { url }, Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/manage/setlang", method = RequestMethod.POST)
	public String setLang(@RequestParam(value = "language") String language, HttpServletRequest request,
			RedirectAttributes ra) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		appSrv.setProperty(AppService.DEFAULTLANG, language);
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("manage.prop.defaultLang.success", null, Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/manage/setemail", method = RequestMethod.POST)
	public String setEmail(@RequestParam(value = "emailHost") String emailHost,
			@RequestParam(value = "emailPort") String emailPort,
			@RequestParam(value = "emailUsername") String emailUsername,
			@RequestParam(value = "emailPass") String emailPass,
			@RequestParam(value = "emailSmtpAuth", required = false) boolean emailSmtpAuth,
			@RequestParam(value = "emailSmtpStarttls", required = false) boolean emailSmtpStarttls,
			@RequestParam(value = "emailDomain") String emailDomain,
			@RequestParam(value = "emailEncoding") String emailEncoding, HttpServletRequest request,
			RedirectAttributes ra) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		appSrv.setProperty(AppService.EMAIL_HOST, emailHost);
		appSrv.setProperty(AppService.EMAIL_PORT, emailPort);
		appSrv.setProperty(AppService.EMAIL_USERNAME, emailUsername);
		appSrv.setProperty(AppService.EMAIL_PASS, emailPass);
		appSrv.setProperty(AppService.EMAIL_SMTPAUTH, Boolean.toString(emailSmtpAuth));
		appSrv.setProperty(AppService.EMAIL_SMTPSTARTTLS, Boolean.toString(emailSmtpStarttls));
		appSrv.setProperty(AppService.EMAIL_DOMAIN, emailDomain);
		appSrv.setProperty(AppService.EMAIL_ENCODING, emailEncoding);
		mailer.initMailSender();
		MessageHelper.addSuccessAttribute(ra,
				msg.getMessage("manage.prop.email.success", null, Utils.getCurrentLocale()));
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "manage/app", method = RequestMethod.GET)
	public String manageApplication(RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		model.addAttribute("themes", themeSrv.findAll());
		model.addAttribute(AppService.URL, appSrv.getProperty(AppService.URL));
		model.addAttribute(AppService.EMAIL_HOST, appSrv.getProperty(AppService.EMAIL_HOST));
		model.addAttribute(AppService.EMAIL_PORT, appSrv.getProperty(AppService.EMAIL_PORT));
		model.addAttribute(AppService.EMAIL_USERNAME, appSrv.getProperty(AppService.EMAIL_USERNAME));
		model.addAttribute(AppService.EMAIL_PASS, appSrv.getProperty(AppService.EMAIL_PASS));
		model.addAttribute(AppService.EMAIL_SMTPAUTH, appSrv.getProperty(AppService.EMAIL_SMTPAUTH));
		model.addAttribute(AppService.EMAIL_SMTPSTARTTLS, appSrv.getProperty(AppService.EMAIL_SMTPSTARTTLS));
		model.addAttribute(AppService.EMAIL_ENCODING, appSrv.getProperty(AppService.EMAIL_ENCODING));
		model.addAttribute(AppService.EMAIL_DOMAIN, appSrv.getProperty(AppService.EMAIL_DOMAIN));
		model.addAttribute(AppService.TASQROOTDIR, appSrv.getProperty(AppService.TASQROOTDIR));
		model.addAttribute(AppService.DEFAULTLANG, appSrv.getProperty(AppService.DEFAULTLANG));
		return "admin/manage";
	}

	@RequestMapping(value = "manage/manageTheme", method = RequestMethod.POST)
	public String createTheme(@RequestParam(value = "themeID", required = false) Long themeID,
			@RequestParam(value = "name") String name, @RequestParam(value = "font") Font font,
			@RequestParam(value = "color") String color, @RequestParam(value = "invcolor") String invcolor,
			RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		Theme theme;
		if (themeID != null) {
			theme = themeSrv.findById(themeID);
			if (theme == null) {
				theme = new Theme();
			}
		} else {
			theme = new Theme();
		}
		theme.setName(name);
		theme.setFont(font);
		theme.setColor(color);
		theme.setInvColor(invcolor);
		themeSrv.save(theme);
		return "redirect:/manage/app";
	}

	@RequestMapping(value = "manage/logoUpload", method = RequestMethod.POST)
	public String logoUpload(@RequestParam(value = "avatar", required = false) MultipartFile logoFile,
			RedirectAttributes ra, HttpServletRequest request, HttpServletResponse response) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		if (logoFile.getSize() != 0) {
			File file = new File(getAvatarDir() + LOGO + PNG);
			try {
				FileUtils.writeByteArrayToFile(file, logoFile.getBytes());
				// Resize and save email logo
				resizeLogo();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
			MessageHelper.addSuccessAttribute(ra, msg.getMessage("manage.logo.saved", null, Utils.getCurrentLocale()));
		}
		return "redirect:/manage/app";
	}

	@RequestMapping(value = "manage/logoRestore", method = RequestMethod.GET)
	public String logoRestore(RedirectAttributes ra, HttpServletRequest request, HttpServletResponse response) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		HttpSession session = request.getSession();
		ServletContext sc = session.getServletContext();
		File file = new File(getAvatarDir() + LOGO + PNG);
		Utils.copyFile(sc, "/resources/img/logo.png", file);
		try {
			resizeLogo();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		MessageHelper.addSuccessAttribute(ra, msg.getMessage("manage.logo.restored", null, Utils.getCurrentLocale()));
		return "redirect:/manage/app";
	}

	private void resizeLogo() throws IOException {
		BufferedImage originalImage = ImageIO.read(new File(getAvatarDir() + LOGO + PNG));
		BufferedImage scaledImg = Scalr.resize(originalImage, 50);
		ImageIO.write(scaledImg, "png", new File(getAvatarDir() + SMALL + LOGO + PNG));
	}

	private String getAvatarDir() {
		return appSrv.getProperty(AppService.TASQROOTDIR) + File.separator + AVATAR_DIR + File.separator;
	}

}
