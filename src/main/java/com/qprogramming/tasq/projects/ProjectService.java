package com.qprogramming.tasq.projects;

import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.support.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

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

    public List<Account> getProjectAccounts(String id, String term) {
        Project project = findByProjectId(id);
        if (project == null) {
            try {
                Long projectID = Long.valueOf(id);
                project = findById(projectID);
            } catch (NumberFormatException e) {
                LOG.error(e.getMessage());
            }
        }
        if (project != null) {
            Set<Account> allParticipants = project.getParticipants();
            if (term == null) {
                return allParticipants.stream().collect(Collectors.toList());
            } else {
                return allParticipants.stream().filter(account -> StringUtils.containsIgnoreCase(account.toString(), term)).collect(Collectors.toList());
            }
        }
        return new LinkedList<>();
    }


    /**
     * Checks if currently logged in user have privileges to change anything in
     * project
     *
     * @param projectID
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
