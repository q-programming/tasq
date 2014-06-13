/**
 * 
 */
package com.qprogramming.tasq.account;

public class DisplayAccount {
	private String name;
	private String surname;
	private String email;
	private String username;
	private Long id;

	public DisplayAccount(Account account) {
		setId(account.getId());
		setName(account.getName());
		setSurname(account.getSurname());
		setEmail(account.getEmail());
		setUsername(account.getUsername());

	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
