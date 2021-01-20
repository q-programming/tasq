package com.qprogramming.tasq.projects;

import com.google.common.annotations.VisibleForTesting;
import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.holiday.HolidayService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.*;
import com.qprogramming.tasq.task.worklog.LogType;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProjectController {


    public static final String REFERER = "Referer";
    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);
    private ProjectService projSrv;
    private AccountService accSrv;
    private TaskService taskSrv;
    private AgileService sprintSrv;
    private MessageSource msg;
    private EventsService eventsSrv;
    private HolidayService holidayService;
    private LastVisitedService visitedSrv;

    @Autowired
    public ProjectController(ProjectService projSrv, AccountService accSrv, TaskService taskSrv, AgileService sprintSrv,
                             MessageSource msg, EventsService eventsSrv, HolidayService holidayService, LastVisitedService visitedSrv) {
        this.projSrv = projSrv;
        this.accSrv = accSrv;
        this.taskSrv = taskSrv;
        this.sprintSrv = sprintSrv;
        this.msg = msg;
        this.eventsSrv = eventsSrv;
        this.holidayService = holidayService;
        this.visitedSrv = visitedSrv;
    }

    @Transactional
    @RequestMapping(value = "project/{id}", method = RequestMethod.GET)
    public String showDetails(@PathVariable String id, @RequestParam(value = "closed", required = false) String closed,
                              Model model, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (!project.getParticipants().contains(Utils.getCurrentAccount()) && !Roles.isAdmin()) {
            throw new TasqAuthException(msg, "role.error.project.permission");
        }
        Account account = Utils.getCurrentAccount();
        visitedSrv.addLastVisited(account.getId(), project);
        // Check status of all projects
        fillTaskByStatus(model, project);
        List<Task> taskList;
        if (closed == null) {
            taskList = taskSrv.findByProjectAndOpen(project);
        } else {
            taskList = taskSrv.findAllByProject(project);
        }
        taskList.sort(new TaskSorter(TaskSorter.SORTBY.ID, false));
        model.addAttribute("tasks", taskList);
        model.addAttribute("project", project);
        return "project/details";
    }

    @Transactional
    @RequestMapping(value = "project/{id}/statistics", method = RequestMethod.GET)
    public String showStats(@PathVariable String id, Model model, RedirectAttributes ra) {
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (!project.getParticipants().contains(Utils.getCurrentAccount()) && !Roles.isAdmin()) {
            throw new TasqAuthException(msg, "role.error.project.permission");
        }
        fillTaskByStatus(model, project);
        List<Project> projects = projSrv.findAllByUser();
        projects.sort(new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
                Utils.getCurrentAccount().getActiveProject(), false));
        model.addAttribute("projects", projects);
        model.addAttribute("project", project);
        return "project/statistics";
    }

    private void fillTaskByStatus(Model model, Project project) {
        Map<Enum<TaskState>, Long> stateCount = project.getTasks().stream().filter(task -> !task.isSubtask()).collect(Collectors.groupingBy(Task::getState, Collectors.counting()));
        model.addAttribute("TO_DO", stateCount.getOrDefault(TaskState.TO_DO, 0L));
        model.addAttribute("ONGOING", stateCount.getOrDefault(TaskState.ONGOING, 0L));
        model.addAttribute("COMPLETE", stateCount.getOrDefault(TaskState.COMPLETE, 0L));
        model.addAttribute("CLOSED", stateCount.getOrDefault(TaskState.CLOSED, 0L));
        model.addAttribute("BLOCKED", stateCount.getOrDefault(TaskState.BLOCKED, 0L));
    }


    @RequestMapping(value = "projects", method = RequestMethod.GET)
    public String listProjects(Model model) {
        List<Project> projects;
        if (Roles.isAdmin()) {
            projects = projSrv.findAll();
        } else {
            projects = projSrv.findAllByUser();
        }
        projects.sort(new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
                Utils.getCurrentAccount().getActiveProject(), true));
        model.addAttribute("projects", projects);
        return "project/list";
    }

    @RequestMapping(value = "project/activate/{id}", method = RequestMethod.GET)
    public String activate(@PathVariable(value = "id") String id, HttpServletRequest request, RedirectAttributes ra) {
        Project activatedProj = projSrv.activateForCurrentUser(id);
        if (activatedProj != null) {
            MessageHelper.addSuccessAttribute(ra, msg.getMessage("project.activated",
                    new Object[]{activatedProj.getName()}, Utils.getCurrentLocale()));
        }
        return "redirect:" + request.getHeader(REFERER);
    }

    @RequestMapping(value = "project/create", method = RequestMethod.GET)
    public NewProjectForm startProjectcreate() {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        return new NewProjectForm();
    }

    @RequestMapping(value = "project/create", method = RequestMethod.POST)
    public String createProject(@Valid @ModelAttribute("newProjectForm") NewProjectForm newProjectForm, Errors errors,
                                RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        if (newProjectForm.getProject_id().length() > 5) {
            errors.rejectValue("project_id", "project.idValid");
        }
        if (newProjectForm.getProject_id().matches(".*\\d.*")) {
            errors.rejectValue("project_id", "project.idValid.letters");
        }
        if (Utils.containsHTMLTags(newProjectForm.getName())) {
            errors.rejectValue("name", "error.name.html");
        }

        Utils.setHttpRequest(request);
        String name = newProjectForm.getName();
        if (null != projSrv.findByName(name)) {
            errors.rejectValue("name", "project.exists", new Object[]{name}, "");
        }
        String projectId = newProjectForm.getProject_id();
        if (null != projSrv.findByProjectId(projectId)) {
            errors.rejectValue("project_id", "project.idunique", new Object[]{projectId}, "");
        }
        if (errors.hasErrors()) {
            return null;
        }
        Project newProject = newProjectForm.createProject();
        newProject = projSrv.save(newProject);
        if (projSrv.findAllByUser().size() == 1) {
            Account account = Utils.getCurrentAccount();
            account.setActiveProject(newProject.getProjectId());
            accSrv.update(account);
        }
        // TODO Create first release if Kanban ?
        MessageHelper.addSuccessAttribute(ra,
                msg.getMessage("project.created", new Object[]{name}, Utils.getCurrentLocale()));
        return "redirect:/project/" + newProject.getProjectId();
    }

    @Transactional
    @RequestMapping(value = "project/{id}/manage", method = RequestMethod.GET)
    public String manageProject(@PathVariable(value = "id") String id, Model model, RedirectAttributes ra) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (project.getDefaultAssigneeID() != null) {
            DisplayAccount assignee = new DisplayAccount(accSrv.findById(project.getDefaultAssigneeID()));
            model.addAttribute("defaultAssignee", assignee);
        }
        Hibernate.initialize(project.getHolidays());
        model.addAttribute("project", project);
        return "project/manage";
    }

    @Transactional
    @RequestMapping(value = "project/{id}/delete", method = RequestMethod.POST)
    public String deleteProject(@PathVariable(value = "id") String id, @RequestParam(value = "projectId") String projectId, @RequestParam(value = "projectname") String name, RedirectAttributes ra, HttpServletRequest request) {
        Project project = projSrv.findByProjectId(id);
        ResultData result;
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (!Roles.isAdmin()) {
            throw new TasqAuthException(msg);
        }
        if (!projectId.equals(project.getProjectId()) || !name.equals(project.getName())) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.delete.confirm", null, Utils.getCurrentLocale()));
            return Utils.REDIRECT + request.getHeader(REFERER);
        }
        //add owners, assignees, participants, admins and active tasks ppl to list to notify about project removal
        List<Task> projectTasks = taskSrv.findAllByProject(project);
        Set<Account> notifyAccounts = new LinkedHashSet<>();
        notifyAccounts.addAll(projectTasks.stream().map(Task::getOwner).collect(Collectors.toSet()));
        notifyAccounts.addAll(projectTasks.stream().map(Task::getAssignee).collect(Collectors.toSet()));
        //remove active project from project participants
        Set<Account> participantsAndAdmins = updateActiveProjects(project);
        notifyAccounts.addAll(participantsAndAdmins);
        for (Task task : projectTasks) {
            notifyAccounts.addAll(accSrv.findAllWithActiveTask(task.getId()));
            result = taskSrv.deleteTask(task, true);
            if (result.code.equals(ResultData.Code.ERROR)) {
                MessageHelper.addErrorAttribute(ra, result.message, Utils.getCurrentLocale());
                rollBack();
                return Utils.REDIRECT + request.getHeader(REFERER);
            }
        }
        notifyAccounts.remove(null);
        sendNotificationsAfterRemove(project, notifyAccounts);
        MessageHelper.addSuccessAttribute(ra, msg.getMessage("project.delete.success", new Object[]{project.toString()}, Utils.getCurrentLocale()), Utils.getCurrentLocale());
        visitedSrv.delete(project);
        projSrv.delete(project);
        return "redirect:/projects";
    }

    private void sendNotificationsAfterRemove(Project project, Set<Account> notifyAccounts) {
        Map<String, String[]> localeMap = new HashMap<>();
        for (Account account : notifyAccounts) {
            Locale locale = new Locale(account.getLanguage());
            String moreDetails;
            String message;
            if (!localeMap.containsKey(account.getLanguage())) {
                moreDetails = msg.getMessage("project.delete.event", new Object[]{Utils.getCurrentAccount(), project.toString()}, locale);
                message = msg.getMessage(LogType.PROJ_REMOVE.getCode(), null, locale);
                localeMap.put(account.getLanguage(), new String[]{message, moreDetails});
            }
            if (!account.equals(Utils.getCurrentAccount())) {
                eventsSrv.addSystemEvent(account, LogType.PROJ_REMOVE, localeMap.get(account.getLanguage())[0], localeMap.get(account.getLanguage())[1]);
            }
        }
    }

    private Set<Account> updateActiveProjects(Project project) {
        Set<Account> participantsAndAdmins = new HashSet<>();
        participantsAndAdmins.addAll(project.getParticipants());
        participantsAndAdmins.addAll(project.getAdministrators());
        Set<Account> removeActive = participantsAndAdmins.stream().filter(account -> project.getProjectId().equals(account.getActiveProject())).collect(Collectors.toSet());
        removeActive.forEach(account -> account.setActiveProject(null));
        accSrv.update(new ArrayList<>(removeActive));
        return participantsAndAdmins;
    }

    @RequestMapping(value = "project/useradd", method = RequestMethod.POST)
    public String addParticipant(@RequestParam(value = "id") String id, @RequestParam(value = "email") String email,
                                 RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Account account = accSrv.findByEmail(email);
        if (account != null) {
            Project project = projSrv.findByProjectId(id);
            if (project == null) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
                return "redirect:/projects";
            }
            project.addParticipant(account);
            if (StringUtils.isEmpty(account.getActiveProject())) {
                account.setActiveProject(project.getProjectId());
                accSrv.update(account);
            }
            eventsSrv.addProjectEvent(account, LogType.ASSIGN_TO_PROJ, project);
            projSrv.save(project);
        }
        return "redirect:" + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "project/{id}/workdays", method = RequestMethod.POST)
    public String saveWorkdays(@PathVariable(value = "id") String id,
                               @RequestParam(value = "workingWeekends", required = false, defaultValue = "false") boolean workingWeekends,
                               @RequestParam(value = "holiday", required = false) Set<String> holidays,
                               RedirectAttributes ra, HttpServletRequest request) {
        Set<String> holidaysSet = holidays == null ? new HashSet<>() : holidays;
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        project.setWorkingWeekends(workingWeekends);
        Hibernate.initialize(project.getHolidays());
        project = holidayService.processProjectHolidays(holidaysSet, project);
        projSrv.save(project);
        return "redirect:" + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "project/userRemove", method = RequestMethod.POST)
    public String removeParticipant(@RequestParam(value = "project_id") String projectId,
                                    @RequestParam(value = "account_id") Long accountId, RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Account account = accSrv.findById(accountId);
        if (account != null) {
            Project project = projSrv.findByProjectId(projectId);
            if (project == null) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
                return "redirect:" + request.getHeader(REFERER);
            }
            Set<Account> admins = project.getAdministrators();
            if (admins.contains(account)) {
                if (admins.size() == 1) {
                    MessageHelper.addErrorAttribute(ra,
                            msg.getMessage("project.lastAdmin", null, Utils.getCurrentLocale()));
                    return "redirect:" + request.getHeader(REFERER);
                } else {
                    project.removeAdministrator(account);
                }

            }
            project.removeParticipant(account);
            eventsSrv.addProjectEvent(account, LogType.REMOVE_FROM_PROJ, project);
            projSrv.save(project);
        }
        return "redirect:" + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "project/grantAdmin", method = RequestMethod.POST)
    public String grantAdmin(@RequestParam(value = "project_id") String projectId,
                             @RequestParam(value = "account_id") Long accountId, RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Account account = accSrv.findById(accountId);
        if (account != null) {
            Project project = projSrv.findByProjectId(projectId);
            if (project == null) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
                return "redirect:/projects";
            }
            project.addAdministrator(account);
            projSrv.save(project);
        }
        return "redirect:" + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "project/removeAdmin", method = RequestMethod.POST)
    public String removeAdmin(@RequestParam(value = "project_id") String projectId,
                              @RequestParam(value = "account_id") Long accountId, RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Account account = accSrv.findById(accountId);
        if (account != null) {
            Project project = projSrv.findByProjectId(projectId);
            if (project == null) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
                return "redirect:/projects";
            }
            if (project.getAdministrators().size() == 1) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage("project.lastAdmin", null, Utils.getCurrentLocale()));
                return "redirect:" + request.getHeader(REFERER);
            }
            project.removeAdministrator(account);
            projSrv.save(project);
        }
        return "redirect:" + request.getHeader(REFERER);
    }


    @Transactional
    @RequestMapping(value = "project/{id}/update", method = RequestMethod.POST)
    public String updateProperties(@PathVariable String id,
                                   @RequestParam(value = "default_priority") TaskPriority priority,
                                   @RequestParam(value = "default_type") TaskType type,
                                   @RequestParam(value = "defaultAssignee") Long assigneId, RedirectAttributes ra,
                                   HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (priority != null) {
            project.setDefault_priority(priority);
        }
        project.setDefault_type(type);
        Account account = accSrv.findById(assigneId);
        assigneId = account != null ? account.getId() : null;
        project.setDefaultAssigneeID(assigneId);
        projSrv.save(project);
        Sprint activeSprint = sprintSrv.findByProjectIdAndActiveTrue(project.getId());
        return "redirect:" + request.getHeader(REFERER);
    }

    @RequestMapping(value = "project/{id}/editDescriptions", method = RequestMethod.POST)
    public String editDescriptions(@PathVariable String id, @RequestParam(value = "name") String name,
                                   @RequestParam(value = "description") String description, RedirectAttributes ra,
                                   HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (Utils.containsHTMLTags(name)) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.name.html", null, Utils.getCurrentLocale()));
            return "redirect:" + request.getHeader(REFERER);
        }

        if (!projSrv.canEdit(project.getId())) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.accesRights", null, Utils.getCurrentLocale()));
            return "redirect:" + request.getHeader(REFERER);
        }
        if (description != null) {
            project.setDescription(description);
        }
        if (name != null) {
            project.setName(name);
            visitedSrv.updateName(project);
        }

        projSrv.save(project);
        return "redirect:" + request.getHeader(REFERER);
    }

    @RequestMapping(value = "project/{id}/git", method = RequestMethod.POST)
    public String editGIT(@PathVariable String id, @RequestParam(value = "git") String git,
                          RedirectAttributes ra, HttpServletRequest request) {
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("project.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/projects";
        }
        if (!projSrv.canEdit(project.getId())) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.accesRights", null, Utils.getCurrentLocale()));
            return "redirect:" + request.getHeader(REFERER);
        }
        if (!StringUtils.isEmpty(git)) {
            project.setGit(git);
            projSrv.save(project);
        }
        return "redirect:" + request.getHeader(REFERER);
    }

    @VisibleForTesting
    void rollBack() {
        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
    }

}
