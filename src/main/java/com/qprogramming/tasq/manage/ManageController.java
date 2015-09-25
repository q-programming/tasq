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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.web.MessageHelper;

@Controller
public class ManageController {

	@Value("${home.directory}")
	private String tasqRootDir;
	private static final String AVATAR_DIR = "avatar";
	private static final String PNG = ".png";
	private static final String LOGO = "logo";
	private static final String SMALL_LOGO = "small_logo";

	private static final Logger LOG = LoggerFactory.getLogger(ManageController.class);

	private ThemeService themeSrv;
	private MessageSource msg;
	private ProjectService projSrv;

	@Autowired
	public ManageController(ThemeService themeSrv, MessageSource msg, ProjectService projSrv) {
		this.themeSrv = themeSrv;
		this.msg = msg;
		this.projSrv = projSrv;
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

	@RequestMapping(value = "manage/app", method = RequestMethod.GET)
	public String manageApplication(RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		model.addAttribute("themes", themeSrv.findAll());
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
		ImageIO.write(scaledImg, "png", new File(getAvatarDir() + SMALL_LOGO + PNG));
	}

	private String getAvatarDir() {
		return tasqRootDir + File.separator + AVATAR_DIR + File.separator;
	}

}
