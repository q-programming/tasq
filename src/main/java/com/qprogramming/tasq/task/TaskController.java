/**
 *
 */
package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
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
import com.qprogramming.tasq.task.comments.CommentsRepository;
import com.qprogramming.tasq.task.link.TaskLink;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.link.TaskLinkType;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.tag.TagsRepository;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

    private TaskService taskSrv;
    private ProjectService projectSrv;
    private AccountService accSrv;
    private WorkLogService wlSrv;
    private MessageSource msg;
    private AgileService sprintSrv;
    private TaskLinkService linkService;
    private WatchedTaskService watchSrv;
    private CommentsRepository commRepo;
    private TagsRepository tagsRepo;
    private EventsService eventSrv;

    @Autowired
    public TaskController(TaskService taskSrv, ProjectService projectSrv, AccountService accSrv, WorkLogService wlSrv,
                          MessageSource msg, AgileService sprintSrv, TaskLinkService linkService, CommentsRepository commRepo,
                          TagsRepository tagsRepo, WatchedTaskService watchSrv, EventsService eventSrv) {
        this.taskSrv = taskSrv;
        this.projectSrv = projectSrv;
        this.accSrv = accSrv;
        this.wlSrv = wlSrv;
        this.msg = msg;
        this.sprintSrv = sprintSrv;
        this.linkService = linkService;
        this.commRepo = commRepo;
        this.tagsRepo = tagsRepo;
        this.watchSrv = watchSrv;
        this.eventSrv = eventSrv;
    }

    @RequestMapping(value = "task/create", method = RequestMethod.GET)
    public TaskForm startTaskCreate(Model model) {
        fillCreateTaskModel(model);
        return new TaskForm();
    }

    @RequestMapping(value = "task/create", method = RequestMethod.POST)
    public String createTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, BindingResult result,
                             @RequestParam(value = "linked", required = false) String linked, RedirectAttributes ra,
                             HttpServletRequest request, Model model) {
        if (!Roles.isUser()) {
            throw new TasqAuthException(msg);
        }
        if (result.hasErrors()) {
            fillCreateTaskModel(model);
            return null;
        }
        Project project = projectSrv.findById(taskForm.getProject());
        if (project != null) {
            // check if can edit
            if (!projectSrv.canEdit(project)) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader("Referer");
            }
            Task task;
            try {
                task = taskForm.createTask();
            } catch (IllegalArgumentException e) {
                result.rejectValue("estimate", "error.estimateFormat");
                fillCreateTaskModel(model);
                return null;
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
            if (taskForm.getAssignee() != null) {
                Account assignee = accSrv.findById(taskForm.getAssignee());
                task.setAssignee(assignee);
                watchSrv.addToWatchers(task, assignee);
            }
            // lookup for sprint
            // Create log work
            task = taskSrv.save(task);
            if (taskForm.getAddToSprint() != null) {
                Sprint sprint = sprintSrv.findByProjectIdAndSprintNo(project.getId(), taskForm.getAddToSprint());
                task.addSprint(sprint);
                // increase scope
                if (sprint.isActive()) {
                    if (checkIfNotEstimated(task, project)) {
                        result.rejectValue("addToSprint", "agile.task2Sprint.Notestimated",
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
            projectSrv.save(project);
            // Save files
            saveTaskFiles(taskForm.getFiles(), task);
            wlSrv.addActivityLog(task, "", LogType.CREATE);
            watchSrv.startWatching(task);
            if (linked != null) {
                Task linkedTask = taskSrv.findById(linked);
                if (linkedTask != null) {
                    TaskLink link = new TaskLink(linkedTask.getId(), taskID, TaskLinkType.RELATES_TO);
                    linkService.save(link);
                    wlSrv.addWorkLogNoTask(linked + " - " + taskID, project, LogType.TASK_LINK);
                }
            }
            return REDIRECT_TASK + taskID;
        }
        return null;
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/edit", method = RequestMethod.GET)
    public TaskForm startEditTask(@PathVariable("id") String id, Model model) {
        Task task = taskSrv.findById(id);
        if (projectSrv.canEdit(task.getProject())
                && (Roles.isUser() | task.getOwner().equals(Utils.getCurrentAccount()))) {
            Hibernate.initialize(task.getRawWorkLog());
            model.addAttribute("task", task);
            model.addAttribute("project", task.getProject());
            return new TaskForm(task);
        } else {
            throw new TasqAuthException(msg);
        }
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/{subid}/edit", method = RequestMethod.GET)
    public TaskForm startEditSubTask(@PathVariable("id") String id, @PathVariable("subid") String subid, Model model) {
        return startEditTask(createSubId(id, subid), model);
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/{subid}/edit", method = RequestMethod.POST)
    public String editSubTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, Errors errors,
                              RedirectAttributes ra, HttpServletRequest request) {
        return editTask(taskForm, errors, ra, request);
    }

    @Transactional
    @RequestMapping(value = "/task/{id}/edit", method = RequestMethod.POST)
    public String editTask(@Valid @ModelAttribute("taskForm") TaskForm taskForm, Errors errors, RedirectAttributes ra,
                           HttpServletRequest request) {
        if (errors.hasErrors()) {
            return null;
        }
        String taskID = taskForm.getId();
        Task task = taskSrv.findById(taskID);
        if (task == null) {
            // something went wrong
            return null;
        }
        // check if can edit
        if (!projectSrv.canEdit(task.getProject())
                && (!Roles.isUser() | !task.getOwner().equals(Utils.getCurrentAccount()))) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader("Referer");
        }
        if (task.getState().equals(TaskState.CLOSED)) {
            ResultData result = taskIsClosed(ra, request, task);
            MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
            return REDIRECT + request.getHeader("Referer");
        }
        StringBuilder message = new StringBuilder(Utils.TABLE);
        if (!task.getName().equalsIgnoreCase(taskForm.getName())) {
            message.append(Utils.changedFromTo(NAME_TXT, task.getName(), taskForm.getName()));
            task.setName(taskForm.getName());
            updateWatched(task);
        }
        if (!task.getDescription().equalsIgnoreCase(taskForm.getDescription())) {
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

        boolean notestimated = !taskForm.getEstimated();
        if (!task.isEstimated().equals(notestimated)) {
            message.append(
                    Utils.changedFromTo(ESTIMATED_TXT, task.getEstimated().toString(), Boolean.toString(notestimated)));
            task.setEstimated(notestimated);
            if (!task.isEstimated()) {
                task.setStory_points(0);
            }
        }
        // Don't check for SP if task is not estimated
        if (task.isEstimated()) {
            try {
                int storyPoints = taskForm.getStory_points() == null || ("").equals(taskForm.getStory_points()) ? 0
                        : Integer.parseInt(taskForm.getStory_points());

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
        taskSrv.save(task);
        message.append(Utils.TABLE_END);
        if (message.length() > 37) {
            wlSrv.addActivityLog(task, message.toString(), LogType.EDITED);
        }
        return REDIRECT_TASK + taskID;
    }

    private void updateWatched(Task task) {
        WatchedTask watched = watchSrv.getByTask(task);
        if (watched != null) {
            watched.setName(task.getName());
            watched.setType((TaskType) task.getType());
        }

    }

    @RequestMapping(value = "task/{id}/{subId}", method = RequestMethod.GET)
    public String showSubTaskDetails(@PathVariable(value = "id") String id, @PathVariable(value = "subId") String subId,
                                     Model model, RedirectAttributes ra) {
        return showTaskDetails(createSubId(id, subId), model, ra);
    }

    @RequestMapping(value = "task/{id}", method = RequestMethod.GET)
    public String showTaskDetails(@PathVariable String id, Model model, RedirectAttributes ra) {
        Task task = taskSrv.findById(id);
        if (task == null) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage("task.notexists", null, Utils.getCurrentLocale()));
            return "redirect:/tasks";
        }
        Account account = Utils.getCurrentAccount();
        List<Task> lastVisited = account.getLast_visited_t();
        lastVisited.add(0, task);
        List<Task> clean = new ArrayList<>();
        Set<Task> lookup = new HashSet<>();
        for (Task item : lastVisited) {
            if (lookup.add(item)) {
                clean.add(item);
            }
        }
        if (clean.size() > 4) {
            clean = clean.subList(0, 4);
        }
        account.setLast_visited_t(clean);
        account = accSrv.update(account);
        // TASK
        Set<Comment> comments = commRepo.findByTaskIdOrderByDateDesc(id);
        Map<TaskLinkType, List<DisplayTask>> links = linkService.findTaskLinks(id);
        if (!task.isSubtask()) {
            List<Task> subtasks = taskSrv.findSubtasks(task);
            // Add all subtasks into remaining work
            for (Task subtask : subtasks) {
                task.setEstimate(PeriodHelper.plusPeriods(task.getRawEstimate(), subtask.getRawEstimate()));
                task.setLoggedWork(PeriodHelper.plusPeriods(task.getRawLoggedWork(), subtask.getRawLoggedWork()));
                task.setRemaining(PeriodHelper.plusPeriods(task.getRawRemaining(), subtask.getRawRemaining()));
            }
            Collections.sort(subtasks, new TaskSorter(TaskSorter.SORTBY.ID, true));
            model.addAttribute("subtasks", subtasks);
        }
        model.addAttribute("watching", watchSrv.isWatching(task.getId()));
        model.addAttribute("comments", comments);
        model.addAttribute("task", task);
        model.addAttribute("links", links);
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
                currentAccount.getActive_project(), true));
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
            projectObj = projects.stream().filter(p -> p.getId().equals(currentAccount.getActive_project())).findFirst();
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
        Project project = projectSrv.findById(taskForm.getProject());
        if (errors.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("task", task);
            return null;
        }
        if (!projectSrv.canEdit(project)) {
            MessageHelper.addErrorAttribute(ra, msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
            return REDIRECT + request.getHeader("Referer");
        }
        Task subTask = taskForm.createSubTask();
        // build ID
        int taskCount = task.getSubtasks();
        taskCount++;
        String taskID = createSubId(task.getId(), String.valueOf(taskCount));
        subTask.setId(taskID);
        subTask.setParent(task.getId());
        subTask.setProject(project);
        task.addSubTask();
        // assigne
        if (taskForm.getAssignee() != null) {
            Account assignee = accSrv.findById(taskForm.getAssignee());
            subTask.setAssignee(assignee);
        }
        if (sprintSrv.taskInActiveSprint(task)) {
            Sprint active = sprintSrv.findByProjectIdAndActiveTrue(task.getProject().getId());
            subTask.addSprint(active);
        }
        Hibernate.initialize(task.getSubtasks());
        taskSrv.save(subTask);
        taskSrv.save(task);
        // TODO save in subdir?
        // saveTaskFiles(taskForm.getFiles(), subTask);
        wlSrv.addActivityLog(subTask, "", LogType.SUBTASK);
        return REDIRECT_TASK + id;
    }

    /**
     * Logs work . If only digits are sent , it's pressumed that those were
     * hours
     *
     * @param taskID     - ID of task for which work is logged
     * @param loggedWork - amount of time spent
     * @param ra
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "logwork", method = RequestMethod.POST)
    public String logWork(@RequestParam(value = "taskID") String taskID,
                          @RequestParam(value = "loggedWork") String loggedWork,
                          @RequestParam(value = "remaining", required = false) String remainingTxt,
                          @RequestParam("date_logged") String dateLogged, @RequestParam("time_logged") String timeLogged,
                          RedirectAttributes ra, HttpServletRequest request, Model model) {
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            // check if can edit
            if (Roles.isPowerUser() | projectSrv.canEdit(task.getProject())) {
                try {
                    if (loggedWork.matches("[0-9]+")) {
                        loggedWork += "h";
                    }
                    Period logged = PeriodHelper.inFormat(loggedWork);
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
                        if (remainingTxt.matches("[0-9]+")) {
                            remainingTxt += "h";
                        }
                        remaining = PeriodHelper.inFormat(remainingTxt);
                        wlSrv.addDatedWorkLog(task, remainingTxt, when, LogType.ESTIMATE);
                    }
                    wlSrv.addTimedWorkLog(task, message.toString(), when, remaining, logged, LogType.LOG);
                    MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.logWork.logged",
                            new Object[]{loggedWork, task.getId()}, Utils.getCurrentLocale()));
                } catch (IllegalArgumentException e) {
                    MessageHelper.addErrorAttribute(ra,
                            msg.getMessage("error.estimateFormat", null, Utils.getCurrentLocale()));
                    return REDIRECT + request.getHeader("Referer");
                }
            } else {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader("Referer");
            }
        }
        return REDIRECT + request.getHeader("Referer");
    }

    @Transactional
    @RequestMapping(value = "/task/changeState", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResultData> changeState(@RequestParam(value = "id") String taskID,
                                                  @RequestParam(value = "state") TaskState state,
                                                  @RequestParam(value = "zero_checkbox", required = false) Boolean remainingZero,
                                                  @RequestParam(value = "closesubtasks", required = false) Boolean closeSubtasks,
                                                  @RequestParam(value = "message", required = false) String commentMessage) {
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
                                msg.getMessage("task.alreadyStarted", null, Utils.getCurrentLocale())));
                    }
                } else if (TaskState.CLOSED.equals(state)) {
                    ResultData result = checkTaskCanOperated(task, false);
                    if (result.code.equals(ResultData.ERROR)) {
                        return ResponseEntity.ok(result);
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

                TaskState oldState = (TaskState) task.getState();
                task.setState(state);
                if (StringUtils.isNotEmpty(commentMessage)) {
                    if (Utils.containsHTMLTags(commentMessage)) {
                        return ResponseEntity.ok(new ResultData(ResultData.ERROR,
                                msg.getMessage("comment.htmlTag", null, Utils.getCurrentLocale())));
                    } else {
                        Comment comment = new Comment();
                        comment.setTask(task);
                        comment.setAuthor(Utils.getCurrentAccount());
                        comment.setDate(new Date());
                        comment.setMessage(commentMessage);
                        commRepo.save(comment);
                        Hibernate.initialize(task.getComments());
                        task.addComment(comment);
                        wlSrv.addActivityLog(task, commentMessage, LogType.COMMENT);
                    }
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
            if (!projectSrv.canEdit(task.getProject()) || !Roles.isPowerUser()) {
                MessageHelper.addErrorAttribute(ra,
                        msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                return REDIRECT + request.getHeader("Referer");
            }
            switch (action) {
                case START: {
                    Account account = Utils.getCurrentAccount();
                    if (account.getActive_task() != null && account.getActive_task().length > 0
                            && !("").equals(account.getActive_task()[0])) {
                        MessageHelper.addWarningAttribute(ra, msg.getMessage("task.stopTime.warning",
                                new Object[]{account.getActive_task()[0]}, Utils.getCurrentLocale()));
                        return REDIRECT + request.getHeader("Referer");
                    }
                    account.startTimerOnTask(task);
                    accSrv.update(account);
                    wlSrv.checkStateAndSave(task);
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
            return REDIRECT + request.getHeader("Referer");
        }
        return REDIRECT + request.getHeader("Referer");
    }

    @RequestMapping(value = "/task/assign", method = RequestMethod.POST)
    public String assign(@RequestParam(value = "taskID") String taskID, @RequestParam(value = "email") String email,
                         RedirectAttributes ra, HttpServletRequest request) {
        Task task = taskSrv.findById(taskID);
        String previous = getAssignee(task);
        if (task != null) {
            if (Roles.isPowerUser() | projectSrv.canEdit(task.getProject())) {
                if (task.getState().equals(TaskState.CLOSED)) {
                    ResultData result = taskIsClosed(ra, request, task);
                    MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                    return REDIRECT + request.getHeader("Referer");

                }
                if (("").equals(email) && task.getAssignee() != null) {
                    task.setAssignee(null);
                    task.setLastUpdate(new Date());
                    taskSrv.save(task);
                    wlSrv.addActivityLog(task, Utils.changedFromTo(previous, UNASSIGNED), LogType.ASSIGNED);

                } else {
                    Account assignee = accSrv.findByEmail(email);
                    if (assignee != null && !assignee.equals(task.getAssignee())) {
                        // check if can edit
                        if (!projectSrv.canEdit(task.getProject())) {
                            MessageHelper.addErrorAttribute(ra,
                                    msg.getMessage(ERROR_ACCES_RIGHTS, null, Utils.getCurrentLocale()));
                            return REDIRECT + request.getHeader("Referer");
                        }
                        task.setAssignee(assignee);
                        task.setLastUpdate(new Date());
                        taskSrv.save(task);
                        watchSrv.addToWatchers(task, assignee);
                        wlSrv.addActivityLog(task, Utils.changedFromTo(previous, assignee.toString()),
                                LogType.ASSIGNED);
                        MessageHelper.addSuccessAttribute(ra, msg.getMessage("task.assigned",
                                new Object[]{task.getId(), assignee.toString()}, Utils.getCurrentLocale()));
                    }
                }
            } else {
                throw new TasqAuthException(msg);
            }
        }
        return REDIRECT + request.getHeader("Referer");

    }

    private String getAssignee(Task task) {
        return task.getAssignee() == null ? UNASSIGNED : task.getAssignee().toString();
    }

    @RequestMapping(value = "/task/assignMe", method = RequestMethod.GET)
    public String assignMe(@RequestParam(value = "id") String taskID, RedirectAttributes ra,
                           HttpServletRequest request) {
        assignMeToTask(taskID);
        return REDIRECT + request.getHeader("Referer");
    }

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

    @RequestMapping(value = "/task/priority", method = RequestMethod.GET)
    public String changePriority(@RequestParam(value = "id") String taskID,
                                 @RequestParam(value = "priority") String priority, RedirectAttributes ra, HttpServletRequest request) {
        Utils.setHttpRequest(request);
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            if (task.getState().equals(TaskState.CLOSED)) {
                ResultData result = taskIsClosed(ra, request, task);
                MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                return REDIRECT + request.getHeader("Referer");
            }
            TaskPriority newPriority = TaskPriority.valueOf(priority);
            if (!task.getPriority().equals(newPriority) && projectSrv.canEdit(task.getProject())
                    && Roles.isPowerUser()) {
                StringBuilder message = new StringBuilder();
                String oldPriority = "";
                // TODO temporary due to old DB
                if (task.getPriority() != null) {
                    oldPriority = task.getPriority().toString();
                }
                message.append(oldPriority);
                message.append(CHANGE_TO);
                task.setPriority(newPriority);
                message.append(task.getPriority().toString());
                taskSrv.save(task);
                wlSrv.addActivityLog(task, message.toString(), LogType.PRIORITY);
            }
        }
        return REDIRECT + request.getHeader("Referer");
    }

    @Transactional
    @RequestMapping(value = "/task/delete", method = RequestMethod.GET)
    public String deleteTask(@RequestParam(value = "id") String taskID, RedirectAttributes ra,
                             HttpServletRequest request) {
        Task task = taskSrv.findById(taskID);
        if (task != null) {
            Project project = projectSrv.findById(task.getProject().getId());
            // Only allow delete for administrators, owner or app admin
            if (isAdmin(task, project)) {
                ResultData result;
                // check for links and subtasks
                List<Task> subtasks = taskSrv.findSubtasks(taskID);
                for (Task subtask : subtasks) {
                    result = removeTaskRelations(subtask);
                    if (ResultData.ERROR.equals(result.code)) {
                        MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                        return REDIRECT + request.getHeader("Referer");
                    }
                }
                taskSrv.deleteAll(subtasks);
                deleteFiles(task);
                result = removeTaskRelations(task);
                if (result.code.equals(ResultData.ERROR)) {
                    MessageHelper.addWarningAttribute(ra, result.message, Utils.getCurrentLocale());
                    return REDIRECT + request.getHeader("Referer");
                }
                // delete files
                // leave message and clear all
                StringBuilder message = new StringBuilder();
                message.append("[");
                message.append(task.getId());
                message.append("]");
                message.append(" - ");
                message.append(task.getName());
                if (task.isSubtask()) {
                    Task parentTask = taskSrv.findById(task.getParent());
                    int count = parentTask.getSubtasks();
                    parentTask.setSubtasks(--count);
                    taskSrv.save(parentTask);
                }
                Task purged = taskSrv.save(purgeTask(task));
                taskSrv.delete(purged);
                wlSrv.addWorkLogNoTask(message.toString(), project, LogType.DELETED);
            }
            // TODO add message about removed task
            return "redirect:/";
        }
        return REDIRECT + request.getHeader("Referer");
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
        return REDIRECT + request.getHeader("Referer");
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
        return REDIRECT + request.getHeader("Referer");
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
            return REDIRECT + request.getHeader("Referer");
        } else {
            Task parent = taskSrv.findById(subtask.getParent());
            parent.removeSubTask();
            taskSrv.save(parent);

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
            Set<Comment> comments = commRepo.findByTaskIdOrderByDateDesc(id);
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
            // TODO After TASQ-165 fixed
            // File oldDir = new File(taskSrv.getTaskDir(subtask));
            // if (oldDir.exists()){
            // File newDir = new File(taskSrv.getTaskDir(task));
            // newDir.mkdirs();
            // newDir.setWritable(true, false);
            // newDir.setReadable(true, false);
            // FileUtils.copyDirectory(oldDir, newDir);
            // }
            project.setLastTaskNo(taskCount);
            project.getTasks().add(task);
            projectSrv.save(project);
            // Add log
            StringBuilder message = new StringBuilder(Utils.TABLE);
            message.append(Utils.changedFromTo("ID", id, taskID));
            message.append(Utils.changedFromTo(TYPE_TXT, subtask.getType().toString(), type.toString()));
            message.append(Utils.TABLE_END);
            wlSrv.addActivityLog(task, message.toString(), LogType.SUBTASK2TASK);
            // cleanup
            ResultData result = removeTaskRelations(subtask);
            if (result.code.equals(ResultData.ERROR)) {
                throw new TasqException(result.message);
            }
            subtask = taskSrv.save(purgeTask(subtask));
            taskSrv.delete(subtask);
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
                return REDIRECT + request.getHeader("Referer");
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

    private String createSubId(String id, String subId) {
        return id + "/" + subId;
    }

    private ResultData taskIsClosed(RedirectAttributes ra, HttpServletRequest request, Task task) {
        String localized = msg.getMessage(((TaskState) task.getState()).getCode(), null, Utils.getCurrentLocale());
        return new ResultData(ResultData.ERROR,
                msg.getMessage("task.closed", new Object[]{localized}, Utils.getCurrentLocale()));
    }

    /**
     * private method to remove task and all potential links
     *
     * @param task
     * @return
     */
    private ResultData removeTaskRelations(Task task) {
        ResultData result = checkTaskCanOperated(task, true);
        if (ResultData.OK.equals(result.code)) {
            linkService.deleteTaskLinks(task);
            task.setOwner(null);
            task.setAssignee(null);
            task.setProject(null);
            task.setTags(null);
            wlSrv.deleteTaskWorklogs(task);
            Set<Comment> comments = commRepo.findByTaskIdOrderByDateDesc(task.getId());
            commRepo.delete(comments);
        }
        return result;
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

    private ResultData checkTaskCanOperated(Task task, boolean remove) {
        List<Account> accounts = accSrv.findAll();
        StringBuilder accountsWorking = new StringBuilder();
        String separator = "";

        for (Account account : accounts) {
            boolean update = false;
            if (account.getActive_task() != null && account.getActive_task().length > 0
                    && account.getActive_task()[0].equals(task.getId())) {
                if (account.equals(Utils.getCurrentAccount())) {
                    if (remove) {
                        account.clearActive_task();
                    } else {
                        stopTimer(task);
                    }
                    update = true;
                } else {
                    accountsWorking.append(separator);
                    accountsWorking.append(account.toString());
                    separator = ", ";
                }
            }
            if (accountsWorking.length() > 0) {
                return new ResultData(ResultData.ERROR, msg.getMessage("task.changeState.change.working",
                        new Object[]{accountsWorking.toString()}, Utils.getCurrentLocale()));
            }
            if (remove) {
                List<Task> lastVisited = account.getLast_visited_t();
                if (lastVisited.contains(task)) {
                    lastVisited.remove(task);
                    account.setLast_visited_t(lastVisited);
                    update = true;
                }
                if (account.equals(Utils.getCurrentAccount())) {
                    Utils.getCurrentAccount().setLast_visited_t(lastVisited);
                }
            }
            if (update) {
                accSrv.update(account);
            }
        }
        return new ResultData(ResultData.OK, null);
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
            return msg.getMessage("task.state.changed.closed", new Object[]{task.getId()}, Utils.getCurrentLocale());
        } else if (TaskState.CLOSED.equals(oldState)) {
            wlSrv.addActivityLog(task, "", LogType.REOPEN);
            task.setFinishDate(null);
            return msg.getMessage("task.state.changed.reopened", new Object[]{task.getId()},
                    Utils.getCurrentLocale());
        } else {
            wlSrv.changeState(oldState, state, task);
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
        DateTime now = new DateTime();
        Period logWork = new Period((DateTime) account.getActive_task_time(), now);
        // Only log work if greater than 1 minute
        if (logWork.toStandardDuration().getMillis() / 1000 / 60 < 1) {
            logWork = new Period().plusMinutes(1);
        }
        wlSrv.addNormalWorkLog(task, PeriodHelper.outFormat(logWork), logWork, LogType.LOG);
        account.clearActive_task();
        accSrv.update(account);
        return logWork;
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
        List<String> fileNames = new ArrayList<String>();
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

    private void deleteFiles(Task task) {
        File folder = new File(taskSrv.getTaskDirectory(task));
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
        folder.delete();
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
            taskSrv.save(task);
            wlSrv.addActivityLog(task, Utils.changedFromTo(previous, assignee.toString()), LogType.ASSIGNED);
            watchSrv.startWatching(task);
            return true;
        }
        return false;
    }

    /**
     * Nuke Task - set everything to null
     */
    private Task purgeTask(Task task) {
        Task zerotask = new Task();
        zerotask.setId(task.getId());
        return zerotask;
    }
}
