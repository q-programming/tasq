package com.qprogramming.tasq.projects;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.support.Utils;

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

	public List<Project> findAll() {
		return projRepo.findAll();
	}

	public List<Project> findAllByUser() {
		Account curent_user = Utils.getCurrentAccount();
		List<Project> projects = findAll();
		List<Project> userProjects = new LinkedList<Project>();
		for (Project project : projects) {
			if (project.getAdministrator().equals(curent_user)
					|| project.getParticipants().contains(curent_user)) {
				userProjects.add(project);
			}
		}
		return userProjects;
	}
}
