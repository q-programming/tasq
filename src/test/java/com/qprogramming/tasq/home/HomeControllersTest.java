package com.qprogramming.tasq.home;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.account.UserService;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.home.HomeController;
import com.qprogramming.tasq.home.HomeControllerAdvice;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectRepository;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskType;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllersTest {

	private static final String TEST = "TEST";

	private static final String EMAIL = "user@test.com";

	private HomeController homeCtrl;
	private HomeControllerAdvice homeAdvCtrl;
	private ProjectService projSrv;

	@Mock
	private ProjectRepository projRepoMock;

	@Mock
	private AccountService accSrvMock;
	
	@Mock
	private UserService usrSrvMock;
	
	@Mock
	private EventsService eventSrvMock;

	@Mock
	private TaskService taskSrvMock;

	@Mock
	private MockSecurityContext securityMock;

	@Mock
	private Authentication authMock;

	@Mock
	private Model model;

	@Mock
	private HttpServletRequest requestMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount;

	@Before
	public void setUp() {
		testAccount = new Account(EMAIL, "", Roles.ROLE_USER);
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		projSrv = new ProjectService(projRepoMock, accSrvMock,usrSrvMock);
		homeCtrl = new HomeController(taskSrvMock, projSrv);
		homeAdvCtrl = new HomeControllerAdvice(accSrvMock,eventSrvMock);
	}

	@Test
	public void newUserLoggedTest() {
		when(projRepoMock.findByParticipants_Id(1L)).thenReturn(
				new LinkedList<Project>());
		testAccount.setRole(Roles.ROLE_VIEWER);
		Assert.assertEquals("homeNewUser", homeCtrl.index(testAccount, model));

	}

	@Test
	public void notLoggedTest() {
		Assert.assertEquals("homeNotSignedIn", homeCtrl.index(null, model));

	}

	@Test
	public void userLoggedTest() {
		List<Project> projectList = new LinkedList<Project>();
		Project project = createProject(TEST);
		projectList.add(project);
		when(projRepoMock.findByParticipants_Id(anyLong())).thenReturn(
				projectList);
		when(taskSrvMock.findAllByProject(project)).thenReturn(
				createTaskList(project));
		Assert.assertEquals("homeSignedIn", homeCtrl.index(testAccount, model));

	}

	@Test
	public void getLastProjectsTest() {
		Project project = createProject(TEST);
		project.setId(2L);
		testAccount.setActive_project(project.getId());
		List<Project> projectList = new LinkedList<Project>();
		projectList.add(project);
		projectList.add(createProject("NEW"));
		testAccount.setLast_visited_p(projectList);
		when(accSrvMock.findByEmail(anyString())).thenReturn(testAccount);
		Assert.assertEquals(TEST, homeAdvCtrl.getLastProjects().get(0)
				.getProjectId());
	}

	@Test
	public void getLastTasksTest() {
		Project project = createProject(TEST);
		testAccount.setLast_visited_t(createTaskList(project));
		when(accSrvMock.findByEmail(anyString())).thenReturn(testAccount);
		Assert.assertEquals("TASK0", homeAdvCtrl.getLastTasks().get(0)
				.getName());
	}

	private List<Task> createTaskList(Project project) {
		List<Task> taskList = new LinkedList<Task>();
		for (int i = 0; i < 5; i++) {
			taskList.add(createTask("TASK" + i, i, project));
		}
		Task myTask = createTask("MINE", 6, project);
		myTask.setAssignee(testAccount);
		taskList.add(myTask);
		Task dueTask = createTask("MINE", 6, project);
		DateTime dt = new DateTime();
		dt.plusDays(1);
		dueTask.setDue_date(dt.toDate());
		taskList.add(dueTask);
		return taskList;
	}

	private Task createTask(String name, int no, Project project) {
		Task task = new Task();
		task.setName(name);
		task.setId(project.getProjectId() + "-" + no);
		task.setPriority(TaskPriority.MAJOR);
		task.setType(TaskType.USER_STORY);
		return task;
	}

	private Project createProject(String name) {
		Project project = new Project();
		project.setId(1L);
		project.setProjectId(name);
		return project;
	}

}
