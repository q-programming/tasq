package com.qprogramming.tasq.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    List<Account> findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String term, String term1, String term2);

    Page<Account> findBySurnameContainingIgnoreCaseOrNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String term, String term1, String term2, Pageable p);

    List<Account> findByActiveTask(String taskID);
}
