package com.qprogramming.tasq.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;

@Controller
public class ManageController {

	private ThemeRepository themeRepo;

	@Autowired
	public ManageController(ThemeRepository themeRepo) {
		this.themeRepo = themeRepo;
	}

	@RequestMapping(value = "manage/tasks", method = RequestMethod.GET)
	public String manageTasks(RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
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
		model.addAttribute("themes", themeRepo.findAll());
		return "admin/manage";
	}

	@RequestMapping(value = "manage/createTheme", method = RequestMethod.POST)
	public String createTheme(@RequestParam(value = "name") String name, @RequestParam(value = "font") Font font,
			@RequestParam(value = "color") String color, @RequestParam(value = "invcolor") String invcolor,
			RedirectAttributes ra, Model model) {
		if (!Roles.isAdmin()) {
			throw new TasqAuthException();
		}
		Theme theme = new Theme();
		theme.setName(name);
		theme.setFont(font);
		theme.setColor(color);
		theme.setInvColor(invcolor);
		themeRepo.save(theme);
		return "redirect:/manage/app";
	}

}
