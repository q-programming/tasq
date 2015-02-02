package com.qprogramming.tasq.manage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;

@Controller
public class ManageController {

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
}
