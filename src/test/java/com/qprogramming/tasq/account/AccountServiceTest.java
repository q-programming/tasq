package com.qprogramming.tasq.account;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

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

import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

	private static final String EMAIL = "user@test.com";
	private static final String USERNAME = "test";

	@InjectMocks
	private AccountService accountSrv;

	@Mock
	private AccountRepository accRepoMomck;

	@Mock
	private MockSecurityContext securityMock;

	@Mock
	private Authentication authMock;

	@Mock
	private MessageSource msgMock;
	@Mock
	private VelocityEngine velocityMock;
	@Mock
	private ResourceService resourceMock;
	@Mock
	private MailMail mailerMock;
	@Mock
	private PasswordEncoder encoderMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount;

	@Before
	public void setUp() {
		accountSrv = new AccountService(accRepoMomck, msgMock, velocityMock, resourceMock, mailerMock, encoderMock);
		testAccount = new Account(EMAIL, "", USERNAME, Roles.ROLE_ADMIN);
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
	}

	@Test
	public void findByEmailTest() {
		when(accRepoMomck.findByEmail(anyString())).thenReturn(testAccount);
		Assert.assertEquals(testAccount, accountSrv.findByEmail(EMAIL));
	}

	@Test
	public void findByIdTest() {
		when(accRepoMomck.findById(anyLong())).thenReturn(testAccount);
		Assert.assertEquals(testAccount, accountSrv.findById(1L));
	}

	@Test
	public void findByUIIDTest() {
		when(accRepoMomck.findByUuid(anyString())).thenReturn(testAccount);
		Assert.assertEquals(testAccount, accountSrv.findByUuid("54564564564564"));
	}

	@Test
	public void findByUserNameTest() {
		when(accRepoMomck.findByUsername("user")).thenReturn(testAccount);
		Assert.assertEquals(testAccount, accountSrv.findByUsername("user"));
	}

}
