package com.qprogramming.tasq.agile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Integer> {

	Sprint findByName(String Name);

	Sprint findById(Long id);

	List<Sprint> findByProjectIdAndFinished(Long project_id, boolean finished);
	
	Sprint findByProjectIdAndActive(Long project_id, boolean active);
	Sprint findByProjectIdAndSprintNo(Long project_id, Long sprint_no);

	List<Sprint> findByProjectId(Long project_id);
}
