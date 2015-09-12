package com.qprogramming.tasq.account;

import org.junit.Assert;
import org.junit.Test;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;

public class AccountClassTest {
	private static final String LAMB = "Lamb";
	private static final String ZOE = "Zoe";

	@Test
	public void displayAccountTest() {
		Account account = new Account(ZOE + "@test.com", "",ZOE, Roles.ROLE_POWERUSER);
		account.setName(ZOE);
		account.setSurname(LAMB);
		account.setId(1L);
		DisplayAccount disp = new DisplayAccount(account);
		DisplayAccount disp2 = new DisplayAccount(account);
		Assert.assertEquals(disp, disp2);
		Assert.assertEquals(disp.getName(), disp2.getName());
		Assert.assertEquals(disp.getUsername(), disp2.getUsername());
		Assert.assertEquals(disp.toString(), disp2.toString());
		Assert.assertEquals(disp.getSurname(), disp2.getSurname());
		Assert.assertEquals(disp.getEmail(), disp2.getEmail());
		Assert.assertEquals(disp.hashCode(), disp2.hashCode());
	}

	@Test
	public void accountTest() {
		Account account = new Account(ZOE + "@test.com", "",ZOE, Roles.ROLE_POWERUSER);
		account.setName(ZOE);
		account.setSurname(LAMB);
		account.setId(1L);
		Account account2 = new Account(ZOE + "@test.com", "",ZOE, Roles.ROLE_POWERUSER);
		account2.setName(ZOE);
		account2.setSurname(LAMB);
		account2.setId(1L);
		Assert.assertEquals(account, account2);
		Assert.assertEquals(account.hashCode(), account2.hashCode());
		Assert.assertEquals(account.getName(), account2.getName());
		account2.setUsername("newUserName");
		account2.setEmail("new@test.com");
		account2.setTheme("red");
		account2.setRole(Roles.ROLE_ADMIN);
		Assert.assertNotNull(account2.getTheme());
		Assert.assertFalse(account.equals(account2));
		Assert.assertTrue(account.isAccountNonExpired());
		Assert.assertTrue(account.isAccountNonLocked());
		Assert.assertTrue(account.isCredentialsNonExpired());
		Assert.assertFalse(account.isConfirmed());
		Assert.assertTrue(account.isEnabled());
	}

}
