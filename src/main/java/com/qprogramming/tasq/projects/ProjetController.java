package com.qprogramming.tasq.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import com.qprogramming.tasq.support.ProjectSorter;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.WorkLogSorter;
import com.qprogramming.tasq.support.web.MessageHelper;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@Controller
public class ProjetController {
	private static final Logger LOG = LoggerFactory
			.getLogger(ProjetController.class);

	@Autowired
	private ProjectService projSrv;

	@Autowired
	private AccountService accSrv;

	@Autowired
	private TaskService taskSrv;

	@Autowired
	private WorkLogService wrkLogSrv;

	@Autowired
	MessageSource msg;

	@RequestMapping(value = "project", method = RequestMethod.GET)
	public String showDetails(@RequestParam(value = "id") Long id,
			@RequestParam(value = "show", required = false) Integer show,
			Model model, RedirectAttributes ra, HttpServletRequest request) {
		Project project = projSrv.findById(id);
		if (project == null) {
			MessageHelper.addErrorAttribute(
					ra,
					msg.getMessage("project.notexists", null,
							Utils.getCurrentLocale()));
			return "redirect:/projects";
		}
		// set last visited
		Account current = Utils.getCurrentAccount();
		List<Project> last_visited = current.getLast_visited_p();
		last_visited.add(0, project);
		if (last_visited.size() > 4) {
			last_visited = last_visited.subList(0, 4);
		}
		List<Project> clean = new ArrayList<Project>();
		HashSet<Project> lookup = new HashSet<Project>();
		for (Project item : last_visited) {
			if (lookup.add(item)) {
				clean.add(item);
			}
		}
		current.setLast_visited_p(clean);
		accSrv.update(current);
		// get latest events for this project
		List<WorkLog> workLogs = wrkLogSrv.getProjectEvents(project);
		Collections.sort(workLogs, new WorkLogSorter(true));
		// Check status of all projects
		List<Task> tasks = project.getTasks();
		Map<TaskState, Integer> state_count = new HashMap<TaskState, Integer>();
		for (TaskState state : TaskState.values()) {
			state_count.put(state, 0);
		}
		for (Task task : tasks) {
			Integer value = state_count.get(task.getState());
			value++;
			state_count.put((TaskState) task.getState(), value);
		}
		show = show == null ? 0 : show;
		show++;
		int begin = (show - 1) * 25;
		int end = show * 25;
		begin = begin < 0 ? 0 : begin;
		end = end > workLogs.size() ? workLogs.size() : end;
		workLogs = workLogs.subList(begin, end);
		model.addAttribute("TO_DO", state_count.get(TaskState.TO_DO));
		model.addAttribute("ONGOING", state_count.get(TaskState.ONGOING));
		model.addAttribute("CLOSED", state_count.get(TaskState.CLOSED));
		model.addAttribute("BLOCKED", state_count.get(TaskState.BLOCKED));
		model.addAttribute("project", project);
		model.addAttribute("events", workLogs);
		return "project/details";
	}

	@RequestMapping(value = "projects", method = RequestMethod.GET)
	public String listProjects(Model model) {
		List<Project> projects = projSrv.findAllByUser();
		Collections.sort(projects, new ProjectSorter(
				ProjectSorter.SORTBY.LAST_VISIT, Utils.getCurrentAccount()
						.getActive_project(), true));
		model.addAttribute("projects", projects);
		return "project/list";
	}

	@RequestMapping(value = "project/activate", method = RequestMethod.GET)
	public String activate(@RequestParam(value = "id") Long id,
			HttpServletRequest request, RedirectAttributes ra) {
		Project activated_proj = projSrv.activate(id);
		if (activated_proj != null) {
			MessageHelper.addSuccessAttribute(ra, msg.getMessage(
					"project.activated",
					new Object[] { activated_proj.getName() },
					Utils.getCurrentLocale()));
		}
		return "redirect:" + request.getHeader("Referer");
	}

	@RequestMapping(value = "project/create", method = RequestMethod.GET)
	public NewProjectForm startProjectcreate() {
		return new NewProjectForm();
	}

	@RequestMapping(value = "project/create", method = RequestMethod.POST)
	public String createProject(
			@Valid @ModelAttribute("newProjectForm") NewProjectForm newProjectForm,
			Errors errors, RedirectAttributes ra, HttpServletRequest request) {
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
			MessageHelper.addErrorAttribute(ra, msg.getMessage(
					"project.exists", new Object[] { name },
					Utils.getCurrentLocale()));
			return "redirect:" + request.getHeader("Referer");
		}
		String project_id = newProjectForm.getProject_id();
		if (null != projSrv.findByProjectId(project_id)) {
			errors.rejectValue("project_id", "project.idunique",
					new Object[] { project_id }, "");
			return null;
		}
		Project new_project = newProjectForm.createProject();
		new_project = projSrv.save(new_project);
		if (projSrv.findAll().size() == 1) {
			Account account = Utils.getCurrentAccount();
			account.setActive_project(new_project.getId());
			accSrv.update(account);
		}
		MessageHelper.addSuccessAttribute(
				ra,
				msg.getMessage("project.created", new Object[] { name },
						Utils.getCurrentLocale()));
		return "redirect:/project?id=" + new_project.getId();
	}

	@RequestMapping(value = "project/manage", method = RequestMethod.GET)
	public String manageProject(@RequestParam(value = "id") Long id,
			Model model, RedirectAttributes ra, HttpServletRequest request) {
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
			@RequestParam(value = "email") String email, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
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
			@RequestParam(value = "project_id") Long project_id,
			@RequestParam(value = "account_id") Long account_id, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
		Account account = accSrv.findById(account_id);
		if (account != null) {
			Project project = projSrv.findById(project_id);
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
			@RequestParam(value = "project_id") Long project_id,
			@RequestParam(value = "account_id") Long account_id, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
		Account account = accSrv.findById(account_id);
		if (account != null) {
			Project project = projSrv.findById(project_id);
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
			@RequestParam(value = "project_id") Long project_id,
			@RequestParam(value = "account_id") Long account_id, Model model,
			RedirectAttributes ra, HttpServletRequest request) {
		Account account = accSrv.findById(account_id);
		if (account != null) {
			Project project = projSrv.findById(project_id);
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

	@RequestMapping(value = "/project/{id}/getParticipants", method = RequestMethod.GET)
	public @ResponseBody
	List<DisplayAccount> listParticipants(@PathVariable Long id,
			@RequestParam String term, HttpServletResponse response) {
		response.setContentType("application/json");
		Project project = projSrv.findById(id);
		Set<Account> all_participants = project.getParticipants();
		List<DisplayAccount> result = new ArrayList<DisplayAccount>();
		for (Account account : all_participants) {
			if (term == null) {
				DisplayAccount s_account = new DisplayAccount(account);
				result.add(s_account);
			} else {
				if (StringUtils.containsIgnoreCase(account.toString(), term)) {
					DisplayAccount s_account = new DisplayAccount(account);
					result.add(s_account);
				}
			}
		}
		return result;
	}

	@Transactional
	@RequestMapping(value = "project/{id}/update", method = RequestMethod.POST)
	public String updateProperties(
			@PathVariable Long id,
			@RequestParam(value = "default_priority", required = false) TaskPriority priority,
			@RequestParam(value = "default_type", required = false) TaskType type,
			Model model, RedirectAttributes ra, HttpServletRequest request) {
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
		projSrv.save(project);
		return "redirect:" + request.getHeader("Referer");
	}
}
