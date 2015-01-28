package com.qprogramming.tasq.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;

@Controller
public class ManageController {

	private AccountService accountSrv;

	@Autowired
	public ManageController(AccountService accountSrv) {
		this.accountSrv = accountSrv;
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
		List<Account> accountsList;
		accountsList = accountSrv.findAll();
		model.addAttribute("accountsList", accountsList);
		return "admin/users";
	}
}
