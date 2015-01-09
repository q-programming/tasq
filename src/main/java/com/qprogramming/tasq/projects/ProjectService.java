package com.qprogramming.tasq.projects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.support.Utils;

@Service
public class ProjectService {

	private ProjectRepository projRepo;
	private AccountService accSrv;

	@Autowired
	public ProjectService(ProjectRepository projRepo, AccountService accSrv) {
		this.projRepo = projRepo;
		this.accSrv = accSrv;
	}

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

	public List<Project> findAllByUser(Long id) {
		return projRepo.findByParticipants_Id(id);
	}

	public Project activate(Long id) {
		Project project = projRepo.findById(id);
		if (project != null) {
			Account account = Utils.getCurrentAccount();
			account.setActive_project(project.getId());
			accSrv.update(account);
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
		Account account = Utils.getCurrentAccount();
		if (account.getActive_project() != null) {
			return projRepo.findById(account.getActive_project());
		}
		return null;
	}

	/**
	 * Checks if currently logged in user have privileges to change anything in
	 * project
	 * 
	 * @param task
	 * @return
	 */
	public boolean canEdit(Long projectID) {
		Project repoProject = projRepo.findById(projectID);
		if (repoProject == null) {
			return false;
		}
		Account currentAccount = Utils.getCurrentAccount();
		return repoProject.getAdministrators().contains(currentAccount)
				|| repoProject.getParticipants().contains(currentAccount)
				|| Roles.isAdmin();
	}

	public boolean canEdit(Project project) {
		return canEdit(project.getId());
	}
}
