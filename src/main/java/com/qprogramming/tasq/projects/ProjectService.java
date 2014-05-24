package com.qprogramming.tasq.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projRepo;

	public Project findByName(String name) {
		return projRepo.findByName(name);
	}

	public Project findById(Long id) {
		return projRepo.findById(id);
	}

	@Transactional
	public Project save(Project project) {
		return projRepo.save(project);
	}

}
