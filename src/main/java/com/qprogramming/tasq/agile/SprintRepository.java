package com.qprogramming.tasq.agile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Integer> {

	Sprint findByName(String Name);

	Sprint findById(Long id);

	/**
	 * Lists sprints that are belonging to Project and are finished
	 * 
	 * @param projectId
	 * @param finished
	 * @return
	 */
	List<Sprint> findByProjectIdAndFinished(Long projectId, boolean finished);
	List<Sprint> findByProjectProjectIdAndFinished(String projectId, boolean finished);

	/**
	 * Returns active sprint from project (by default only one sprint per
	 * project can be active)
	 * 
	 * @param project_id
	 * @param active
	 * @return
	 */
	Sprint findByProjectIdAndActiveTrue(Long projectId);

	Sprint findByProjectIdAndSprintNo(Long projectId, Long sprintNo);

	List<Sprint> findByProjectId(Long projectId);
}
