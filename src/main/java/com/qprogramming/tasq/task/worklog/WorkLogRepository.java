package com.qprogramming.tasq.task.worklog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Integer> {

	WorkLog findById(Long id);
}
