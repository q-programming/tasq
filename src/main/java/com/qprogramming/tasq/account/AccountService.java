/**
 * 
 */
package com.qprogramming.tasq.account;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.support.Utils;

/**
 * @author romanjak
 * @date 21 maj 2014
 */
@Service
public class AccountService {
	@Value("${default.locale}")
	private String defaultLang;

	@Autowired
	AccountRepository accRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Transactional
	public Account save(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.setLanguage(defaultLang);
		account.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		entityManager.persist(account);
		return account;
	}

	public Account findByEmail(String email) {
		return accRepo.findByEmail(email);
	}

	public Account findByUsername(String username) {
		return accRepo.findByUsername(username);
	}

	public Account findByUuid(String id) {
		return accRepo.findByUuid(id);
	}

	/**
	 * @param account
	 */
	@Transactional
	public void update(Account account) {
		accRepo.save(account);
	}

	public Account findById(Long id) {
		return accRepo.findById(id);
	}

	/**
	 * @return
	 */
	public List<Account> findAll() {
		return accRepo.findAll();
	}

	public Account getCurrent() {
		return accRepo.findByEmail(Utils.getCurrentAccount().getEmail());
	}

	public List<Account> findByNameStartingWith(String name) {
		return accRepo.findByNameStartingWith(name);
	}

	public List<Account> findBySurnameStartingWith(String name) {
		return accRepo.findBySurnameStartingWith(name);
	}

	public List<Account> findAdmins() {
		return accRepo.findByRole(Roles.ROLE_ADMIN);
	}
}
