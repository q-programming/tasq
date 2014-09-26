package com.qprogramming.tasq.task.worklog;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Integer> {

	WorkLog findById(Long id);

	List<WorkLog> findByTaskProjectId(Long id);

	List<WorkLog> findByTask_worklogLike(String id);

	List<WorkLog> findByProjectId(Long id);

	List<WorkLog> findByProjectIdAndTimeBetweenAndActivityNotNullOrderByTimeAsc(Long id, Date start, Date end);

}
