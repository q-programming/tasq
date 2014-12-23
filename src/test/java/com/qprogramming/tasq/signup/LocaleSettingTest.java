package com.qprogramming.tasq.signup;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountController;
import com.qprogramming.tasq.account.AccountRepository;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.signin.LocaleSettingAuthenticationSuccessHandler;
import com.qprogramming.tasq.support.web.Message;

@RunWith(MockitoJUnitRunner.class)
public class LocaleSettingTest {

	@InjectMocks
	private LocaleSettingAuthenticationSuccessHandler localeSettter;

	@Mock
	private AccountRepository accRepoMock;

	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private Authentication authMock;
	@Mock
	private HttpServletResponse responseMock;
	@Mock
	private HttpServletRequest requestMock;
	@Mock
	private SessionLocaleResolver localeResolver;
	@Mock
	private HttpSession sessionMock;
	@Mock
	private SavedRequest savedRequestMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount = new Account("user@test.com", "", Roles.ROLE_ADMIN);
	
	@Before
	public void setUp(){
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
	}


	@Test
	public void testLoggin() {
		try {
			when(requestMock.getSession(false)).thenReturn(sessionMock);
			when(sessionMock.getAttribute(anyString())).thenReturn(savedRequestMock);
			when(savedRequestMock.getRedirectUrl()).thenReturn("projects/show");
			localeSettter.onAuthenticationSuccess(requestMock, responseMock,
					authMock);
			
		} catch (ServletException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
