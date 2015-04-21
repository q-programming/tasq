package com.qprogramming.tasq.events;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends
		JpaRepository<Event, Integer> {

	Event findById(Long id);

	List<Event> findByAccountIdOrderByDateDesc(Long userId);

	List<Event> findByAccountIdAndUnreadTrue(Long userId);

	Page<Event> findByAccountId(Long id, Pageable p);
}
