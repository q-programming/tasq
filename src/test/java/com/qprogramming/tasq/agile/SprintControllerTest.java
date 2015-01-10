package com.qprogramming.tasq.agile;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.WorkLogService;

@RunWith(MockitoJUnitRunner.class)
public class SprintControllerTest {

	private static final String TEST = "TEST";
	private static final String TEST_PROJ = "Test project";
	private static final String EMAIL = "user@user.com";
	private static final String LAMB = "Lamb";
	private static final String ZOE = "Zoe";
	private static final String ART = "Art";
	private static final String DOE = "Doe";
	private static final String KATE = "Kate";
	private static final String JOHN = "John";
	private static final String ADAM = "Adam";
	private static final String MARRY = "Marry";

	@Mock
	private AccountService accSrvMock;
	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private RedirectAttributes raMock;
	@Mock
	private ProjectService projSrvMock;
	@Mock
	private TaskService taskSrvMock;
	@Mock
	private SprintRepository sprintRepoMock;
	@Mock
	private WorkLogService wrkLogSrv;

	@Mock
	private Authentication authMock;
	@Mock
	private Model modelMock;
	@Mock
	private MessageSource msgMock;
	@Mock
	private HttpServletResponse responseMock;
	@Mock
	private HttpServletRequest requestMock;

	private Account testAccount;
	private Project project;
	private SprintController sprintCtrl;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		testAccount = new Account(EMAIL, "", Roles.ROLE_USER);
		project = new Project();
		project.setName(TEST_PROJ);
		project.setId(1L);
		project.setProjectId(TEST);
		Set<Account> participants = new HashSet<Account>(createList());
		project.setParticipants(participants);
		sprintCtrl = new SprintController(projSrvMock, taskSrvMock,
				sprintRepoMock, wrkLogSrv, msgMock);
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);

	}

	@Test
	public void showNotParticipantTest() {
		boolean catched = false;
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		try {
			sprintCtrl.showBoard(TEST, modelMock, requestMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("TasqAuthException not thrown", catched);
		catched = false;
		try {
			sprintCtrl.showBacklog(TEST, modelMock, requestMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue("TasqAuthException not thrown", catched);

	}

	@Test
	public void showNoActiveSprintTest() {
		project.addParticipant(testAccount);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectIdAndActiveTrue(project.getId()))
				.thenReturn(null);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Assert.assertEquals("redirect:/" + project.getProjectId()
				+ "/scrum/backlog",
				sprintCtrl.showBoard(TEST, modelMock, requestMock, raMock));
		verify(modelMock, times(1)).addAttribute(anyString(), anyObject());
	}

	@Test
	public void showBoardTest() {
		project.addParticipant(testAccount);
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setId(1L);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectIdAndActiveTrue(project.getId()))
				.thenReturn(sprint);
		when(taskSrvMock.findAllBySprint(sprint)).thenReturn(
				createTaskList(project));
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Assert.assertEquals("/scrum/board",
				sprintCtrl.showBoard(TEST, modelMock, requestMock, raMock));
		verify(modelMock, times(3)).addAttribute(anyString(), anyObject());
	}
	
	
	
	
	
	

	private List<Account> createList() {
		List<Account> accountsList = new LinkedList<Account>();
		accountsList.add(createAccount(JOHN, DOE));
		accountsList.add(createAccount(ADAM, ART));
		accountsList.add(createAccount(ADAM, ZOE));
		accountsList.add(createAccount(MARRY, LAMB));
		accountsList.add(createAccount(KATE, DOE));
		return accountsList;
	}

	private Account createAccount(String name, String surname) {
		Account account = new Account(name + "@test.com", "", Roles.ROLE_USER);
		account.setName(name);
		account.setSurname(surname);
		return account;

	}

	private List<Task> createTaskList(Project project) {
		List<Task> taskList = new LinkedList<Task>();
		for (int i = 0; i < 5; i++) {
			taskList.add(createTask("TASK" + i, i, project));
		}
		return taskList;
	}

	private Task createTask(String name, int no, Project project) {
		Task task = new Task();
		task.setName(name);
		task.setProject(project);
		task.setId(project.getProjectId() + "-" + no);
		task.setPriority(TaskPriority.MAJOR);
		task.setType(TaskType.USER_STORY);
		return task;
	}

}
