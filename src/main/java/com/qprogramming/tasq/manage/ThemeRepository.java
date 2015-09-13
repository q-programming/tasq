package com.qprogramming.tasq.manage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {

	Theme findById(Long id);

	Theme findByName(String name);
}
