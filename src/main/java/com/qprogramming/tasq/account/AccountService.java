/**
 * 
 */
package com.qprogramming.tasq.account;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author romanjak
 * @date 21 maj 2014
 */
@Service
public class AccountService {
	@Autowired
	AccountRepository accRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		entityManager.persist(account);
		return account;
	}
	
	public Account findByEmail(String email){
		return accRepo.findByEmail(email);
	}
	
	public Account findByUsername(String username){
		return accRepo.findByUsername(username);
	}

	
	
}
