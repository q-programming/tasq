package com.qprogramming.tasq.signup;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.support.web.*;

@Controller
public class SignupController {
	
	@Autowired
	private AccountService accountSrv;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}
	
	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return null;
		}
		
		Account account = accountSrv.save(signupForm.createAccount());
		userService.signin(account);

        MessageHelper.addSuccessAttribute(ra, "Congratulations! You have successfully signed up.");
		
		return "redirect:/";
	}
}
