package com.qprogramming.tasq.projects;

import com.google.common.annotations.VisibleForTesting;
import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.agile.StartStop;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.holiday.HolidayService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.*;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    public static final String APPLICATION_JSON = "application/json";
    public static final String REFERER = "Referer";
    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);
    private ProjectService projSrv;
    private AccountService accSrv;
    private TaskService taskSrv;
    private AgileService sprintSrv;
    private WorkLogService wrkLogSrv;
    private MessageSource msg;
    private EventsService eventsSrv;
    private HolidayService holidayService;
    private LastVisitedService visitedSrv;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public ProjectController(ProjectService projSrv, AccountService accSrv, TaskService taskSrv, AgileService sprintSrv,
                             WorkLogService wrklSrv, MessageSource msg, EventsService eventsSrv, HolidayService holidayService, LastVisitedService visitedSrv) {
        this.projSrv = projSrv;
        this.accSrv = accSrv;
        this.taskSrv = taskSrv;
        this.sprintSrv = sprintSrv;
        this.wrkLogSrv = wrklSrv;
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
        List<Task> tasks = project.getTasks().stream().filter(task -> !task.isSubtask()).collect(Collectors.toList());
        Map<TaskState, Integer> stateCount = new HashMap<>();
        for (TaskState state : TaskState.values()) {
            stateCount.put(state, 0);
        }
        for (Task task : tasks) {
            Integer value = stateCount.get(task.getState());
            value++;
            stateCount.put((TaskState) task.getState(), value);
        }
        model.addAttribute("TO_DO", stateCount.get(TaskState.TO_DO));
        model.addAttribute("ONGOING", stateCount.get(TaskState.ONGOING));
        model.addAttribute("COMPLETE", stateCount.get(TaskState.COMPLETE));
        model.addAttribute("CLOSED", stateCount.get(TaskState.CLOSED));
        model.addAttribute("BLOCKED", stateCount.get(TaskState.BLOCKED));
        List<Task> taskList;
        if (closed == null) {
            taskList = taskSrv.findByProjectAndOpen(project);
        } else {
            taskList = taskSrv.findAllByProject(project);
        }
        Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID, false));
        model.addAttribute("tasks", taskList);
        model.addAttribute("project", project);
        return "project/details";
    }

    @RequestMapping(value = "projectEvents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<DisplayWorkLog>> getProjectEvents(@RequestParam(value = "id") String id,
                                                                 @PageableDefault(size = 25, page = 0, sort = "time", direction = Direction.DESC) Pageable p) {
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            // NULL
            return null;
        }
        if (!project.getParticipants().contains(Utils.getCurrentAccount()) && !Roles.isAdmin()) {
            throw new TasqAuthException(msg, "role.error.project.permission");
        }
        // Fetch events
        Page<WorkLog> page = wrkLogSrv.findByProjectId(project.getId(), p);
        List<DisplayWorkLog> list = new LinkedList<DisplayWorkLog>();
        for (WorkLog workLog : page) {
            list.add(new DisplayWorkLog(workLog));
        }
        return ResponseEntity.ok(new PageImpl<>(list, p, page.getTotalElements()));
    }

    @RequestMapping(value = "/usersProjectsEvents", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<DisplayWorkLog>> getProjectsLogs(
            @PageableDefault(size = 25, page = 0, sort = "time", direction = Direction.DESC) Pageable p) {
        Account account = Utils.getCurrentAccount();
        List<Project> usersProjects = projSrv.findAllByUser(account.getId());
        if (!usersProjects.isEmpty()) {
            List<Long> ids = usersProjects.stream().map(Project::getId).collect(Collectors.toCollection(LinkedList::new));
            Page<WorkLog> page = wrkLogSrv.findByProjectIdIn(ids, p);
            List<DisplayWorkLog> list = page.getContent().stream().map(DisplayWorkLog::new).collect(Collectors.toList());
            return ResponseEntity.ok(new PageImpl<>(list, p, page.getTotalElements()));
        }
        return null;
    }

    @RequestMapping(value = "projects", method = RequestMethod.GET)
    public String listProjects(Model model) {
        List<Project> projects;
        if (Roles.isAdmin()) {
            projects = projSrv.findAll();
        } else {
            projects = projSrv.findAllByUser();
        }
        Collections.sort(projects, new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
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
        removeActive.stream().forEach(account -> account.setActiveProject(null));
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

    @RequestMapping(value = "/project/getParticipants", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<List<DisplayAccount>> listParticipants(@RequestParam String id, @RequestParam String term,
                                                          @RequestParam(required = false) boolean userOnly, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        List<Account> accounts = projSrv.getProjectAccounts(id, term);
        if (userOnly) {
            return ResponseEntity.ok(accounts.stream().filter(Account::getIsUser).map(DisplayAccount::new).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(accounts.stream().map(DisplayAccount::new).collect(Collectors.toList()));
    }


    @Transactional
    @RequestMapping(value = "/project/getChart", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ProjectChart> getProjectChart(@RequestParam String id,
                                                        @RequestParam(required = false) boolean all, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        Project project = projSrv.findByProjectId(id);
        Map<String, Integer> created = new HashMap<>();
        Map<String, Integer> closed = new HashMap<>();
        ProjectChart result = new ProjectChart();
        List<WorkLog> events = wrkLogSrv.findProjectCreateCloseEvents(project, all);
        // Fill maps
        if (events.size() > 0) {
            for (WorkLog workLog : events) {
                // Don't calculate for subtask ( not important )
                if (workLog.getTask() != null && !workLog.getTask().isSubtask()) {
                    LocalDate date = new LocalDate(workLog.getRawTime());
                    if (LogType.CREATE.equals(workLog.getType())) {
                        Integer value = created.get(date.toString());
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        created.put(date.toString(), value);
                    } else if (LogType.REOPEN.equals(workLog.getType())) {
                        Integer value = closed.get(date.toString());
                        if (value == null) {
                            value = 0;
                        }
                        value--;
                        closed.put(date.toString(), value);
                    } else {
                        Integer value = closed.get(date.toString());
                        if (value == null) {
                            value = 0;
                        }
                        value++;
                        closed.put(date.toString(), value);
                    }
                }
            }
            // Look for the first event ever (they are sorted)
            LocalDate start = new LocalDate(events.get(0).getRawTime());
            LocalDate end = new LocalDate().plusDays(1);
            LocalDate counter = start;
            Integer taskCreated = 0;
            Integer taskClosed = 0;
            LocalTime nearMidnight = new LocalTime(23, 59);
            DateTime startTime = start.toDateTime(new LocalTime(0, 0));
            DateTime endTime = end.toDateTime(nearMidnight);
            List<LocalDate> freeDays = projSrv.getFreeDays(project, startTime, endTime);
            result.setFreeDays(freeDays.stream()
                    .map(dateTime -> new StartStop(fmt.print(dateTime.minusDays(1).toDateTime(nearMidnight)), fmt.print(dateTime.toDateTime(nearMidnight))))
                    .collect(Collectors.toList()));
            while (counter.isBefore(end)) {
                Integer createValue = created.get(counter.toString());
                if (createValue == null) {
                    createValue = 0;
                }
                taskCreated += createValue;
                result.getCreated().put(counter.toString(), taskCreated);

                Integer closeValue = closed.get(counter.toString());
                if (closeValue == null) {
                    closeValue = 0;
                }
                taskClosed += closeValue;
                result.getClosed().put(counter.toString(), taskClosed);
                counter = counter.plusDays(1);
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Returns DisplayProject - minified version of project detials to get all
     * default values etc.
     *
     * @param id       id of project
     * @param response
     * @return
     */
    @RequestMapping(value = "/project/getDefaults", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<DisplayProject> getDefaults(@RequestParam String id, HttpServletResponse response) {
        response.setContentType(APPLICATION_JSON);
        Project project = projSrv.findByProjectId(id);
        if (project == null) {
            return ResponseEntity.badRequest().body(null);
        }
        DisplayProject result = new DisplayProject(project);
        Account account = accSrv.findById(project.getDefaultAssigneeID());
        if (account != null) {
            result.setDefaultAssignee(new DisplayAccount(account));
        }
        return ResponseEntity.ok(result);
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

    @VisibleForTesting
    void rollBack() {
        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
    }

}
