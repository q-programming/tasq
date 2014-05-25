package com.qprogramming.tasq.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

	Project findByName(String Name);

	Project findById(Long id);
	
	Project findByActive (boolean active);

	Project findByProjectId(String project_id);
}
