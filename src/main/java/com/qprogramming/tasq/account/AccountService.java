/**
 * 
 */
package com.qprogramming.tasq.account;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.uuid.Generators;

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
	public Account save(Account account, boolean passwordReset) {
		if (passwordReset) {
			account.setPassword(passwordEncoder.encode(account.getPassword()));
		}
		if (account.getLanguage() == null) {
			account.setLanguage(defaultLang);
		}
		UUID uuid = Generators.timeBasedGenerator().generate();
		account.setUuid(uuid.toString());
		entityManager.persist(account);
		return account;
	}

	public Account findByEmail(String email) {
		return accRepo.findByEmail(email);
	}

	public Account findByUsername(String username) {
		return accRepo.findByUsername(username);
	}

	public Account findByUuid(String uiid) {
		return accRepo.findByUuid(uiid);
	}

	/**
	 * @param account
	 */
	@Transactional
	public Account update(Account account) {
		return accRepo.save(account);
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

	public Page<Account> findAll(Pageable p) {
		return accRepo.findAll(p);
	}

	public Page<Account> findByStartingWith(String term, Pageable p) {
		return accRepo
				.findBySurnameStartingWithIgnoreCaseOrNameStartingWithIgnoreCase(
						term, term, p);
	}

	public List<Account> findAdmins() {
		return accRepo.findByRole(Roles.ROLE_ADMIN);
	}
}
