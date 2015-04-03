package com.qprogramming.tasq.account;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

	Account findByEmail(String email);

	Account findByUsername(String username);

	Account findById(Long id);

	Account findByUuid(String id);

	Page<Account> findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase(
			String term, String term2, Pageable p);

	List<Account> findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCase(
			String term, String term2);

	List<Account> findByRole(Roles role);

	Account findByNameAndSurname(String name, String surname);
}
