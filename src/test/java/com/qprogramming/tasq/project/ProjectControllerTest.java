package com.qprogramming.tasq.project;

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

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.DisplayAccount;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.agile.SprintService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.NewProjectForm;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectChart;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.projects.ProjetController;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

	private static final String NEW_DESCRIPTION = "newDescription";
	private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
	private static final String PROJ_ID = "TEST";
	private static final String PROJ_NAME = "Test project";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";
	private Account testAccount;

	private ProjetController projectCtr;

	@Mock
	private ProjectService projSrv;
	@Mock
	private TaskService taskSrv;
	@Mock
	private SprintService sprintSrvMock;
	@Mock
	private WorkLogService wrkLogSrv;
	@Mock
	private MessageSource msg;
	@Mock
	private AccountService accountServiceMock;
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
		projectCtr = new ProjetController(projSrv, accountServiceMock, taskSrv,
				sprintSrvMock, wrkLogSrv, msg);
	}

	@Test
	public void showDetailsTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		List<Task> taskList = new LinkedList<Task>();
		List<Task> openTaskList = new LinkedList<Task>();
		Task task = new Task();
		task.setId("TEST-1");
		task.setState(TaskState.TO_DO);
		Task task2 = new Task();
		task2.setState(TaskState.ONGOING);
		task2.setId("TEST-2");
		Task task3 = new Task();
		task3.setState(TaskState.CLOSED);
		task3.setId("TEST-3");
		taskList.add(task);
		taskList.add(task2);
		taskList.add(task3);
		openTaskList.add(task);
		openTaskList.add(task2);

		project.setTasks(taskList);
		when(projSrv.findById(1L)).thenReturn(project);
		when(taskSrv.findByProjectAndOpen(project)).thenReturn(openTaskList);
		projectCtr.showDetails(1L, null, modelMock, raMock);
		verify(modelMock, times(1)).addAttribute("TO_DO", 1);
		verify(modelMock, times(1)).addAttribute("ONGOING", 1);
		verify(modelMock, times(1)).addAttribute("CLOSED", 1);
		verify(modelMock, times(1)).addAttribute("BLOCKED", 0);
		verify(modelMock, times(1)).addAttribute("tasks", openTaskList);
		verify(modelMock, times(1)).addAttribute("project", project);
	}

	@Test
	public void showDetailsNoProjectsTest() {
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.showDetails(1L, null, modelMock, raMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));

	}

	@Test
	public void showDetailsBadAuthTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		testAccount.setRole(Roles.ROLE_USER);
		project.removeParticipant(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		boolean catched = false;
		try {
			projectCtr.showDetails(1L, null, modelMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);

	}

	@Test
	public void getProjectEventsAndChartTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		Task task = createTask(PROJ_NAME, 1, project);
		project.addParticipant(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		List<WorkLog> list = new LinkedList<WorkLog>();
		WorkLog wl = new WorkLog();
		wl.setAccount(testAccount);
		wl.setType(LogType.CREATE);
		wl.setMessage("msg");
		wl.setTime(new Date());
		wl.setTimeLogged(new Date());
		wl.setTask(task);
		WorkLog w2 = new WorkLog();
		w2.setAccount(testAccount);
		w2.setType(LogType.CLOSED);
		w2.setMessage("msg");
		w2.setTime(new Date());
		w2.setTimeLogged(new Date());
		w2.setTask(task);
		WorkLog w3 = new WorkLog();
		w3.setAccount(testAccount);
		w3.setType(LogType.REOPEN);
		w3.setMessage("msg");
		w3.setTime(new Date());
		w3.setTimeLogged(new Date());
		w3.setTask(task);
		WorkLog w4 = new WorkLog();
		w4.setAccount(testAccount);
		w4.setType(LogType.CLOSED);
		w4.setMessage("msg");
		w4.setTime(new Date());
		w4.setTimeLogged(new Date());
		w4.setTask(task);
		list.add(wl);
		list.add(wl);
		list.add(wl);
		list.add(wl);
		list.add(wl);
		list.add(w2);
		list.add(w3);
		list.add(w4);
		Page<WorkLog> page = new PageImpl<WorkLog>(list);
		when(wrkLogSrv.findByProjectId(anyLong(), any(Pageable.class)))
				.thenReturn(page);
		when(wrkLogSrv.findProjectCreateCloseEvents(project)).thenReturn(list);
		Pageable p = new PageRequest(0, 5, new Sort(Sort.Direction.ASC, "time"));
		Page<DisplayWorkLog> result = projectCtr.getProjectEvents(1L, p);
		ProjectChart chart = projectCtr.getProjectChart(1L, responseMock);
		String today = new LocalDate().toString();
		Assert.assertEquals(Integer.valueOf(1), chart.getClosed().get(today));
		Assert.assertEquals(Integer.valueOf(5), chart.getCreated().get(today));
		Assert.assertEquals(8L, result.getTotalElements());

	}

	@Test
	public void getProjectEventsUnahtorizedTest() {
		boolean catched = false;
		testAccount.setRole(Roles.ROLE_USER);
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setParticipants(new HashSet<Account>());
		when(projSrv.findById(1L)).thenReturn(project);
		try {
			projectCtr.getProjectEvents(1L, null);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);

	}

	@Test
	public void getProjectEventsNoProjectTest() {
		when(projSrv.findById(1L)).thenReturn(null);
		Assert.assertNull(projectCtr.getProjectEvents(1L, null));
	}

	@Test
	public void listProjectsTest() {
		List<Project> list = createList(5);
		testAccount.setActive_project(3L);
		testAccount.setRole(Roles.ROLE_USER);
		when(projSrv.findAllByUser()).thenReturn(list);
		when(projSrv.findAll()).thenReturn(list);
		when(
				msg.getMessage(anyString(), any(Object[].class), anyString(),
						any(Locale.class))).thenReturn("MSG");
		projectCtr.listProjects(modelMock);
		testAccount.setRole(Roles.ROLE_ADMIN);
		projectCtr.listProjects(modelMock);
		verify(modelMock, times(2)).addAttribute("projects", list);
		Assert.assertEquals(new Long(3), list.get(0).getId());
	}

	@Test
	public void createProjectSuccessTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		List<Project> list = createList(1);
		Project project = form.createProject();
		Assert.assertEquals(form.getProject_id(),
				new NewProjectForm(project).getProject_id());
		when(projSrv.findByName(anyString())).thenReturn(null);
		when(projSrv.findAllByUser()).thenReturn(list);
		when(
				msg.getMessage(anyString(), any(Object[].class), anyString(),
						any(Locale.class))).thenReturn("MSG");
		when(projSrv.save(any(Project.class))).thenReturn(project);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
	}

	@Test
	public void createProjectBadAuthTest() {
		boolean catched = false;
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.createProject(form, errors, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
		catched = false;
		try {
			projectCtr.manageProject(1L, modelMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
	}

	@Test
	public void createProjectIDLongErrorsTest() {
		NewProjectForm form = createForm(PROJ_NAME, "TESTTEST");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectIDDigitsErrorsTest() {
		NewProjectForm form = createForm(PROJ_NAME, "TEST2");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectErrorsTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		errors.rejectValue("name", "Error name");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectNameExistsTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Project project = form.createProject();
		when(projSrv.findByName(PROJ_NAME)).thenReturn(project);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasFieldErrors("name"));
	}

	@Test
	public void createProjectIDExistsTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Project project = form.createProject();
		when(projSrv.findByProjectId(PROJ_ID)).thenReturn(project);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasFieldErrors("project_id"));
	}

	@Test
	public void projectManageNoProjectTest() {
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.manageProject(1L, modelMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void projectManageTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Project project = form.createProject();
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.manageProject(1L, modelMock, raMock);
		verify(modelMock, times(1)).addAttribute("project", project);
	}

	@Test
	public void updatePropertiesAuthErrorTest() {
		boolean catched = false;
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.updateProperties(1L, true, TaskPriority.BLOCKER,
					TaskType.BUG, 1L, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
	}

	@Test
	public void updatePropertiesNoProjectTest() {
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.updateProperties(1L, true, TaskPriority.BLOCKER,
				TaskType.BUG, 1L, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void updatePropertiesActiveSprintTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		when(projSrv.findById(1L)).thenReturn(project);
		when(sprintSrvMock.findByProjectIdAndActiveTrue(1L)).thenReturn(
				new Sprint());
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		projectCtr.updateProperties(1L, true, TaskPriority.BLOCKER,
				TaskType.BUG, 1L, raMock, requestMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));
	}

	@Test
	public void updatePropertiesTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		when(projSrv.findById(1L)).thenReturn(project);
		when(sprintSrvMock.findByProjectIdAndActiveTrue(1L)).thenReturn(null);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		projectCtr.updateProperties(1L, true, TaskPriority.BLOCKER,
				TaskType.BUG, 1L, raMock, requestMock);
		verify(projSrv, times(1)).save(project);
	}

	@Test
	public void activateProjectFailTest() {
		when(projSrv.activate(1L)).thenReturn(null);
		projectCtr.activate(1L, requestMock, raMock);
		verify(raMock, never())
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.SUCCESS,
								new Object[] {}));
	}

	@Test
	public void activateProjectSuccesTest() {
		NewProjectForm form = createForm(PROJ_NAME, PROJ_ID);
		Project project = form.createProject();
		when(projSrv.activate(1L)).thenReturn(project);
		projectCtr.activate(1L, requestMock, raMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.SUCCESS,
								new Object[] {}));
	}

	@Test
	public void addParticipantAuthErrorTest() {
		boolean catched = false;
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.addParticipant(1L, "", raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
	}

	@Test
	public void addParticipantTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		testAccount.setName("John");
		testAccount.setSurname("Doe");
		project.setId(1L);
		project.addParticipant(testAccount);
		when(accountServiceMock.findByEmail(NEW_EMAIL)).thenReturn(
				new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER));
		when(projSrv.findById(1L)).thenReturn(project);
		when(projSrv.findByProjectId(PROJ_ID)).thenReturn(project);
		projectCtr.addParticipant(1L, NEW_EMAIL, raMock, requestMock);
		verify(accountServiceMock, times(1)).update(any(Account.class));
		verify(projSrv, times(1)).save(project);
		List<DisplayAccount> result = projectCtr.listParticipants(PROJ_ID, null,
				responseMock);
		Assert.assertEquals(2, result.size());
		result = projectCtr.listParticipants(PROJ_ID, "Jo", responseMock);
		Assert.assertEquals(1, result.size());
		result = projectCtr.listParticipants("1", "Jo", responseMock);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void addParticipantNoProjectTest() {
		when(accountServiceMock.findByEmail(NEW_EMAIL)).thenReturn(
				new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER));
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.addParticipant(1L, NEW_EMAIL, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void removeParticipantNoProjectTest() {
		when(accountServiceMock.findById(1L)).thenReturn(
				new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER));
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.removeParticipant(1L, 1L, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void removeParticipantAuthErrorTest() {
		boolean catched = false;
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.removeParticipant(1L, 1L, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
	}

	@Test
	public void removeParticipantLastAdminTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.addAdministrator(testAccount);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.removeParticipant(1L, 1L, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void removeParticipantTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		Account newUser = new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_ADMIN);
		project.addAdministrator(newUser);
		project.addParticipant(newUser);
		project.addParticipant(testAccount);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.removeParticipant(1L, 1L, raMock, requestMock);
		verify(projSrv, times(1)).save(project);
	}

	@Test
	public void grantAndRemoveAdminAuthErrorTest() {
		boolean catched = false;
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.grantAdmin(1L, 1L, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
		catched = false;
		try {
			projectCtr.removeAdmin(1L, 1L, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);

	}

	@Test
	public void grantAndRemoveAdminNoProjectTest() {
		when(accountServiceMock.findById(1L)).thenReturn(
				new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER));
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.grantAdmin(1L, 1L, raMock, requestMock);
		projectCtr.removeAdmin(1L, 1L, raMock, requestMock);
		verify(raMock, times(2)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void grantAndRemoveAdminTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.addParticipant(testAccount);
		when(accountServiceMock.findById(1L)).thenReturn(
				new Account(NEW_EMAIL, PASSWORD, Roles.ROLE_USER));
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.grantAdmin(1L, 1L, raMock, requestMock);
		projectCtr.removeAdmin(1L, 1L, raMock, requestMock);
		verify(projSrv, times(2)).save(project);
	}

	@Test
	public void grantAndRemoveLastAdminTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.addParticipant(testAccount);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.removeAdmin(1L, 1L, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void getDefaultAssigneeTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.setDefaultAssigneeID(1L);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		Assert.assertNotNull(projectCtr.getDefaultAssignee(1L, responseMock));
		project.setDefaultAssigneeID(null);
		Assert.assertNull(projectCtr.getDefaultAssignee(1L, responseMock));
	}
	
	@Test
	public void changeDescriptionNoProjectsTest() {
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.changeDescriptions(1L, NEW_DESCRIPTION, raMock, requestMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));
	}
	
	@Test
	public void changeDescriptionBadAuthTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.addParticipant(testAccount);
		testAccount.setRole(Roles.ROLE_VIEWER);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		boolean catched=false;
		try{
		projectCtr.changeDescriptions(1L, NEW_DESCRIPTION, raMock, requestMock);
		}catch (TasqAuthException e){
			catched = true;
		}
		Assert.assertTrue(TASQ_AUTH_MSG, catched);
		testAccount.setRole(Roles.ROLE_USER);
		projectCtr.changeDescriptions(1L, NEW_DESCRIPTION, raMock, requestMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}
	@Test
	public void changeDescriptionTest() {
		Project project = createForm(PROJ_NAME, PROJ_ID).createProject();
		project.setId(1L);
		project.addParticipant(testAccount);
		when(accountServiceMock.findById(1L)).thenReturn(testAccount);
		when(projSrv.findById(1L)).thenReturn(project);
		when(projSrv.canEdit(1L)).thenReturn(true);
		projectCtr.changeDescriptions(1L, NEW_DESCRIPTION, raMock, requestMock);
		Project newProject = project;
		newProject.setDescription(NEW_DESCRIPTION);
		verify(projSrv, times(1)).findById(1L);
		verify(projSrv, times(1)).save(newProject);
	}
	

	private List<Project> createList(int count) {
		List<Project> list = new LinkedList<Project>();
		for (int i = 0; i < count; i++) {
			Project project = createForm(PROJ_NAME + i, PROJ_ID + 1)
					.createProject();
			project.setId(new Long(i + 1));
			list.add(project);
		}
		return list;
	}

	private NewProjectForm createForm(String name, String projid) {
		NewProjectForm form = projectCtr.startProjectcreate();
		form.setProject_id(projid);
		form.setName(name);
		form.setDescription("Description");
		form.setAgile("SCRUM");
		form.setId(1L);
		return form;
	}
	
	private Task createTask(String name, int no, Project project) {
		Task task = new Task();
		task.setName(name);
		task.setProject(project);
		task.setId(project.getProjectId() + "-" + no);
		task.setPriority(TaskPriority.MAJOR);
		task.setType(TaskType.USER_STORY);
		task.setStory_points(2);
		task.setState(TaskState.TO_DO);
		return task;
	}

}
