package com.qprogramming.tasq.support.sorters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;

@RunWith(MockitoJUnitRunner.class)
public class AccountsSorterTest {

	private static final String LAMB = "Lamb";
	private static final String ZOE = "Zoe";
	private static final String ART = "Art";
	private static final String DOE = "Doe";
	private static final String KATE = "Kate";
	private static final String JOHN = "John";
	private static final String ADAM = "Adam";
	private static final String MARRY = "Marry";
	private List<Account> accountsList;

	@Before
	public void setUp() {
		accountsList = createList();
	}

	@Test
	public void sortAccountsTest() {
		Collections.sort(accountsList, new AccountSorter(
				AccountSorter.SORTBY.NAME, true));
		Assert.assertEquals(MARRY, accountsList.get(0).getName());
	}

	@Test
	public void sortAccountsSurnameTest() {
		Collections.sort(accountsList, new AccountSorter(
				AccountSorter.SORTBY.SURNAME, false));
		Assert.assertEquals(ART, accountsList.get(0).getSurname());
	}

	@Test
	public void sortAccountsEmailTest() {
		Collections.sort(accountsList, new AccountSorter(
				AccountSorter.SORTBY.EMAIL, true));
		Assert.assertEquals(LAMB, accountsList.get(0).getSurname());
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
		Account account = new Account(name + "@test.com", "", Roles.ROLE_USER);
		account.setName(name);
		account.setSurname(surname);
		return account;

	}
}
