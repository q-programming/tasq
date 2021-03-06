package com.qprogramming.tasq.task.watched;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchedTaskRepository extends
		JpaRepository<WatchedTask, Integer> {

	WatchedTask findById(String id);

	List<WatchedTask> findByWatchersId(Long userId);

	Page<WatchedTask> findByWatchersId(Long id, Pageable p);

}
