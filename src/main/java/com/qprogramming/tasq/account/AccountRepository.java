package com.qprogramming.tasq.account;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	
	Account findByEmail(String email);

	Account findByUsername(String username);

	Account findById(Long id);

	List<Account> findByNameStartingWith(String name);

	List<Account> findBySurnameStartingWith(String surname);

	List<Account> findByRole(Account.Role role);
	
	Account findByNameAndSurname(String name, String surname);
}
