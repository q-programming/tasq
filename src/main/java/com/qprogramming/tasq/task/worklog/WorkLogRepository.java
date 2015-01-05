package com.qprogramming.tasq.task.worklog;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Integer> {

	WorkLog findById(Long id);

	List<WorkLog> findByProjectId(Long id);

	Page<WorkLog> findByProjectId(Long id, Pageable page);

	List<WorkLog> findByProjectIdAndTimeBetweenAndActivityNotNullOrderByTimeAsc(
			Long id, Date start, Date end);

	List<WorkLog> findByProjectIdAndTimeBetweenAndTypeOrTypeOrderByTimeAsc(
			Long id, Date start, Date end, LogType closed, LogType reopen);
	
	List<WorkLog> findByProjectIdAndTimeBetweenOrderByTimeAsc(
			Long id, Date start, Date end);

	List<WorkLog> findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
			Long id, Date start, Date end);

}
