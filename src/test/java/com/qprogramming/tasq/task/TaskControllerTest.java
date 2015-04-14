package com.qprogramming.tasq.task;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.stylesheets.LinkStyle;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.agile.SprintService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.NewProjectForm;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectChart;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.projects.ProjectController;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.comments.CommentsRepository;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.tag.TagsRepository;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

	private static final String NEW_DESCRIPTION = "newDescription";
	private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
	private static final String PROJ_ID = "TEST";
	private static final String PROJ_NAME = "Test project";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";
	private static final String TASK_NAME = "Task";
	private static final String TASK_DESC = "Task description";
	private Account testAccount;

	private TaskController taskCtr;
	private TaskService taskSrv;

	@Mock
	private ProjectService projSrvMock;
	@Mock
	private TaskRepository taskRepoMock;
	@Mock
	private SprintService sprintSrvMock;
	@Mock
	private WorkLogService wrkLogSrv;
	@Mock
	private CommentsRepository commRepoMock;
	@Mock
	private EventsService eventsSrvMock;
	@Mock
	private TaskLinkService taskLinkSrvMock;
	@Mock
	private AccountService accountServiceMock;
	@Mock
	private TagsRepository tagsRepoMock;
	@Mock
	private WatchedTaskService watchSrvMock;
	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private Authentication authMock;
	@Mock
	private MessageSource msgMock;
	@Mock
	private RedirectAttributes raMock;
	@Mock
	private HttpServletResponse responseMock;
	@Mock
	private HttpServletRequest requestMock;
	@Mock
	private Model modelMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		testAccount = new Account(EMAIL, "", Roles.ROLE_ADMIN);
		testAccount.setLanguage("en");
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("MESSAGE");
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		taskSrv = new TaskService(taskRepoMock);
		taskCtr = new TaskController(taskSrv, projSrvMock, accountServiceMock,
				wrkLogSrv, msgMock, sprintSrvMock, taskLinkSrvMock,
				commRepoMock, tagsRepoMock, watchSrvMock);
	}

	@Test
	public void startCreateTest() {
		testAccount.setRole(Roles.ROLE_VIEWER);
		boolean catched = false;
		try {
			taskCtr.startTaskCreate(modelMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("AuthException not thrown on user roles", catched);
		// Auth valid
		testAccount.setRole(Roles.ROLE_REPORTER);
		catched = false;
		try {
			taskCtr.startTaskCreate(modelMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("AuthException not thrown on no project", catched);
		Project project = createProject(1L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		taskCtr.startTaskCreate(modelMock);
		verify(modelMock, times(1)).addAttribute("project", project);
		verify(modelMock, times(1)).addAttribute("projects_list", list);
	}

	@Test
	public void createTaskErrorsTest() {
		testAccount.setRole(Roles.ROLE_VIEWER);
		boolean catched = false;
		Project project = createProject(1L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		Task task = createTask(TASK_NAME, 1, project);
		TaskForm form = new TaskForm(task);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		try {
			taskCtr.createTask(form, errors, raMock, requestMock, modelMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("AuthException not thrown on user roles", catched);
		testAccount.setRole(Roles.ROLE_USER);
		errors.rejectValue("name", "Error name");
		String result = taskCtr.createTask(form, errors, raMock, requestMock,
				modelMock);
		Assert.assertNull("No errors", result);
	}

	@Test
	public void createTaskCantEditTest() {
		Project project = createProject(1L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		Task task = createTask(TASK_NAME, 1, project);
		TaskForm form = new TaskForm(task);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canEdit(project)).thenReturn(false);
		String result = taskCtr.createTask(form, errors, raMock, requestMock,
				modelMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void createTaskBadEstimateTest() {
		Project project = createProject(1L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		Task task = createTask(TASK_NAME, 1, project);
		TaskForm form = new TaskForm(task);
		form.setStory_points("TEST");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canEdit(project)).thenReturn(true);
		taskCtr.createTask(form, errors, raMock, requestMock, modelMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createTaskRejectAddSprintTest() {
		Sprint sprint = new Sprint();
		sprint.setId(1L);
		sprint.setActive(true);
		Project project = createProject(1L);
		project.setLastTaskNo(0L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		Task task = createTask(TASK_NAME, 1, project);
		TaskForm form = new TaskForm(task);
		form.setAssignee(1L);
		form.setAddToSprint(1L);
		form.setStory_points("");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canEdit(project)).thenReturn(true);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(sprintSrvMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(
				sprint);
		taskCtr.createTask(form, errors, raMock, requestMock, modelMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createTaskTest() {
		Sprint sprint = new Sprint();
		sprint.setId(1L);
		sprint.setActive(true);
		Project project = createProject(1L);
		project.setLastTaskNo(0L);
		Project project2 = createProject(2L);
		List<Project> list = new LinkedList<Project>();
		list.add(project);
		list.add(project2);
		Task task = createTask(TASK_NAME, 1, project);
		TaskForm form = new TaskForm(task);
		form.setAssignee(1L);
		form.setAddToSprint(1L);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		when(projSrvMock.findUserActiveProject()).thenReturn(project);
		when(projSrvMock.findAllByUser()).thenReturn(list);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canEdit(project)).thenReturn(true);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(sprintSrvMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(
				sprint);
		taskCtr.createTask(form, errors, raMock, requestMock, modelMock);
	}

	@Test
	public void startEditNotReporterAndNotOwnerTask() {
		Project project = createProject(1L);
		Task task = createTask(TASK_NAME, 1, project);
		when(taskSrv.findById("TEST-1")).thenReturn(task);
		testAccount.setRole(Roles.ROLE_VIEWER);
		boolean catched = false;
		try {
			taskCtr.startEditTask(PROJ_ID + "-1", modelMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("AuthException not thrown on not reporter", catched);
		testAccount.setRole(Roles.ROLE_REPORTER);
		Account owner = new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER);
		task.setOwner(owner);
		catched = false;
		try {
		taskCtr.startEditTask("TEST-1", modelMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("AuthException not thrown on not owner project", catched);
	}
	@Test
	public void startEditTask() {
		Project project = createProject(1L);
		Task task = createTask(TASK_NAME, 1, project);
		when(taskSrv.findById("TEST-1")).thenReturn(task);
		testAccount.setRole(Roles.ROLE_REPORTER);
		task.setOwner(testAccount);
		taskCtr.startEditTask("TEST-1", modelMock);
		verify(modelMock,times(1)).addAttribute("task", task);
		verify(modelMock,times(1)).addAttribute("project", task.getProject());
	}

	private Project createProject(long id) {
		Project project = new Project();
		project.setName(PROJ_NAME);
		project.setId(id);
		project.setProjectId(PROJ_ID + id);
		return project;
	}

	private Task createTask(String name, int no, Project project) {
		Task task = new Task();
		task.setName(name);
		task.setDescription(TASK_DESC);
		task.setProject(project);
		task.setId(project.getProjectId() + "-" + no);
		task.setPriority(TaskPriority.MAJOR);
		task.setType(TaskType.USER_STORY);
		task.setStory_points(2);
		task.setState(TaskState.TO_DO);
		return task;
	}

}
