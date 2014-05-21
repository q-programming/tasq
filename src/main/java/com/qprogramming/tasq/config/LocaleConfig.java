package com.qprogramming.tasq.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;


@Configuration
public class LocaleConfig extends SavedRequestAwareAuthenticationSuccessHandler {

	@Value("${default.locale}")
	private String defaultLang;


	@Bean
	public SessionLocaleResolver localeResolver() {
		SessionLocaleResolver lr = new SessionLocaleResolver();
		lr.setDefaultLocale(new Locale(defaultLang));
		return lr;
	}
}
