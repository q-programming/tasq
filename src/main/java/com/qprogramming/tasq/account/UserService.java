package com.qprogramming.tasq.account;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService implements UserDetailsService {
	
	@Autowired
	private AccountService accountSrv;
	
//	@Inject
//	private PasswordEncoder passwordEncoder;
	
//	@PostConstruct	
	protected void initialize() {
		accountSrv.save(new Account("user", "demo", "ROLE_USER"));
		accountSrv.save(new Account("admin", "admin", "ROLE_ADMIN"));
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountSrv.findByEmail(username);
		if(account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		//account.setPassword(passwordEncoder.encode(account.getPassword()));
		return account;
	}
	
	public void signin(Account account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}
	
	private Authentication authenticate(Account account) {
		return new UsernamePasswordAuthenticationToken(account, null, Collections.singleton(createAuthority(account)));		
	}
	
//	private User createUser(Account account) {
//		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority(account)));
//	}

	private GrantedAuthority createAuthority(Account account) {
		return new SimpleGrantedAuthority(account.getRole().toString());
	}

}
