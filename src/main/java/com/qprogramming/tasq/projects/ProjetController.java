package com.qprogramming.tasq.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.agile.SprintService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.ProjectSorter;
import com.qprogramming.tasq.support.sorters.TaskSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class ProjetController {

	private static final Logger LOG = LoggerFactory
			.getLogger(ProjetController.class);
	private ProjectService projSrv;
	private AccountService accSrv;
	private TaskService taskSrv;
	private SprintService sprintSrv;
	private WorkLogService wrkLogSrv;
	private MessageSource msg;

	@Autowired
	public ProjetController(ProjectService projSrv, AccountService accSrv,
			TaskService taskSrv, SprintService sprintSrv,
			WorkLogService wrklSrv, MessageSource msg) {
		this.projSrv = projSrv;
		this.accSrv = accSrv;
		this.taskSrv = taskSrv;
		this.sprintSrv = sprintSrv;
		this.wrkLogSrv = wrklSrv;
		this.msg = msg;
	}

	@Transactional
	@RequestMapping(value = "project", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") Long id,
			@RequestParam(value = "closed", required = false) String closed,
			Model model, RedirectAttributes ra) {
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		if (!project.getParticipants().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg, "role.error.project.permission");
		}
		// set last visited
		Account current = Utils.getCurrentAccount();
		List<Project> lastVisited = current.getLast_visited_p();
		List<Project> clean = new ArrayList<Project>();
		Set<Project> lookup = new HashSet<Project>();
		for (Project item : lastVisited) {
			if (lookup.add(item)) {
				clean.add(item);
			}
		}
		if (clean.size() > 4) {
			clean = clean.subList(0, 4);
		}
		current.setLast_visited_p(clean);
		accSrv.update(current);
		// Check status of all projects
		List<Task> tasks = project.getTasks();
		Map<TaskState, Integer> stateCount = new HashMap<TaskState, Integer>();
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
		model.addAttribute("CLOSED", stateCount.get(TaskState.CLOSED));
		model.addAttribute("BLOCKED", stateCount.get(TaskState.BLOCKED));
		List<Task> taskList = new LinkedList<Task>();
		if (closed == null) {
			taskList = taskSrv.findByProjectAndOpen(project);
		} else {
			taskList = taskSrv.findAllByProject(project);
		}
		Collections.sort(taskList, new TaskSorter(TaskSorter.SORTBY.ID, false));
		// Initilize getRawWorkLog for all task in this project . Otherwise lazy
		// init exception is thrown
		Utils.initializeWorkLogs(taskList);
		model.addAttribute("tasks", taskList);
		model.addAttribute("project", project);
		return "project/details";
	}

	@RequestMapping(value = "projectEvents", method = RequestMethod.GET)
	public @ResponseBody
	Page<DisplayWorkLog> getProjectEvents(
			@RequestParam(value = "id") Long id,
			@PageableDefault(size = 25, page = 0, sort = "time", direction = Direction.DESC) Pageable p) {
		Project project = projSrv.findById(id);
		if (project == null) {
			// NULL
			return null;
		}
		if (!project.getParticipants().contains(Utils.getCurrentAccount())
				&& !Roles.isAdmin()) {
			throw new TasqAuthException(msg, "role.error.project.permission");
		}
		// Fetch events
		Page<WorkLog> page = wrkLogSrv.findByProjectId(id, p);
		List<DisplayWorkLog> list = new LinkedList<DisplayWorkLog>();
		for (WorkLog workLog : page) {
			list.add(new DisplayWorkLog(workLog));
		}
		Page<DisplayWorkLog> result = new PageImpl<DisplayWorkLog>(list, p,
				page.getTotalElements());
		return result;
	}

	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public String listProjects(Model model) {
		List<Project> projects;
		if (Roles.isAdmin()) {
			projects = projSrv.findAll();
		} else {
			projects = projSrv.findAllByUser();
		}
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
						.getActive_project(), true));
		model.addAttribute("projects", projects);
		return "project/list";
	}

	@RequestMapping(value = "project/activate", method = RequestMethod.GET)
	public String activate(@RequestParam(value = "id") Long id,
			HttpServletRequest request, RedirectAttributes ra) {
		Project activatedProj = projSrv.activate(id);
		if (activatedProj != null) {
			MessageHelper.addSuccessAttribute(ra, msg.getMessage(
					"project.activated",
					new Object[] { activatedProj.getName() },
					Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "project/create", method = RequestMethod.GET)
	public NewProjectForm startProjectcreate() {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		return new NewProjectForm();
	}

	@RequestMapping(value = "project/create", method = RequestMethod.POST)
	public String createProject(
			@Valid @ModelAttribute("newProjectForm") NewProjectForm newProjectForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		if (errors.hasErrors()) {
			return null;
		}
		if (newProjectForm.getProject_id().length() > 5) {
			errors.rejectValue("project_id", "project.idValid");
			return null;
		}
		if (newProjectForm.getProject_id().matches(".*\\d.*")) {
			errors.rejectValue("project_id", "project.idValid.letters");
			return null;
		}
		Utils.setHttpRequest(request);
		String name = newProjectForm.getName();
		if (null != projSrv.findByName(name)) {
			errors.rejectValue("name", "project.exists", new Object[] { name },
					"");
			return null;
		}
		String projectId = newProjectForm.getProject_id();
		if (null != projSrv.findByProjectId(projectId)) {
			errors.rejectValue("project_id", "project.idunique",
					new Object[] { projectId }, "");
			return null;
		}
		Project newProject = newProjectForm.createProject();
		newProject = projSrv.save(newProject);
		if (projSrv.findAllByUser().size() == 1) {
			Account account = Utils.getCurrentAccount();
			account.setActive_project(newProject.getId());
			accSrv.update(account);
		}
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("project.created", new Object[] { name },
						Utils.getCurrentLocale()));
		return "redirect:/project?id=" + newProject.getId();
	}

	@RequestMapping(value = "project/manage", method = RequestMethod.GET)
	public String manageProject(@RequestParam(value = "id") Long id,
			Model model, RedirectAttributes ra) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}

		model.addAttribute("project", project);
		return "project/manage";
	}

	@RequestMapping(value = "project/useradd", method = RequestMethod.POST)
	public String addParticipant(@RequestParam(value = "id") Long id,
			@RequestParam(value = "email") String email, RedirectAttributes ra,
			HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Account account = accSrv.findByEmail(email);
		if (account != null) {
			Project project = projSrv.findById(id);
			if (project == null) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("project.notexists", null,
								Utils.getCurrentLocale()));
				return "redirect:/projects";
			}
			project.addParticipant(account);
			if (account.getActive_project() == null) {
				account.setActive_project(id);
				accSrv.update(account);
			}
			projSrv.save(project);
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "project/userRemove", method = RequestMethod.POST)
	public String removeParticipant(
			@RequestParam(value = "project_id") Long projectId,
			@RequestParam(value = "account_id") Long accountId,
			RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Account account = accSrv.findById(accountId);
		if (account != null) {
			Project project = projSrv.findById(projectId);
			if (project == null) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("project.notexists", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			Set<Account> admins = project.getAdministrators();
			if (admins.contains(account)) {
				if (admins.size() == 1) {
					MessageHelper.addErrorAttribute(
							ra,
							msg.getMessage("project.lastAdmin", null,
									Utils.getCurrentLocale()));
					return "redirect:" + request.getHeader("Referer");
				} else {
					project.removeAdministrator(account);
				}

			}
			project.removeParticipant(account);
			projSrv.save(project);
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "project/grantAdmin", method = RequestMethod.POST)
	public String grantAdmin(
			@RequestParam(value = "project_id") Long projectId,
			@RequestParam(value = "account_id") Long accountId,
			RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Account account = accSrv.findById(accountId);
		if (account != null) {
			Project project = projSrv.findById(projectId);
			if (project == null) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("project.notexists", null,
								Utils.getCurrentLocale()));
				return "redirect:/projects";
			}
			project.addAdministrator(account);
			projSrv.save(project);
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@Transactional
	@RequestMapping(value = "project/removeAdmin", method = RequestMethod.POST)
	public String removeAdmin(
			@RequestParam(value = "project_id") Long projectId,
			@RequestParam(value = "account_id") Long accountId,
			RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Account account = accSrv.findById(accountId);
		if (account != null) {
			Project project = projSrv.findById(projectId);
			if (project == null) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("project.notexists", null,
								Utils.getCurrentLocale()));
				return "redirect:/projects";
			}
			if (project.getAdministrators().size() == 1) {
				MessageHelper.addErrorAttribute(
						ra,
						msg.getMessage("project.lastAdmin", null,
								Utils.getCurrentLocale()));
				return "redirect:" + request.getHeader("Referer");
			}
			project.removeAdministrator(account);
			projSrv.save(project);
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "/project/getParticipants", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplayAccount> listParticipants(@RequestParam String id,
			@RequestParam String term, HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findByProjectId(id);
		if (project == null) {
			try {
				Long projectID = Long.valueOf(id);
				project = projSrv.findById(projectID);
			} catch (NumberFormatException e) {
				LOG.error(e.getMessage());
			}
		}
		Set<Account> allParticipants = project.getParticipants();
		List<DisplayAccount> result = new ArrayList<DisplayAccount>();
		for (Account account : allParticipants) {
			if (term == null) {
				DisplayAccount sAccount = new DisplayAccount(account);
				result.add(sAccount);
			} else {
				if (StringUtils.containsIgnoreCase(account.toString(), term)) {
					DisplayAccount sAccount = new DisplayAccount(account);
					result.add(sAccount);
				}
			}
		}
		return result;
	}

	@RequestMapping(value = "/project/getChart", method = RequestMethod.GET)
	public @ResponseBody
	ProjectChart getProjectChart(@RequestParam Long id,
			HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(id);
		Map<String, Integer> created = new HashMap<String, Integer>();
		Map<String, Integer> closed = new HashMap<String, Integer>();
		ProjectChart result = new ProjectChart();
		List<WorkLog> events = wrkLogSrv.findProjectCreateCloseEvents(project);
		// Fill maps
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
		return result;
	}

	@RequestMapping(value = "/project/getDefaultAssignee", method = RequestMethod.GET)
	public @ResponseBody
	DisplayAccount getDefaultAssignee(@RequestParam Long id,
			HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(id);
		Account assignee = accSrv.findById(project.getDefaultAssigneeID());
		if (assignee == null) {
			return null;
		} else {
			return new DisplayAccount(assignee);
		}
	}

	@RequestMapping(value = "/project/getDefaultTaskType", method = RequestMethod.GET)
	public @ResponseBody
	TaskType getDefaultTaskType(@RequestParam Long id,
			HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(id);
		return (TaskType) project.getDefault_type();
	}

	@RequestMapping(value = "/project/getDefaultTaskPriority", method = RequestMethod.GET)
	public @ResponseBody
	TaskPriority getDefaultTaskPriority(@RequestParam Long id,
			HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(id);
		return (TaskPriority) project.getDefault_priority();
	}

	@Transactional
	@RequestMapping(value = "project/{id}/update", method = RequestMethod.POST)
	public String updateProperties(@PathVariable Long id,
			@RequestParam(value = "timeTracked") Boolean timeTracked,
			@RequestParam(value = "default_priority") TaskPriority priority,
			@RequestParam(value = "default_type") TaskType type,
			@RequestParam(value = "defaultAssignee") Long assigneId,
			RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		if (priority != null) {
			project.setDefault_priority(priority);
		}
		project.setDefault_type(type);
		Sprint activeSprint = sprintSrv.findByProjectIdAndActiveTrue(id);
		if (activeSprint != null) {
			MessageHelper.addWarningAttribute(
					ra,
					msg.getMessage("project.sprintActive", null,
							Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		project.setTimeTracked(timeTracked);
		Account account = accSrv.findById(assigneId);
		assigneId = account != null ? account.getId() : null;
		project.setDefaultAssigneeID(assigneId);
		projSrv.save(project);
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "project/{id}/description", method = RequestMethod.POST)
	public String changeDescriptions(@PathVariable Long id,
			@RequestParam(value = "description") String description,
			RedirectAttributes ra, HttpServletRequest request) {
		if (!Roles.isUser()) {
			throw new TasqAuthException(msg);
		}
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		if (!projSrv.canEdit(id)) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("error.accesRights", null,
							Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		project.setDescription(description);
		projSrv.save(project);
		return "redirect:" + request.getHeader("Referer");
	}

}
