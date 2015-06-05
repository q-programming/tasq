package com.qprogramming.tasq.account;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.support.Utils;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService = new UserService();

	@Mock
	private AccountService accountServiceMock;

	@Mock
	private MockSecurityContext securityMock;

	@Mock
	private Authentication authMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount;

	@Before
	public void setUp() {
		testAccount = new Account("user@test.com", "", Roles.ROLE_ADMIN);
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
	}

	@Test
	public void shouldThrowExceptionWhenUserNotFound() {
		// arrange
		thrown.expect(UsernameNotFoundException.class);
		thrown.expectMessage("User not found or is not confirmed");

		when(accountServiceMock.findByEmail("user@example.com")).thenReturn(
				null);
		// act
		userService.loadUserByUsername("user@example.com");
	}

	@Test
	public void shouldReturnUserDetails() {
		// arrange
		Account demoUser = new Account("user@example.com", "demo",
				Roles.ROLE_USER);
		demoUser.setConfirmed(true);
		when(accountServiceMock.findByEmail("user@example.com")).thenReturn(
				demoUser);
		// act
		UserDetails userDetails = userService
				.loadUserByUsername("user@example.com");
		// assert
		Assert.assertTrue(demoUser.getUsername().equals(
				userDetails.getUsername()));
		Assert.assertTrue(demoUser.getPassword().equals(
				userDetails.getPassword()));
	}

	@Test
	public void signInTest() {
		userService.signin(testAccount);
		Assert.assertNotNull(Utils.getCurrentAccount());
	}

}
