package com.qprogramming.tasq.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.signin.PersistentTokenRepositoryImpl;
import com.qprogramming.tasq.signin.RememberMeTokenRepository;


@Configuration
@ImportResource(value = "classpath:spring-security-context.xml")
public class SecurityConfig {
	
	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Autowired
	private RememberMeTokenRepository rememberMeTokenRepository;

	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
				"test", userService(),
				persistentTokenRepository());
		return rememberMeServices;
	}

	@Bean
	public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
		RememberMeAuthenticationProvider rememberMeAuthenticationProvider = new RememberMeAuthenticationProvider(
				"test");
		return rememberMeAuthenticationProvider;
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		return new PersistentTokenRepositoryImpl(rememberMeTokenRepository);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}
}