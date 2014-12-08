package com.qprogramming.tasq.account;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService = new UserService();

	@Mock
	private AccountService accountServiceMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
}
