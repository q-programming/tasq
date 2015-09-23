package com.qprogramming.tasq.account;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserService implements UserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private AccountService accountSrv;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountSrv.findByEmail(username);
		// try to find by username
		if (account == null) {
			account = accountSrv.findByUsername(username);
		}
		if (account == null) {
			LOG.error("User not found or not confirmed registration.");
			throw new UsernameNotFoundException("User not found or is not confirmed");
		}
		account.setAuthority(account.getRole());
		return account;
	}

	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(account, null, Collections.singleton(createAuthority(account)));
	}

	private GrantedAuthority createAuthority(Account account) {
		return new SimpleGrantedAuthority(account.getRole().toString());
	}

}
