package com.qprogramming.tasq.signup;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.velocity.app.VelocityEngine;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.uuid.Generators;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountRepository;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.ThemeService;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class SignupControllerTest {

	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";

	private SignupController signupCtr;

	@InjectMocks
	private AccountService accountSrv;

	@Mock
	private EntityManager entityManagerMock;
	@Mock
	private AccountRepository accRepoMock;
	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private RedirectAttributes raMock;
	@Mock
	private Authentication authMock;
	@Mock
	private Model modelMock;
	@Mock
	private MessageSource msgMock;
	@Mock
	private HttpServletResponse responseMock;
	@Mock
	private HttpServletRequest requestMock;
	@Mock
	private SessionLocaleResolver localeResolver;
	@Mock
	private HttpSession httpSesssionMock;
	@Mock
	private ServletContext scMock;
	@Mock
	private ServletOutputStream outStreamMock;
	@Mock
	private VelocityEngine velocityMock;
	@Mock
	private ResourceService resourceMock;
	@Mock
	private MailMail mailerMock;
	@Mock
	private ThemeService themeSrvMock;
	@Mock
	private PasswordEncoder encoderMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount;
	private static final String LAMB = "Lamb";
	private static final String ZOE = "Zoe";
	private static final String ART = "Art";
	private static final String DOE = "Doe";
	private static final String KATE = "Kate";
	private static final String JOHN = "John";
	private static final String ADAM = "Adam";
	private static final String MARRY = "Marry";
	private static final String SORT_BY_NAME = "name";
	private static final String SORT_BY_EMAIL = "email";
	private static final String SORT_BY_SURNAME = "surname";
	private static final String USERNAME = "user";

	private List<Account> accountsList;

	@Before
	public void setUp() {
		accountSrv = new AccountService(accRepoMock, msgMock, velocityMock, resourceMock, mailerMock, encoderMock);
		signupCtr = new SignupController(accountSrv, msgMock, themeSrvMock);
		testAccount = new Account(EMAIL, "", USERNAME, Roles.ROLE_ADMIN);
		testAccount.setLanguage("en");
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		accountsList = createList();
	}

	@Test
	public void signUpAndConfirmTest() {
		try {
			when(requestMock.getSession()).thenReturn(httpSesssionMock);
			URL fileURL = getClass().getResource("/avatar.png");
			when(httpSesssionMock.getServletContext()).thenReturn(scMock);
			when(scMock.getRealPath(anyString())).thenReturn(fileURL.getFile());
			when(responseMock.getOutputStream()).thenReturn(outStreamMock);
			when(accRepoMock.findAll()).thenReturn(accountsList);
			when(accRepoMock.findByEmail(NEW_EMAIL)).thenReturn(null);
			when(encoderMock.encode(any(CharSequence.class))).thenReturn("encodedPassword");
			SignupForm form = fillForm();
			Errors errors = new BeanPropertyBindingResult(form, "form");
			signupCtr.signup(form, errors, raMock, requestMock);
			verify(accRepoMock, times(1)).save(any(Account.class));
			when(accRepoMock.findByUuid("confirmMe")).thenReturn(testAccount);
			signupCtr.confirm("confirmMe", raMock, requestMock);
			verify(accRepoMock, times(1)).save(testAccount);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void signUpFormErrorTest() {
		SignupForm form = fillForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		errors.rejectValue("name", "error");
		signupCtr.signup(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void signUpEmailFormErrorTest() {
		when(accRepoMock.findByEmail(NEW_EMAIL)).thenReturn(testAccount);
		SignupForm form = fillForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		signupCtr.signup(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void confirmErrorTest() throws ServletException {
		when(accRepoMock.findByUuid("confirmMe")).thenReturn(null);
		signupCtr.confirm("don't confirm me", raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));

	}

	@Test
	public void SignupPasswordNotMatchingTest() {
		SignupForm form = fillForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		form.setConfirmPassword("wrong");
		signupCtr.signup(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void resetSubmitNotMatchingTest() {
		PasswordResetForm form = fillPasswordForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		form.setConfirmPassword("wrong");
		signupCtr.resetSubmit(form, errors, raMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void resetSubmitNoAccountTest() {
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");

		PasswordResetForm form = fillPasswordForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		signupCtr.resetSubmit(form, errors, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void resetSubmitTest() {
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
		when(accountSrv.findByUuid(anyString())).thenReturn(testAccount);
		when(encoderMock.encode(any(CharSequence.class))).thenReturn("encodedPassword");
		PasswordResetForm form = fillPasswordForm();
		Errors errors = new BeanPropertyBindingResult(form, "form");
		signupCtr.resetSubmit(form, errors, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void resetPasswordNoAccountTest() {
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
		signupCtr.resetPassword(EMAIL, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.WARNING, new Object[] {}));
	}

	@Test
	public void resetPasswordTest() {
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
		when(accountSrv.findByEmail(EMAIL)).thenReturn(testAccount);
		when(requestMock.getScheme()).thenReturn("http");
		when(requestMock.getServerName()).thenReturn("testServer");
		when(requestMock.getServerPort()).thenReturn(8080);
		signupCtr.resetPassword(EMAIL, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.WARNING, new Object[] {}));
	}

	private List<Account> createList() {
		List<Account> accountsList = new LinkedList<Account>();
		accountsList.add(createAccount(JOHN, DOE));
		accountsList.add(createAccount(ADAM, ART));
		accountsList.add(createAccount(ADAM, ZOE));
		accountsList.add(createAccount(MARRY, LAMB));
		accountsList.add(createAccount(KATE, DOE));
		return accountsList;
	}

	private Account createAccount(String name, String surname) {
		Account account = new Account(name + "@test.com", "", name, Roles.ROLE_POWERUSER);
		account.setName(name);
		account.setSurname(surname);
		return account;
	}

	private SignupForm fillForm() {
		SignupForm form = signupCtr.signup();
		form.setName(ADAM);
		form.setSurname(DOE);
		form.setEmail(NEW_EMAIL);
		form.setPassword(PASSWORD);
		form.setConfirmPassword(PASSWORD);
		return form;
	}

	private PasswordResetForm fillPasswordForm() {
		PasswordResetForm form = new PasswordResetForm();
		form.setPassword(PASSWORD);
		form.setConfirmPassword(PASSWORD);
		UUID uuid = Generators.timeBasedGenerator().generate();
		form.setId(uuid.toString());
		return form;
	}

}
