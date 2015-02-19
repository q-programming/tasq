package com.qprogramming.tasq.task.events;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsRepository extends
		JpaRepository<Event, Integer> {

	Event findById(Long id);

	List<Event> findByAccountId(Long userId);

	List<Event> findByAccountIdAndUnreadTrue(Long userId);
}
