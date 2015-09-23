package com.qprogramming.tasq.signin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, Integer> {
	RememberMeToken findBySeries(String series);

	List<RememberMeToken> findByUsername(String username);
}