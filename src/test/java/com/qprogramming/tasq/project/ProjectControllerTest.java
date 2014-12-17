package com.qprogramming.tasq.project;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.SprintRepository;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.NewProjectForm;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.projects.ProjetController;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";

	private ProjetController projectCtr;

	@Mock
	private ProjectService projSrv;
	@Mock
	private AccountService accSrv;
	@Mock
	private TaskService taskSrv;
	@Mock
	private SprintRepository sprintRepo;
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

	private Account testAccount;
	private List<Account> accountsList;

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
		projectCtr = new ProjetController(projSrv, accSrv, taskSrv, sprintRepo,
				wrkLogSrv, msg);
	}

	@Test
	public void createProjectSuccessTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		List<Project> list = createList(1);
		Project project = form.createProject();
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
		NewProjectForm form = createForm("Test project", "TEST");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			projectCtr.createProject(form, errors, raMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("TasqAuthException was not thrown", catched);
		catched=false;
		try {
			projectCtr.manageProject(1L, modelMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("TasqAuthException was not thrown", catched);
	}

	@Test
	public void createProjectIDLongErrorsTest() {
		NewProjectForm form = createForm("Test project", "TESTTEST");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectIDDigitsErrorsTest() {
		NewProjectForm form = createForm("Test project", "TEST2");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectErrorsTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		Errors errors = new BeanPropertyBindingResult(form, "form");
		errors.rejectValue("name", "Error name");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasErrors());
	}

	@Test
	public void createProjectNameExistsTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		Project project = form.createProject();
		when(projSrv.findByName("Test project")).thenReturn(project);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasFieldErrors("name"));
	}

	@Test
	public void createProjectIDExistsTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		Project project = form.createProject();
		when(projSrv.findByProjectId("TEST")).thenReturn(project);
		Errors errors = new BeanPropertyBindingResult(form, "form");
		projectCtr.createProject(form, errors, raMock, requestMock);
		Assert.assertTrue(errors.hasFieldErrors("project_id"));
	}

	@Test
	public void projectManageNoProjectTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		Project project = form.createProject();
		when(projSrv.findById(1L)).thenReturn(null);
		projectCtr.manageProject(1L, modelMock, raMock);
		verify(raMock, times(1))
		.addFlashAttribute(
				anyString(),
				new Message(anyString(), Message.Type.DANGER,
						new Object[] {}));

	}
	@Test
	public void projectManageTest() {
		NewProjectForm form = createForm("Test project", "TEST");
		Project project = form.createProject();
		when(projSrv.findById(1L)).thenReturn(project);
		projectCtr.manageProject(1L, modelMock, raMock);
		verify(modelMock,times(1)).addAttribute("project", project);
	}


	private List<Project> createList(int count) {
		List<Project> list = new LinkedList<Project>();
		for (int i = 0; i < count; i++) {
			list.add(createForm("Test project" + i, "TEST" + 1).createProject());
		}
		return list;
	}

	private NewProjectForm createForm(String name, String id) {
		NewProjectForm form = projectCtr.startProjectcreate();
		form.setProject_id(id);
		form.setName(name);
		form.setDescription("Description");
		form.setAgile_type("SCRUM");
		form.setId(1L);
		return form;
	}

}
