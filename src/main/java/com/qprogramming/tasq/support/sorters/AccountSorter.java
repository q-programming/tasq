package com.qprogramming.tasq.support.sorters;

import java.util.Comparator;
import com.qprogramming.tasq.account.Account;


public class AccountSorter implements Comparator<Account> {
	public static enum SORTBY {
		NAME, SURNAME, EMAIL
	};

	private SORTBY sortBy;
	private boolean descending;

	public AccountSorter(SORTBY sortBy, boolean isDescending) {
		this.sortBy = sortBy;
		descending = isDescending;
	}

	public int compare(Account a, Account b) {
		int result = 0;
		switch (sortBy) {
		case NAME:
			result = a.getName().compareTo(b.getName());
			break;
		case SURNAME:
			result = a.getSurname().compareTo(b.getSurname());
			break;
		case EMAIL:
			result = a.getEmail().compareTo(b.getEmail());
			break;
		default:
			return 0;
		}
		return descending ? -result : result;
	}
}