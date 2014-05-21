package com.qprogramming.tasq.signin;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.qprogramming.tasq.support.Utils;

@Component
public class LocaleSettingAuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	@Autowired
	private SessionLocaleResolver localeResolver;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
		String locale = Utils.getCurrentAccount().getLanguage();
		Locale loc = new Locale(locale);
		localeResolver.setLocale(request, response, loc);
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
