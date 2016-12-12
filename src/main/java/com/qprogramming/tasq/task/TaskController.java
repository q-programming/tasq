/**
 *
 */
package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.error.TasqException;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.comments.CommentService;
import com.qprogramming.tasq.task.link.TaskLink;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.link.TaskLinkType;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.tag.TagsRepository;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.TaskResolution;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.qprogramming.tasq.task.TaskForm.*;

/**
 * @author romanjak
 * @date 26 maj 2014
 */
@Controller
public class TaskController {

    private static final String TYPE_TXT = "Type";
    private static final String ESTIMATED_TXT = "Estimated ";
    private static final String REMAINING_TXT = "Remaining";
    private static final String ESTIMATE_TXT = "Estimate";
    private static final String DESCRIPTION_TXT = "Description";
    private static final String NAME_TXT = "Name";
    private static final String STORY_POINTS_TXT = "Story points";

    private static final String UNASSIGNED = "<i>Unassigned</i>";
    private static final String OPEN = "OPEN";
    private static final String ALL = "ALL";
    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);
    private static final String CHANGE_TO = " -> ";
    private static final String BR = "<br>";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String CANCEL = "cancel";
    private static final String REDIRECT_TASK = "redirect:/task/";
    private static final String REDIRECT = "redirect:";
    private static final String ERROR_ACCES_RIGHTS = "error.accesRights";
    private static final String REFERER = "Referer";
    private static final String ERROR_NAME_HTML = "error.name.html";
    @PersistenceContext
    private EntityManager entityManager;
    private TaskService taskSrv;
    private ProjectService projectSrv;
    private AccountService accSrv;
    private WorkLogService wlSrv;
    private MessageSource msg;
    private AgileService sprintSrv;
    private TaskLinkService linkService;
    private WatchedTaskService watchSrv;
    private CommentService commSrv;
    private TagsRepository tagsRepo;
    private EventsService eventSrv;
    private LastVisitedService visitedSrv;

    @Autowired
    public TaskController(TaskService taskSrv, ProjectService projectSrv, AccountService accSrv, WorkLogService wlSrv,
                          MessageSource msg, AgileService sprintSrv, TaskLinkService linkService, CommentService commSrv,
                          TagsRepository tagsRepo, WatchedTaskService watchSrv, EventsService eventSrv, LastVisitedService visitedSrv) {
        this.taskSrv = taskSrv;
        this.projectSrv = projectSrv;
        this.accSrv = accSrv;
        this.wlSrv = wlSrv;
        this.msg = msg;
        this.sprintSrv = sprintSrv;
        this.linkService = linkService;
        this.commSrv = commSrv;
        this.tagsRepo = tagsRepo;
        this.watchSrv = watchSrv;
        this.eventSrv = eventSrv;
        this.visitedSrv = visitedSrv;
    }

    @RequestMapping(value = "task/create", method = RequestMethod.GET)
    public TaskForm startTaskCreate(Model model) {
        fillCreateTaskModel(model);
        return new TaskForm();
    }

    @Transactional
    @RequestMapping(value = "task/create", method = RequestMethod.POST)
    public String createTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult errors,
                             @RequestParam(value = "linked", required = false) String linked, RedirectAttributes ra,
                             HttpServletRequest request, Model model) {
        if (!Roles.isUser()) {
            throw new TasqAuthException(msg);
        }
        if (Utils.containsHTMLTags(taskForm.getName())) {
            errors.rejectValue(NAME, ERROR_NAME_HTML);
        }
        checkEstimatesValues(taskForm, errors);
        int storyPoints = getStoryPoints(taskForm);
        if (!StringUtils.isNumeric(taskForm.getStory_points()) || !Utils.validStoryPoint(storyPoints)) {
            errors.rejectValue(STORY_POINTS, "task.storyPoints.invalid");
        }
        if (errors.hasErrors()) {
            fillCreateTaskModel(model);
            return null;
        }
        Project project = projectSrv.findByProjectId(taskForm.getProject());
        if (project != null) {
            // check if can edit
            Task task;
            task = taskForm.createTask();
            if (!projectSrv.canEdit(project)) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader(REFERER);
            }
            // build ID
            long taskCount = project.getLastTaskNo();
            taskCount++;
            String taskID = project.getProjectId() + "-" + taskCount;
            task.setId(taskID);
            task.setProject(project);
            task.setTaskOrder(taskCount);
            project.getTasks().add(task);
            project.setLastTaskNo(taskCount);
            // assigne
            setCreatedTaskAssignee(taskForm, task);
            task = taskSrv.save(task);//save before adding rest
            // lookup for sprint
            // Create log work
            if (taskForm.getAddToSprint() != null) {
                Sprint sprint = sprintSrv.findByProjectIdAndSprintNo(project.getId(), taskForm.getAddToSprint());
                task.addSprint(sprint);
                // increase scope
                if (sprint.isActive()) {
                    if (checkIfNotEstimated(task, project)) {
                        errors.rejectValue("addToSprint", "agile.task2Sprint.Notestimated",
                                new Object[]{"", sprint.getSprintNo()},
                                "Unable to add not estimated task to active sprint");
                        fillCreateTaskModel(model);
                        return null;
                    }
                    String message = "";
                    if (task.isEstimated() && project.getTimeTracked()) {
                        message = task.getEstimate();
                    }
                    wlSrv.addActivityLog(task, message, LogType.TASKSPRINTADD);
                }
            }
            task = taskSrv.save(task);
            projectSrv.save(project);
            // Save files
            saveTaskFiles(taskForm.getFiles(), task);
            wlSrv.addActivityLog(task, "", LogType.CREATE);
            watchSrv.startWatching(task);
            if (StringUtils.isNotBlank(linked)) {
                Task linkedTask = taskSrv.findById(linked);
                if (linkedTask != null) {
                    if (linkedTask.getProject().equals(project)) {
                        TaskLink link = new TaskLink(linkedTask.getId(), taskID, TaskLinkType.RELATES_TO);
                        linkService.save(link);
                        wlSrv.addWorkLogNoTask(linked + " - " + taskID, project, LogType.TASK_LINK);
                    } else {
                        MessageHelper.addWarningAttribute(ra,
                                msg.getMessage("task.create.not.linked", null, Utils.getCurrentLocale()));

                    }
                }
            }
            //everything went well , save task
            taskSrv.save(task);
            MessageHelper.addSuccessAttribute(ra,
                    msg.getMessage("task.create.success", new Object[]{taskID}, Utils.getCurrentLocale()));
            return REDIRECT_TASK + taskID;
        }
        return null;
    }

    private void checkEstimatesValues(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult result) {
        if (StringUtils.isNotBlank(taskForm.getEstimate())) {
            if (!Utils.correctEstimate(taskForm.getEstimate())) {
                result.rejectValue("estimate", "error.estimateFormat");
            } else if (PeriodHelper.inFormat(taskForm.getEstimate()).getDays() > 28) {
                result.rejectValue("estimate", "error.estimateTooLong");
            }
        }
    }

    private void setCreatedTaskAssignee(TaskForm taskForm, Task task) {
        if (StringUtils.isNotBlank(taskForm.getAssignee())) {
            Account assignee = accSrv.findByEmail(taskForm.getAssignee());
            if (assignee != null) {
                task.setAssignee(assignee);
                watchSrv.addToWatchers(task, assignee);
            }
        }
    }

    @RequestMapping(value = "/task/{id}/edit", method = RequestMethod.GET)
    public TaskForm startEditTask(@PathVariable("id") String id, Model model) {
        Task task = taskSrv.findById(id);
        if (projectSrv.canEdit(task.getProject())
                && (Roles.isUser() | task.getOwner().equals(Utils.getCurrentAccount()))) {
            fillModelForEdit(model, task);
            return new TaskForm(task);
        } else {
            throw new TasqAuthException(msg);
        }
    }

    private void fillModelForEdit(Model model, Task task) {
        model.addAttribute("task", task);
        model.addAttribute("project", task.getProject());
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/{subid}/edit", method = RequestMethod.GET)
    public TaskForm startEditSubTask(@PathVariable("id") String id, @PathVariable("subid") String subid, Model model) {
        return startEditTask(taskSrv.createSubId(id, subid), model);
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/{subid}/edit", method = RequestMethod.POST)
    public String editSubTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult errors,
                              RedirectAttributes ra, HttpServletRequest request, Model model) {
        return editTask(taskForm, errors, ra, request, model);
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/edit", method = RequestMethod.POST)
    public String editTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult errors, RedirectAttributes ra,
                           HttpServletRequest request, Model model) {
        String taskID = taskForm.getId();
        Task task = taskSrv.findById(taskID);
        if (task == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.task.notfound", null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (Utils.containsHTMLTags(taskForm.getName())) {
            errors.rejectValue(NAME, ERROR_NAME_HTML);
        }
        checkEstimatesValues(taskForm, errors);
        if (StringUtils.isNotBlank(taskForm.getRemaining()) && !Utils.correctEstimate(taskForm.getRemaining())) {
            errors.rejectValue(REMAINING, "error.estimateFormat");
        }
        int storyPoints = getStoryPoints(taskForm);
        if (!task.isSubtask() && !StringUtils.isNumeric(taskForm.getStory_points()) || !Utils.validStoryPoint(storyPoints)) {
            errors.rejectValue(STORY_POINTS, "task.storyPoints.invalid");
        }
        if (errors.hasErrors()) {
            fillModelForEdit(model, task);
            return null;
        }
        // check if can edit
        if (!projectSrv.canEdit(task.getProject())
                && (!Roles.isUser() | !task.getOwner().equals(Utils.getCurrentAccount()))) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (task.getState().equals(TaskState.CLOSED)) {
            ResultData result = taskIsClosed(task);
            MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
            return REDIRECT + request.getHeader(REFERER);
        }
        StringBuilder message = new StringBuilder(Utils.TABLE);
        if (nameChanged(taskForm.getName(), task)) {
            message.append(Utils.changedFromTo(NAME_TXT, task.getName(), taskForm.getName()));
            task.setName(taskForm.getName());
            updateWatched(task);
            visitedSrv.updateName(task);
        }
        if (!task.getDescription().equals(taskForm.getDescription())) {
            message.append(Utils.changedFromTo(DESCRIPTION_TXT, task.getDescription(), taskForm.getDescription()));
            task.setDescription(taskForm.getDescription());
        }
        if ((taskForm.getEstimate() != null) && (!task.getEstimate().equalsIgnoreCase(taskForm.getEstimate()))) {
            Period estimate = PeriodHelper.inFormat(taskForm.getEstimate());
            Period difference = PeriodHelper.minusPeriods(estimate, task.getRawEstimate());
            // only add estimate change event if task is in sprint
            if (sprintSrv.taskInActiveSprint(task)) {
                wlSrv.addActivityPeriodLog(task, PeriodHelper.outFormat(difference), difference, LogType.ESTIMATE);
            } else {
                message.append(Utils.changedFromTo(ESTIMATE_TXT, task.getEstimate(), taskForm.getEstimate()));
            }
            task.setEstimate(estimate);
            task.setRemaining(estimate);
        }
        if ((taskForm.getRemaining() != null) && (!task.getRemaining().equalsIgnoreCase(taskForm.getRemaining()))) {
            Period remaining = PeriodHelper.inFormat(taskForm.getRemaining());
            message.append(Utils.changedFromTo(REMAINING_TXT, task.getRemaining(), taskForm.getRemaining()));
            task.setRemaining(remaining);
        }

        boolean estimated = !taskForm.getNotEstimated();
        if (!task.isEstimated().equals(estimated) && !task.isInSprint()) {
            message.append(
                    Utils.changedFromTo(ESTIMATED_TXT, task.getEstimated().toString(), Boolean.toString(estimated)));
            task.setEstimated(estimated);
            if (!task.isEstimated()) {
                task.setStory_points(0);
            }
        }
        // Don't check for SP if task is not estimated
        if (task.isEstimated()) {
            try {
                if (task.getStory_points() != null && task.getStory_points() != storyPoints) {
                    if (shouldAddWorklogPointsChanged(task, storyPoints)) {
                        message.append(Utils.changedFromTo(STORY_POINTS_TXT, task.getStory_points().toString(),
                                Integer.toString(storyPoints)));
                    }
                    task.setStory_points(storyPoints);
                }
            } catch (NumberFormatException e) {
                throw new TasqException("Please use only full numbers");
            }
        }
        if (!task.getDue_date().equalsIgnoreCase(taskForm.getDue_date())) {
            message.append(Utils.changedFromTo("Due date", task.getDue_date(), taskForm.getDue_date()));
            task.setDue_date(Utils.convertStringToDate(taskForm.getDue_date()));
        }
        TaskType type = TaskType.toType(taskForm.getType());
        if (!task.getType().equals(type)) {
            message.append(Utils.changedFromTo(TYPE_TXT, task.getType().toString(), type.toString()));
            task.setType(type);
            updateWatched(task);
        }
        LOG.debug(message.toString());
        message.append(Utils.TABLE_END);
        if (message.length() > 37) {
            wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);
        }
        taskSrv.save(task);
        return REDIRECT_TASK + taskID;
    }

    private int getStoryPoints(TaskForm taskForm) {
        return (StringUtils.isNotBlank(taskForm.getStory_points()) && StringUtils.isNumeric(taskForm.getStory_points())) ? Integer.parseInt(taskForm.getStory_points())
                : 0;
    }

    private boolean nameChanged(String newName, Task task) {
        return !task.getName().equals(newName);
    }

    private void updateWatched(Task task) {
        WatchedTask watched = watchSrv.getByTask(task);
        if (watched != null) {
            watched.setName(task.getName());
            watched.setType((TaskType) task.getType());
        }

    }

    @Transactional
    @RequestMapping(value = "task/{id}/{subId}", method = RequestMethod.GET)
    public String showSubTaskDetails(@PathVariable(value = "id") String id, @PathVariable(value = "subId") String subId,
                                     Model model, RedirectAttributes ra) {
        return showTaskDetails(taskSrv.createSubId(id, subId), model, ra);
    }

    @Transactional
    @RequestMapping(value = "task/{id}", method = RequestMethod.GET)
    public String showTaskDetails(@PathVariable String id, Model model, RedirectAttributes ra) {
        Task task = taskSrv.findById(id);
        if (task == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("task.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/tasks";
        }
        getEntitymanager().detach(task);
        Account account = Utils.getCurrentAccount();
        visitedSrv.addLastVisited(account.getId(), task);
        // TASK
        Set<Comment> comments = commSrv.findByTaskIdOrderByDateDesc(id);
        Map<TaskLinkType, List<String>> links = linkService.findTaskLinks(id);
        Map<TaskLinkType, List<Task>> taskLinks = new HashMap<>();
        for (Map.Entry<TaskLinkType, List<String>> taskLink : links.entrySet()) {
            taskLinks.put(taskLink.getKey(), taskLink.getValue().stream().map(s -> taskSrv.findById(s)).collect(Collectors.toList()));
        }
        if (!task.isSubtask()) {
            List<Task> subtasks = taskSrv.findSubtasks(task);
            // Add all subtasks into remaining work
            if (!subtasks.isEmpty()) {
                Period parentEstimate = task.getRawEstimate();
                Period parentLoggedWork = task.getRawLoggedWork();
                Period parentRemaining = task.getRawRemaining();
                for (Task subtask : subtasks) {
                    task.setEstimate(PeriodHelper.plusPeriods(task.getRawEstimate(), subtask.getRawEstimate()));
                    task.setLoggedWork(PeriodHelper.plusPeriods(task.getRawLoggedWork(), subtask.getRawLoggedWork()));
                    task.setRemaining(PeriodHelper.plusPeriods(task.getRawRemaining(), subtask.getRawRemaining()));
                }
                Collections.sort(subtasks, new TaskSorter(TaskSorter.SORTBY.ID, true));
                model.addAttribute("taskEstimate", PeriodHelper.outFormat(parentEstimate));
                model.addAttribute("subtasksEstimate", PeriodHelper.outFormat(PeriodHelper.minusPeriods(task.getRawEstimate(), parentEstimate)));
                model.addAttribute("taskLogged", PeriodHelper.outFormat(parentLoggedWork));
                model.addAttribute("subtasksLogged", PeriodHelper.outFormat(PeriodHelper.minusPeriods(task.getRawLoggedWork(), parentLoggedWork)));
                model.addAttribute("taskRemaining", PeriodHelper.outFormat(parentRemaining));
                model.addAttribute("subtasksRemaining", PeriodHelper.outFormat(PeriodHelper.minusPeriods(task.getRawRemaining(), parentRemaining)));
            }
            model.addAttribute("subtasks", subtasks);
        }
        model.addAttribute("watching", watchSrv.isWatching(task.getId()));
        model.addAttribute("comments", comments);
        model.addAttribute("task", task);
        model.addAttribute("links", taskLinks);
        model.addAttribute("files", getTaskFiles(task));
        return "task/details";
    }

    @Transactional
    @RequestMapping(value = "tasks", method = RequestMethod.GET)
    public String listTasks(@RequestParam(value = "projectID", required = false) String projId,
                            @RequestParam(value = "state", required = false) String state,
                            @RequestParam(value = "query", required = false) String query,
                            @RequestParam(value = "priority", required = false) String priority,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "assignee", required = false) String assignee, Model model) {
        if (StringUtils.isEmpty(state)) {
            if (query != null) {
                state = ALL;
            } else {
                state = OPEN;
            }
        }
        Account currentAccount = Utils.getCurrentAccount();
        List<Project> projects = projectSrv.findAllByUser();
        Collections.sort(projects, new ProjectSorter(ProjectSorter.SORTBY.LAST_VISIT,
                currentAccount.getActiveProject(), true));
        Account assigneeAccount = null;
        if (StringUtils.isNotEmpty(assignee)) {
            assigneeAccount = accSrv.findByUsername(assignee);
            if (assigneeAccount != null) {
                model.addAttribute("assignee", assigneeAccount);
            }
        }
        model.addAttribute("projects", projects);
        // Get active or choosen project
        Optional<Project> projectObj;
        if (projId == null) {
            projectObj = projects.stream().filter(p -> p.getProjectId().equals(currentAccount.getActiveProject())).findFirst();
        } else {
            projectObj = projects.stream().filter(p -> p.getProjectId().equals(projId)).findFirst();
        }
        if (projectObj.isPresent()) {
            Project project = projectObj.get();
            TaskFilter filter = new TaskFilter(project, state, query, priority, type, assigneeAccount);
            List<Task> tasks = taskSrv.findBySpecification(filter);
            if (StringUtils.isNotEmpty(query)) {
                Tag tag = tagsRepo.findByName(query);
                List<Task> searchResult = tasks.stream().filter(task -> StringUtils.containsIgnoreCase(task.getId(), query)
                        || StringUtils.containsIgnoreCase(task.getName(), query)
                        || StringUtils.containsIgnoreCase(task.getDescription(), query)
                        || task.getTags().contains(tag)).collect(Collectors.toCollection(LinkedList::new));
                tasks = searchResult;
            }
            Collections.sort(tasks, new TaskSorter(TaskSorter.SORTBY.ID, false));
            model.addAttribute("tasks", tasks);
            model.addAttribute("active_project", project);
        }
        return "task/list";
    }

    @RequestMapping(value = "task/{id}/subtask", method = RequestMethod.GET)
    public TaskForm startSubTaskCreate(@PathVariable String id, Model model) {
        Task task = taskSrv.findById(id);
        if (task != null) {
            model.addAttribute("project", task.getProject());
            model.addAttribute("task", task);
            return new TaskForm();
        }
        return null;
    }

    @Transactional
    @RequestMapping(value = "task/{id}/subtask", method = RequestMethod.POST)
    public String createSubTask(@PathVariable String id, @Valid @ModelAttribute("taskForm") TaskForm taskForm,
                                Errors errors, RedirectAttributes ra, HttpServletRequest request, Model model) {
        if (!Roles.isUser()) {
            throw new TasqAuthException(msg);
        }
        Task task = taskSrv.findById(id);
        if (task != null) {
            if (task.getState().equals(TaskState.CLOSED)) {
                ResultData result = taskIsClosed(task);
                MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                return REDIRECT + request.getHeader(REFERER);
            }
            Project project = projectSrv.findByProjectId(taskForm.getProject());
            if (Utils.containsHTMLTags(taskForm.getName())) {
                errors.rejectValue(NAME, ERROR_NAME_HTML);
            }
            if (errors.hasErrors()) {
                model.addAttribute("project", project);
                model.addAttribute("task", task);
                return null;
            }
            if (!projectSrv.canEdit(project)) {
                MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader(REFERER);
            }
            Task subTask = taskForm.createSubTask();
            // assigne
            if (StringUtils.isNotBlank(taskForm.getAssignee())) {
                Account assignee = accSrv.findByEmail(taskForm.getAssignee());
                subTask.setAssignee(assignee);
            }
            subTask = taskSrv.createSubTask(project, task, subTask);
            wlSrv.addActivityLog(subTask, "", LogType.SUBTASK);
            taskSrv.save(subTask);
            return REDIRECT_TASK + id;
        }
        MessageHelper.addErrorAttribute(ra, msg.getMessage("task.notexists", null, Utils.getCurrentLocale()));
        return REDIRECT + request.getHeader(REFERER);
    }

    /**
     * Logs work . If only digits are sent , it's pressumed that those were
     * hours
     *
     * @param taskID     - ID of task for which work is logged
     * @param loggedWork - amount of time spent
     * @param ra
     * @param request
     * @return
     */
    @Transactional
    @RequestMapping(value = "logwork", method = RequestMethod.POST)
    public String logWork(@RequestParam(value = "taskID") String taskID,
                          @RequestParam(value = "loggedWork") String loggedWork,
                          @RequestParam(value = REMAINING, required = false) String remainingTxt,
                          @RequestParam("date_logged") String dateLogged, @RequestParam("time_logged") String timeLogged,
                          RedirectAttributes ra, HttpServletRequest request) {
        loggedWork = Utils.matchTimeFormat(loggedWork);
        remainingTxt = Utils.matchTimeFormat(remainingTxt);
        if ((StringUtils.isNotBlank(loggedWork) && !Utils.correctEstimate(loggedWork))
                || (StringUtils.isNotBlank(remainingTxt) && !Utils.correctEstimate(remainingTxt))) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("error.estimateFormat", null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        Period logged = PeriodHelper.inFormat(loggedWork);
        if (!validLoggedWork(logged)) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("error.logged.minmax", null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (StringUtils.isNotBlank(remainingTxt) && PeriodHelper.inFormat(remainingTxt).getDays() > 28) {
            MessageHelper.addErrorAttribute(ra,
                    msg.getMessage("error.estimateTooLong", null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            if (task.getState().equals(TaskState.CLOSED)) {
                ResultData result = taskIsClosed(task);
                MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                return REDIRECT + request.getHeader(REFERER);
            }
            // check if can edit
            if (Roles.isPowerUser() | projectSrv.canEdit(task.getProject())) {

                StringBuilder message = new StringBuilder(loggedWork);
                Date when = new Date();
                if (StringUtils.isNotEmpty(dateLogged) && StringUtils.isNotEmpty(timeLogged)) {
                    when = Utils.convertStringToDateAndTime(dateLogged + " " + timeLogged);
                    message.append(BR);
                    message.append("Date: ");
                    message.append(dateLogged);
                    message.append(" ");
                    message.append(timeLogged);

                }
                Period remaining = null;
                if (StringUtils.isNotEmpty(remainingTxt)) {
                    remaining = PeriodHelper.inFormat(remainingTxt);
                    task = wlSrv.addDatedWorkLog(task, remainingTxt, when, LogType.ESTIMATE);
                }
                task = wlSrv.addTimedWorkLog(task, message.toString(), when, remaining, logged, LogType.LOG);
                task = taskSrv.checkStateAndSave(task);
                if (!totalLoggedTimeValid(logged, task)) {
                    MessageHelper.addWarningAttribute(ra, msg.getMessage("task.logWork.logged.tooMuch",
                            new Object[]{loggedWork, task.getId()}, Utils.getCurrentLocale()));
                } else {
                    MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.logWork.logged",
                            new Object[]{loggedWork, task.getId()}, Utils.getCurrentLocale()));
                }
            } else {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader(REFERER);
            }
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    /**
     * Check if total logged time is not longer than 4 weeks which is total sprint durration
     *
     * @param logged
     * @param task
     * @return
     */
    private boolean totalLoggedTimeValid(Period logged, Task task) {
        Period loggedWork = task.getRawLoggedWork();
        Period totalWork = PeriodHelper.plusPeriods(loggedWork, logged);
        return totalWork.getDays() <= 28;
    }

    private boolean validLoggedWork(Period logged) {
        Duration min = PeriodHelper.toStandardDuration(new Period().withMinutes(1));
        Duration duration = PeriodHelper.toStandardDuration(logged);
        return duration.isLongerThan(min) && logged.getDays() <= 5;
    }


    @Transactional
    @RequestMapping(value = "/task/changeState", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResultData> changeState(@RequestParam(value = "id") String taskID,
                                                  @RequestParam(value = "state") TaskState state,
                                                  @RequestParam(value = "zero_checkbox", required = false) Boolean remainingZero,
                                                  @RequestParam(value = "closesubtasks", required = false) Boolean closeSubtasks,
                                                  @RequestParam(value = "message", required = false) String commentMessage,
                                                  @RequestParam(value = "resolution", required = false) TaskResolution resolution) {
        // check if not admin or user
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            if (state.equals(task.getState())) {
                String stateText = msg.getMessage(state.getCode(), null, Utils.getCurrentLocale());
                return ResponseEntity.ok(new ResultData(ResultData.WARNING, msg.getMessage("task.already.inState", new Object[]{stateText}, Utils.getCurrentLocale())));
            }
            // check if can edit
            if ((Utils.getCurrentAccount().equals(task.getOwner())
                    || Utils.getCurrentAccount().equals(task.getAssignee()))
                    || (Roles.isPowerUser() | projectSrv.canEdit(task.getProject()))) {
                // check if reopening kanban
                if (task.getState().equals(TaskState.CLOSED)
                        && Project.AgileType.KANBAN.equals(task.getProject().getAgile()) && task.getRelease() != null) {
                    return ResponseEntity.ok(new ResultData(ResultData.ERROR, msg.getMessage("task.changeState.change.kanbanRelease",
                            new Object[]{task.getRelease().getRelease()}, Utils.getCurrentLocale())));
                }
                if (TaskState.TO_DO.equals(state)) {
                    Hibernate.initialize(task.getLoggedWork());
                    if (!("0m").equals(task.getLoggedWork())) {
                        return ResponseEntity.ok(new ResultData(ResultData.ERROR,
                                msg.getMessage("task.alreadyStarted", new Object[]{taskID}, Utils.getCurrentLocale())));
                    }
                } else if (TaskState.CLOSED.equals(state)) {
                    ResultData result = taskSrv.checkTaskCanOperated(task, false);
                    if (result.code.equals(ResultData.ERROR)) {
                        return ResponseEntity.ok(result);
                    } else {
                        stopTimer(task);
                    }
                }
                if (closeSubtasks != null && closeSubtasks) {
                    List<Task> subtasks = taskSrv.findSubtasks(task);
                    subtasks.stream().filter(subtask -> !TaskState.CLOSED.equals(subtask.getState())).forEach(subtask -> {
                        wlSrv.addActivityLog(subtask, "", LogType.CLOSED);
                        subtask.setState(TaskState.CLOSED);
                    });
                    taskSrv.save(subtasks);
                }
                //Resolution
                if (resolution != null) {
                    task.setResolution(resolution);
                }
                if (task.getState().equals(TaskState.CLOSED)) {
                    task.setResolution(null);
                }
                TaskState oldState = (TaskState) task.getState();
                task.setState(state);
                if (StringUtils.isNotEmpty(commentMessage)) {
                    Hibernate.initialize(task.getComments());
                    task.addComment(commSrv.addComment(commentMessage, Utils.getCurrentAccount(), task));
                }
                // Zero remaining time
                if (remainingZero != null && remainingZero) {
                    task.setRemaining(PeriodHelper.inFormat("0m"));
                }
                String message = worklogStateChange(state, oldState, task);
                taskSrv.save(task);
                return ResponseEntity.ok(new ResultData(ResultData.OK, message));
            }
            return ResponseEntity.ok(new ResultData(ResultData.ERROR, msg.getMessage("error.unknown", null, Utils.getCurrentLocale())));
        } else {
            return ResponseEntity.ok(new ResultData(ResultData.ERROR,
                    msg.getMessage("role.error.task.permission", null, Utils.getCurrentLocale())));
        }

    }

    @Transactional
    @RequestMapping(value = "/task/changePoints", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResultData> changeStoryPoints(@RequestParam(value = "id") String taskID,
                                                        @RequestParam(value = "points") Integer points) {
        //check if valud story point
        if (!Utils.validStoryPoint(points)) {
            return ResponseEntity.ok(new ResultData(ResultData.ERROR, msg.getMessage("task.storyPoints.invalid", new Object[]{points}, Utils.getCurrentLocale())));
        }
        // check if not admin or user
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            // check if can edit
            if (!projectSrv.canEdit(task.getProject()) || !Roles.isPowerUser()) {
                throw new TasqAuthException(msg, "role.error.task.permission");
            }
            // updatepoints
            if (shouldAddWorklogPointsChanged(task, points)) {
                StringBuilder message = new StringBuilder(Utils.TABLE);
                message.append(Utils.changedFromTo(STORY_POINTS_TXT, task.getStory_points().toString(),
                        Integer.toString(points)));
                message.append(Utils.TABLE_END);
                wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);

            }
            task.setStory_points(points);
            taskSrv.save(task);
            return ResponseEntity.ok(new ResultData(ResultData.OK, msg.getMessage("task.storypoints.edited",
                    new Object[]{task.getId(), points}, Utils.getCurrentLocale())));
        }
        return ResponseEntity.ok(new ResultData(ResultData.ERROR, msg.getMessage("error.unknown", null, Utils.getCurrentLocale())));
    }

    @Transactional
    @RequestMapping(value = "/task/time", method = RequestMethod.GET)
    public String handleTimer(@RequestParam(value = "id") String taskID, @RequestParam(value = "action") String action,
                              RedirectAttributes ra, HttpServletRequest request) {
        Utils.setHttpRequest(request);
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            // check if can edit
            if (Roles.isPowerUser() | projectSrv.canEdit(task.getProject())) {
                switch (action) {
                    case START: {
                        Account account = Utils.getCurrentAccount();
                        if (account.getActive_task() != null && account.getActive_task().length > 0
                                && !("").equals(account.getActive_task()[0])) {
                            MessageHelper.addWarningAttribute(ra, msg.getMessage("task.stopTime.warning",
                                    new Object[]{account.getActive_task()[0]}, Utils.getCurrentLocale()));
                            return REDIRECT + request.getHeader(REFERER);
                        }
                        account.startTimerOnTask(task);
                        accSrv.update(account);
                        taskSrv.checkStateAndSave(task);
                        break;
                    }
                    case STOP:
                        Period logWork = stopTimer(task);
                        MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.logWork.logged",
                                new Object[]{PeriodHelper.outFormat(logWork), task.getId()}, Utils.getCurrentLocale()));
                        break;
                    case CANCEL: {
                        Account account = Utils.getCurrentAccount();
                        account.clearActive_task();
                        accSrv.update(account);
                        return REDIRECT_TASK + taskID;
                    }
                }
            } else {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader(REFERER);
            }
        } else {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("error.task.notfound", null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "/task/assign", method = RequestMethod.POST)
    public String assign(@RequestParam(value = "taskID") String taskID, @RequestParam(value = "email") String email,
                         RedirectAttributes ra, HttpServletRequest request) {
        Task task = taskSrv.findById(taskID);
        String previous = getAssignee(task);
        if (task != null) {
            if (Roles.isPowerUser() | projectSrv.canEdit(task.getProject())) {
                if (task.getState().equals(TaskState.CLOSED)) {
                    ResultData result = taskIsClosed(task);
                    MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                    return REDIRECT + request.getHeader(REFERER);

                }
                if (("").equals(email) && task.getAssignee() != null) {
                    task.setAssignee(null);
                    task.setLastUpdate(new Date());
                    wlSrv.addActivityLog(task, Utils.changedFromTo(previous, UNASSIGNED), LogType.ASSIGNED);
                    taskSrv.save(task);

                } else {
                    Account assignee = accSrv.findByEmail(email);
                    if (assignee != null && !assignee.equals(task.getAssignee())) {
                        // check if can edit
                        if (!projectSrv.canEdit(task.getProject())) {
                            MessageHelper.addErrorAttribute(ra,
                                    msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                            return REDIRECT + request.getHeader(REFERER);
                        }
                        task.setAssignee(assignee);
                        task.setLastUpdate(new Date());
                        watchSrv.addToWatchers(task, assignee);
                        wlSrv.addActivityLog(task, Utils.changedFromTo(previous, assignee.toString()),
                                LogType.ASSIGNED);
                        taskSrv.save(task);
                        MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.assigned",
                                new Object[]{task.getId(), assignee.toString()}, Utils.getCurrentLocale()));
                    }
                }
            } else {
                throw new TasqAuthException(msg);
            }
        }
        return REDIRECT + request.getHeader(REFERER);

    }

    private String getAssignee(Task task) {
        return task.getAssignee() == null ? UNASSIGNED : task.getAssignee().toString();
    }

    @Transactional
    @RequestMapping(value = "/task/assignMe", method = RequestMethod.GET)
    public String assignMe(@RequestParam(value = "id") String taskID, RedirectAttributes ra,
                           HttpServletRequest request) {
        assignMeToTask(taskID);
        return REDIRECT + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "/task/assignMe", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResultData> assignMePOST(@RequestParam(value = "id") String id) {
        // check if not admin or user
        if (!Roles.isPowerUser()) {
            throw new TasqAuthException(msg);
        }
        if (assignMeToTask(id)) {
            return ResponseEntity.ok(new ResultData(ResultData.OK,
                    msg.getMessage("task.assinged.me", null, Utils.getCurrentLocale()) + " " + id));
        } else {
            return ResponseEntity.ok(new ResultData(ResultData.ERROR,
                    msg.getMessage("role.error.task.permission", null, Utils.getCurrentLocale())));
        }
    }

    @Transactional
    @RequestMapping(value = "/task/priority", method = RequestMethod.GET)
    public String changePriority(@RequestParam(value = "id") String taskID,
                                 @RequestParam(value = "priority") String priority, RedirectAttributes ra, HttpServletRequest request) {
        Utils.setHttpRequest(request);
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            if (task.getState().equals(TaskState.CLOSED)) {
                ResultData result = taskIsClosed(task);
                MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                return REDIRECT + request.getHeader(REFERER);
            }
            TaskPriority newPriority = TaskPriority.valueOf(priority);
            if (!task.getPriority().equals(newPriority) && projectSrv.canEdit(task.getProject())
                    && Roles.isPowerUser()) {
                StringBuilder message = new StringBuilder();
                String oldPriority = "";
                if (task.getPriority() != null) {
                    oldPriority = task.getPriority().toString();
                }
                message.append(oldPriority);
                message.append(CHANGE_TO);
                task.setPriority(newPriority);
                message.append(task.getPriority().toString());
                wlSrv.addActivityLog(task, message.toString(), LogType.PRIORITY);
                taskSrv.save(task);
            }
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @Transactional
    @RequestMapping(value = "/task/delete", method = RequestMethod.GET)
    public String deleteTask(@RequestParam(value = "id") String taskID, RedirectAttributes ra,
                             HttpServletRequest request) throws IOException {
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            Project project = projectSrv.findById(task.getProject().getId());
            // Only allow delete for administrators, owner or app admin
            Locale currentLocale = Utils.getCurrentLocale();
            if (isAdmin(task, project)) {
                Account currentAccount = Utils.getCurrentAccount();
                Account owner = task.getOwner();
                String taskName = task.getName();
                ResultData result;
                result = taskSrv.deleteTask(task);
                if (result.code.equals(ResultData.ERROR)) {
                    MessageHelper.addWarningAttribute(ra, result.message, currentLocale);
                    //TODO rollback ?
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    return REDIRECT + request.getHeader(REFERER);
                }

                StringBuilder message = new StringBuilder(taskSrv.printID(taskID));
                message.append(" - ");
                message.append(taskName);
                //send event to owner if needed
                if (!owner.equals(currentAccount)) {
                    Locale ownerLocale = new Locale(owner.getLanguage());
                    String moreDetails = msg.getMessage("log.type.delete.info", new Object[]{currentAccount, taskID, taskName}, ownerLocale);
                    eventSrv.addSystemEvent(owner, LogType.DELETED, msg.getMessage(LogType.DELETED.getCode(), null, ownerLocale), moreDetails);
                }
                wlSrv.addWorkLogNoTask(message.toString(), project, LogType.DELETED);
                visitedSrv.delete(task);
            }
            MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.delete.success",
                    new Object[]{taskID}, currentLocale), currentLocale);
            return "redirect:/";
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @RequestMapping(value = "/task/attachFiles", method = RequestMethod.POST)
    public String attacheFiles(@RequestParam String taskID, @RequestParam List<MultipartFile> files,
                               HttpServletRequest request) {
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            // check if can edit
            if (!projectSrv.canEdit(task.getProject()) && !Roles.isPowerUser()) {
                throw new TasqAuthException(msg, "role.error.task.permission");
            }
            saveTaskFiles(files, task);
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @RequestMapping(value = "/task/{id}/file", method = RequestMethod.GET)
    public void downloadFile(@PathVariable String id, @RequestParam("get") String filename,
                             HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) throws IOException {
        Task task = taskSrv.findById(id);
        if (task.getProject().getParticipants().contains(Utils.getCurrentAccount())) {
            File file = new File(taskSrv.getTaskDirectory(task) + File.separator + filename);
            response.setHeader("content-Disposition", "attachment; filename=" + filename);
            try (InputStream is = new FileInputStream(file)) {
                IOUtils.copyLarge(is, response.getOutputStream());
            } catch (IOException e) {
                LOG.error("Error while writing to output stream , filename '{}'", filename, e);
            } finally {
                response.flushBuffer();
            }
        } else {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
        }
    }

    @RequestMapping(value = "/task/{id}/{subid}/file", method = RequestMethod.GET)
    public void downloadSubtaskFile(@PathVariable String id, @PathVariable String subid,
                                    @RequestParam("get") String filename, HttpServletRequest request, HttpServletResponse response,
                                    RedirectAttributes ra) throws IOException {
        downloadFile(id + "/" + subid, filename, request, response, ra);
    }

    @RequestMapping(value = "/task/{id}/imgfile", method = RequestMethod.GET)
    public void showImageFile(@PathVariable String id, @RequestParam("get") String filename,
                              HttpServletResponse response, RedirectAttributes ra) throws IOException {
        Task task = taskSrv.findById(id);
        if (task.getProject().getParticipants().contains(Utils.getCurrentAccount())) {
            File file = new File(taskSrv.getTaskDirectory(task) + File.separator + filename);
            response.setHeader("content-Disposition", "attachment; filename=" + filename);
            try (InputStream is = new FileInputStream(file)) {
                response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
                IOUtils.copyLarge(is, response.getOutputStream());
            } catch (FileNotFoundException e) {
                LOG.error("File not found filename '{}'", filename, e);
            } catch (IOException e) {
                LOG.error("Error while writing to output stream , filename '{}'", filename, e);
            } finally {
                response.flushBuffer();
                response.getOutputStream().close();
            }

        } else {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
        }
    }

    @RequestMapping(value = "/task/{id}/{subid}/imgfile", method = RequestMethod.GET)
    public void showSubTaskImageFile(@PathVariable String id, @PathVariable String subid,
                                     @RequestParam("get") String filename, HttpServletRequest request, HttpServletResponse response,
                                     RedirectAttributes ra) throws IOException {
        showImageFile(id + "/" + subid, filename, response, ra);
    }

    @RequestMapping(value = "/task/removeFile", method = RequestMethod.GET)
    public String removeFile(@RequestParam String id, @RequestParam("file") String filename, HttpServletRequest request,
                             RedirectAttributes ra) {
        Task task = taskSrv.findById(id);
        if (!projectSrv.canEdit(task.getProject())
                && (!Roles.isUser() || !task.getOwner().equals(Utils.getCurrentAccount()))) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
        } else {
            File file = new File(taskSrv.getTaskDirectory(task) + File.separator + filename);
            if (file.exists()) {
                file.delete();
                MessageHelper.addSuccessAttribute(ra,
                        msg.getMessage("task.file.deleted", new Object[]{filename}, Utils.getCurrentLocale()));
            }
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    /**
     * Convert subtask into new regular task
     *
     * @param id
     * @param type
     * @param request
     * @param ra
     * @return
     * @throws IOException
     */
    @Transactional
    @RequestMapping(value = "/task/conver2task", method = RequestMethod.POST)
    public String convert2task(@RequestParam("taskid") String id, @RequestParam("type") TaskType type,
                               HttpServletRequest request, RedirectAttributes ra) throws IOException {
        Task subtask = taskSrv.findById(id);
        Project project = projectSrv.findById(subtask.getProject().getId());
        if (!projectSrv.canEdit(project)
                && (!Roles.isUser() || !subtask.getOwner().equals(Utils.getCurrentAccount()))) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader(REFERER);
        } else {
            ResultData checkResult = taskSrv.checkTaskCanOperated(subtask, false);
            if (checkResult.code.equals(ResultData.ERROR)) {
                MessageHelper.addWarningAttribute(ra, checkResult.message, Utils.getCurrentLocale());
                return REDIRECT + request.getHeader(REFERER);
            }
            Task parent = taskSrv.findById(subtask.getParent());
            long taskCount = project.getLastTaskNo();
            taskCount++;
            String taskID = project.getProjectId() + "-" + taskCount;
            Task task = cloneTask(subtask, taskID);
            task.setParent(null);
            task.setType(type);
            task.setTaskOrder(taskCount);
            task.setEstimated(false);
            taskSrv.save(task);
            List<Event> events = eventSrv.getTaskEvents(id);
            for (Event event : events) {
                event.setTask(taskID);
                eventSrv.save(event);
            }
            List<WorkLog> worklogs = wlSrv.getTaskEvents(id);
            for (WorkLog workLog : worklogs) {
                workLog.setTask(task);
            }
            Set<Comment> comments = commSrv.findByTaskIdOrderByDateDesc(id);
            for (Comment comment : comments) {
                comment.setTask(task);
            }
            List<TaskLink> links = linkService.findAllTaskLinks(subtask);
            for (TaskLink taskLink : links) {
                if (taskLink.getTaskA().equals(id)) {
                    taskLink.setTaskA(taskID);
                }
                if (taskLink.getTaskB().equals(id)) {
                    taskLink.setTaskB(taskID);
                }
                linkService.save(taskLink);
            }
            // files
            File oldDir = new File(taskSrv.getTaskDir(subtask));
            if (oldDir.exists()) {
                File newDir = new File(taskSrv.getTaskDir(task));
                if (!newDir.mkdirs()) {
                    LOG.error("Failed to create new dir {}", newDir.getAbsolutePath());
                }
                newDir.setWritable(true, false);
                newDir.setReadable(true, false);
                FileUtils.copyDirectory(oldDir, newDir);
            }
            project.setLastTaskNo(taskCount);
            project.getTasks().add(task);
            projectSrv.save(project);
            // Add log
            StringBuilder message = new StringBuilder(Utils.TABLE);
            message.append(Utils.changedFromTo("ID", id, taskID));
            message.append(Utils.changedFromTo(TYPE_TXT, subtask.getType().toString(), type.toString()));
            message.append(Utils.TABLE_END);
            wlSrv.addActivityLog(task, message.toString(), LogType.SUBTASK2TASK);
            taskSrv.save(task);
            // cleanup
            taskSrv.deleteTask(subtask);
            MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.subtasks.2task.success",
                    new Object[]{id, taskID}, Utils.getCurrentLocale()));
            TaskLink link = new TaskLink(parent.getId(), taskID, TaskLinkType.RELATES_TO);
            linkService.save(link);
            return REDIRECT_TASK + taskID;
        }
    }

    private Task cloneTask(Task subtask, String taskID) {
        Task task = new Task();
        BeanUtils.copyProperties(subtask, task);
        task.setId(taskID);
        task.setEstimate(subtask.getRawEstimate());
        task.setLoggedWork(subtask.getRawLoggedWork());
        task.setRemaining(subtask.getRawRemaining());
        task.setDue_date(subtask.getRawDue_date());
        task.setCreate_date(subtask.getRawCreate_date());
        task.setLastUpdate(new Date());
        Hibernate.initialize(subtask.getTags());
        Set<Tag> tags = subtask.getTags();
        task.setTags(tags);
        Hibernate.initialize(subtask.getSprints());
        Set<Sprint> sprints = subtask.getSprints();
        task.setSprints(sprints);
        return task;
    }

    @Transactional
    @RequestMapping(value = "task/delWorklog", method = RequestMethod.GET)
    public String deleteWorkLog(@RequestParam("id") Long id, RedirectAttributes ra, HttpServletRequest request,
                                Model model) {
        if (Roles.isAdmin()) {
            WorkLog wl = wlSrv.findById(id);
            if (wl != null) {
                Long projectID = wl.getTask().getProject().getId();
                wlSrv.delete(wl);
                MessageHelper.addSuccessAttribute(ra,
                        msg.getMessage("task.worklog.deleted", null, Utils.getCurrentLocale()));
                return "redirect:/manage/tasks?project=" + projectID;
            } else {
                return REDIRECT + request.getHeader(REFERER);
            }
        } else {
            throw new TasqAuthException();
        }
    }

    /**
     * Admin call to update all tasks logged work based on their worklog events
     *
     * @param ra
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "task/updatelogs", method = RequestMethod.GET)
    public String update(@RequestParam(value = "project", required = false) Long project, RedirectAttributes ra,
                         HttpServletRequest request, Model model) {
        if (Roles.isAdmin()) {
            List<Task> list;
            if (project != null) {
                list = taskSrv.findAllByProjectId(project);
            } else {
                list = taskSrv.findAll();
            }
            StringBuilder console = new StringBuilder("Updating logged work on all tasks within application");
            console.append(BR);
            for (Task task : list) {
                task.updateLoggedWork();
                console.append(task.toString());
                console.append(": updated with ");
                console.append(task.getLoggedWork());
                console.append(BR);
            }
            model.addAttribute("console", console.toString());
            return "other/console";
        } else {
            throw new TasqAuthException();
        }
    }

    /**
     * Helper temp method to eliminate depreciated task without finishDate
     *
     * @param ra
     * @param request
     * @param model
     * @return
     */
    @Deprecated
    @Transactional
    @RequestMapping(value = "task/updateFinish", method = RequestMethod.GET)
    public String updateFinishDate(@RequestParam(value = "project", required = false) Long project,
                                   RedirectAttributes ra, HttpServletRequest request, Model model) {
        if (Roles.isAdmin()) {
            List<Task> list;
            if (project != null) {
                list = taskSrv.findAllByProjectId(project);
            } else {
                list = taskSrv.findAll();
            }
            StringBuilder console = new StringBuilder("Updating logged work on tasks within application");
            console.append(BR);
            for (Task task : list) {
                List<WorkLog> worklogs = wlSrv.getTaskEvents(task.getId());
                WorkLog closing = new WorkLog();
                for (WorkLog workLog : worklogs) {
                    if (workLog.getType().equals(LogType.CLOSED)) {
                        closing = workLog;
                    }
                }
                task.setFinishDate(closing.getRawTime());
                console.append(task.toString());
                console.append(": finish date set to :");
                console.append(closing.getRawTime());
                console.append(BR);
            }
            model.addAttribute("console", console.toString());
            return "other/console";
        } else {
            throw new TasqAuthException();
        }
    }

    private ResultData taskIsClosed(Task task) {
        String localized = msg.getMessage(((TaskState) task.getState()).getCode(), null, Utils.getCurrentLocale());
        return new ResultData(ResultData.ERROR,
                msg.getMessage("task.closed.cannot.operate=", new Object[]{localized}, Utils.getCurrentLocale()));
    }

    /**
     * Fills model with project list and user's active project
     *
     * @param model
     */
    private void fillCreateTaskModel(Model model) {
        if (!Roles.isUser()) {
            throw new TasqAuthException(msg);
        }
        Project project = projectSrv.findUserActiveProject();
        if (project == null) {
            throw new TasqAuthException(msg, "error.noProjects");
        }
        model.addAttribute("project", project);
        model.addAttribute("projects_list", projectSrv.findAllByUser());
    }

    private boolean checkIfNotEstimated(Task task, Project project) {
        if (!project.getTimeTracked()) {
            if (task.getStory_points() == 0 && task.isEstimated()) {
                return true;
            }
        } else {
            if (task.getEstimate().equals("0m") && task.isEstimated()) {
                return true;
            }

        }
        return false;
    }

    private String worklogStateChange(TaskState state, TaskState oldState, Task task) {
        if (TaskState.CLOSED.equals(state)) {
            task.setFinishDate(new Date());
            wlSrv.addActivityLog(task, "", LogType.CLOSED);
            taskSrv.save(task);
            return msg.getMessage("task.state.changed.closed", new Object[]{task.getId()}, Utils.getCurrentLocale());
        } else if (TaskState.CLOSED.equals(oldState)) {
            wlSrv.addActivityLog(task, "", LogType.REOPEN);
            task.setFinishDate(null);
            taskSrv.save(task);
            return msg.getMessage("task.state.changed.reopened", new Object[]{task.getId()},
                    Utils.getCurrentLocale());
        } else {
            taskSrv.changeState(oldState, state, task);
            String localised = msg.getMessage(state.getCode(), null, Utils.getCurrentLocale());
            return msg.getMessage("task.state.changed", new Object[]{task.getId(), localised},
                    Utils.getCurrentLocale());
        }
    }

    /**
     * Checks if worklog point changes should be added. If task is in active
     * sprint , work log is added and false is returned
     *
     * @param task
     * @param storyPoints
     * @return
     */
    private boolean shouldAddWorklogPointsChanged(Task task, int storyPoints) {
        if (sprintSrv.taskInActiveSprint(task)) {
            wlSrv.addActivityLog(task, Integer.toString(-1 * (task.getStory_points() - storyPoints)), LogType.ESTIMATE);
            taskSrv.save(task);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Stops currently running timer on task
     *
     * @param task
     * @return Period logged as worklog
     */
    private Period stopTimer(Task task) {
        Account account = Utils.getCurrentAccount();
        if (account.getActive_task() != null && account.getActive_task().length > 0
                && !(task.getId()).equals(account.getActive_task()[0])) {
            DateTime now = new DateTime();
            Period logWork = new Period((DateTime) account.getActive_task_time(), now);
            // Only log work if greater than 1 minute
            if (logWork.toStandardDuration().getMillis() / 1000 / 60 < 1) {
                logWork = new Period().plusMinutes(1);
            }
            wlSrv.addTimedWorkLog(task, PeriodHelper.outFormat(logWork), new Date(), null, logWork, LogType.LOG);
            account.clearActive_task();
            accSrv.update(account);
            return logWork;
        }
        return null;
    }

    /**
     * Check if is project admin or admin
     *
     * @param task
     * @param project
     * @return
     */
    private boolean isAdmin(Task task, Project project) {
        Account currentAccount = Utils.getCurrentAccount();
        return project.getAdministrators().contains(currentAccount) || task.getOwner().equals(currentAccount)
                || Roles.isAdmin();
    }

    private boolean saveTaskFiles(List<MultipartFile> filesArray, Task task) {
        // Save
        for (MultipartFile multipartFile : filesArray) {
            if (!multipartFile.isEmpty()) {
                File file = new File(
                        taskSrv.getTaskDirectory(task) + File.separator + multipartFile.getOriginalFilename());
                try {
                    FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
                } catch (IOException e) {
                    LOG.error("IOException while saving task files", e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get's tasks all files
     *
     * @param task
     * @return
     */
    private List<String> getTaskFiles(Task task) {
        File folder = new File(taskSrv.getTaskDirectory(task));
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    fileNames.add(fileName);
                }
            }
        }
        return fileNames;
    }

    /**
     * Assigns currently logged user into task with given ID
     *
     * @param id
     * @return
     */
    private boolean assignMeToTask(String id) {
        Task task = taskSrv.findById(id);
        String previous = getAssignee(task);
        if (!projectSrv.canEdit(task.getProject())) {
            return false;
        }
        Account assignee = Utils.getCurrentAccount();
        if (!assignee.equals(task.getAssignee())) {
            task.setAssignee(assignee);
            task.setLastUpdate(new Date());
            wlSrv.addActivityLog(task, Utils.changedFromTo(previous, assignee.toString()), LogType.ASSIGNED);
            task = taskSrv.save(task);
            watchSrv.startWatching(task);
            return true;
        }
        return false;
    }

    protected EntityManager getEntitymanager() {
        return entityManager;
    }
}
