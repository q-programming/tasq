package com.qprogramming.tasq.projects;

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
		return projRepo.findByParticipants_Id(curent_user.getId());
	}

	public Project activate(Long id) {
		Project project = projRepo.findById(id);
		if (project != null) {
			List<Project> users_projects = findAllByUser();
			Project active_project = null;
			for (Project user_project : users_projects) {
				if (user_project.isActive()) {
					active_project = user_project;
				}
			}
			if (active_project != null) {
				active_project.setActive(false);
				projRepo.save(active_project);
			}
			project.setActive(true);
			return projRepo.save(project);
		}
		return null;
	}

	public Project findByProjectId(String project_id) {
		return projRepo.findByProjectId(project_id);
	}

	/**
	 * @return
	 */
	public Project findUserActiveProject() {
		List<Project> projects = findAllByUser();
		Project active = null;
		for (Project project : projects) {
			if (project.isActive()) {
				active = project;
			}
		}
		return active;
	}
}
