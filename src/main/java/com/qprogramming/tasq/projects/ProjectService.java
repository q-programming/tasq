package com.qprogramming.tasq.projects;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.support.Utils;

@Service
public class ProjectService {

	private ProjectRepository projRepo;
	private AccountService accSrv;
	private UserService userSrv;

	@Autowired
	public ProjectService(ProjectRepository projRepo, AccountService accSrv, UserService userSrv) {
		this.projRepo = projRepo;
		this.accSrv = accSrv;
		this.userSrv = userSrv;
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
		if (curent_user.getIsAdmin()) {
			return projRepo.findAll();
		} else {
			return projRepo.findByParticipants_Id(curent_user.getId());
		}
	}

	public List<Project> findAllByUser(Long id) {
		return projRepo.findByParticipants_Id(id);
	}

	public Project activateForCurrentUser(String id) {
		Project project = projRepo.findByProjectId(id);
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
		if (account.getActive_project() == null) {
			// if there is not active project, force reload current logged user
			account = accSrv.findById(Utils.getCurrentAccount().getId());
			if (account.getActive_project() != null) {
				userSrv.signin(account);
			}
		}
		return projRepo.findById(account.getActive_project());
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
		return repoProject != null && canEdit(repoProject);
	}

	public boolean canEdit(Project project) {
		Account currentAccount = Utils.getCurrentAccount();
		return project.getAdministrators().contains(currentAccount)
				|| project.getParticipants().contains(currentAccount) || Roles.isAdmin();

	}

	public boolean canAdminister(Project project) {
		Account currentAccount = Utils.getCurrentAccount();
		return project.getAdministrators().contains(currentAccount) || Roles.isAdmin();

	}
}
