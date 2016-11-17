package com.qprogramming.tasq.config;

import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.signin.PersistentTokenRepositoryImpl;
import com.qprogramming.tasq.signin.RememberMeTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;


@Configuration
@ImportResource(value = "classpath:spring-security-context.xml")
public class SecurityConfig {

    @Autowired
    private RememberMeTokenRepository rememberMeTokenRepository;

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        return new PersistentTokenBasedRememberMeServices(
                "tasq-aWreDWE4343asrwerewraWeFFTgxcv9u1X", userService(),
                persistentTokenRepository());
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider(
                "test");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new PersistentTokenRepositoryImpl(rememberMeTokenRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }
}