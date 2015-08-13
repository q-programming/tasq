package com.qprogramming.tasq.account;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

	private static final String EMAIL = "user@test.com";

	private AccountController accountCtr;

	@Mock
	private AccountService accSrvMock;
	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private RedirectAttributes raMock;
	@Mock
	private ProjectService projSrvMock;
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
	private HttpSession httpSesssionMock;
	@Mock
	private ServletContext scMock;
	@Mock
	private ServletOutputStream outStreamMock;
	@Mock
	private SessionLocaleResolver localeResolverMock;
	@Mock 
	private SessionRegistry sessionRegistry;

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

	private List<Account> accountsList;

	@Before
	public void setUp() {
		accountCtr = new AccountController(accSrvMock, projSrvMock, msgMock,
				localeResolverMock,sessionRegistry);
		testAccount = new Account(EMAIL, "", Roles.ROLE_ADMIN);
		testAccount.setLanguage("en");
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		accountsList = createList();
	}

	@Test
	public void getUserTest() {
		when(accSrvMock.findById(1L)).thenReturn(testAccount);
		when(projSrvMock.findAllByUser(1L)).thenReturn(null);
		accountCtr.getUser(1L, modelMock, raMock);
		verify(modelMock, times(2)).addAttribute(anyString(), anyObject());
	}

	@Test
	public void getUserNotFoundTest() {
		when(accSrvMock.findById(1L)).thenReturn(null);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		accountCtr.getUser(1L, modelMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));

	}

	@Test
	public void listUsersTest() {
		List<Account> single = new LinkedList<Account>();
		single.add(testAccount);
		Page<Account> result = new PageImpl<Account>(accountsList);
		Page<Account> singleResult = new PageImpl<Account>(single);
		List<Object> principals = new LinkedList<Object>();
		List<SessionInformation> sessions = new LinkedList<SessionInformation>();
		principals.add(testAccount);
		SessionInformation session = new SessionInformation(testAccount, "12345", new Date());
		sessions.add(session);
		Pageable p = new PageRequest(0, 5, new Sort(Sort.Direction.ASC, "surname"));
		when(accSrvMock.findByNameSurnameContaining("Do", p)).thenReturn(singleResult);
		when(accSrvMock.findAll(p)).thenReturn(result);
		when(sessionRegistry.getAllPrincipals()).thenReturn(principals);
		when(sessionRegistry.getAllSessions(testAccount, false)).thenReturn(sessions);
		Assert.assertEquals(5, accountCtr.listUsers(null, p).getTotalElements());  accountCtr.listUsers(null, p);
		Assert.assertEquals(1, accountCtr.listUsers("Do", p).getTotalElements());  accountCtr.listUsers(null, p);
	}


	@Test
	public void getAccountsTest() {
		when(accSrvMock.findAll()).thenReturn(accountsList);
		List<DisplayAccount> list = accountCtr.listAccounts(null, responseMock);
		Assert.assertEquals(5, list.size());
	}

	@Test
	public void setRoleTest() {
		List<Account> admins = new LinkedList<Account>();
		admins.add(testAccount);
		when(accSrvMock.findById(anyLong())).thenReturn(testAccount);
		when(accSrvMock.findAdmins()).thenReturn(admins);
		Assert.assertNotEquals("OK", accountCtr.setRole(1L, Roles.ROLE_POWERUSER));

	}

	@Test
	public void saveSettingsTest() {
		URL fileURL = getClass().getResource("/avatar.png");
		MockMultipartFile mockMultipartFile;
		try {
			mockMultipartFile = new MockMultipartFile("content",
					fileURL.getFile(), "text/plain", getClass()
							.getResourceAsStream("/avatar.png"));
			accountCtr.saveSettings(mockMultipartFile, EMAIL, "en", "red",
					raMock, requestMock, responseMock);
			verify(accSrvMock, times(1)).update(any(Account.class));
			verify(localeResolverMock, times(1)).setLocale(requestMock,
					responseMock, new Locale("en"));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void saveSettingsAvatarTooBigTest() {
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		URL fileURL = getClass().getResource("/avatar_tooBig.png");
		MockMultipartFile mockMultipartFile;
		try {
			mockMultipartFile = new MockMultipartFile("content",
					fileURL.getFile(), "text/plain", getClass()
							.getResourceAsStream("/avatar_tooBig.png"));
			accountCtr.saveSettings(mockMultipartFile, EMAIL, "en", "red",
					raMock, requestMock, responseMock);
			verify(raMock, times(1)).addFlashAttribute(
					anyString(),
					new Message(anyString(), Message.Type.DANGER,
							new Object[] {}));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
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
		Account account = new Account(name + "@test.com", "", Roles.ROLE_POWERUSER);
		account.setName(name);
		account.setSurname(surname);
		return account;

	}

}
