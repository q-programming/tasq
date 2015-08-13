package com.qprogramming.tasq.agile;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:/project.properties")
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
	private ReleaseRepository releaseRepoMock;
	@Mock
	private WorkLogService wrkLogSrvMock;

	@Autowired
	PeriodHelper periodHelper;

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
		// ReflectionTestUtils.setField(PeriodHelper.class, "hours", 8);
		testAccount = new Account(EMAIL, "", Roles.ROLE_POWERUSER);
		project = new Project();
		project.setName(TEST_PROJ);
		project.setId(1L);
		project.setProjectId(TEST);
		Set<Account> participants = new HashSet<Account>(createList());
		project.setParticipants(participants);
		AgileService sprintSrv = new AgileService(sprintRepoMock,
				releaseRepoMock);
		sprintCtrl = new SprintController(projSrvMock, taskSrvMock, sprintSrv,
				wrkLogSrvMock, msgMock);
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
		when(projSrvMock.canEdit(project)).thenReturn(true);
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
		when(projSrvMock.canEdit(project)).thenReturn(true);
		when(sprintRepoMock.findByProjectIdAndActiveTrue(project.getId()))
				.thenReturn(sprint);
		when(taskSrvMock.findAllBySprint(sprint)).thenReturn(
				createTaskList(project));
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Assert.assertEquals("/scrum/board",
				sprintCtrl.showBoard(TEST, modelMock, requestMock, raMock));
		verify(modelMock, times(4)).addAttribute(anyString(), anyObject());
	}

	@Test
	public void showBacklogTest() {
		project.addParticipant(testAccount);
		List<Sprint> sprintList = createSprints();
		List<Task> taskList = createTaskList(project);
		taskList.get(0).addSprint(sprintList.get(1));
		taskList.get(1).addSprint(sprintList.get(1));
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(projSrvMock.canEdit(project)).thenReturn(true);
		when(sprintRepoMock.findByProjectIdAndFinished(project.getId(), false))
				.thenReturn(sprintList);
		when(taskSrvMock.findByProjectAndOpen(project)).thenReturn(taskList);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Assert.assertEquals("/scrum/backlog",
				sprintCtrl.showBacklog(TEST, modelMock, requestMock));
		verify(modelMock, times(4)).addAttribute(anyString(), anyObject());
	}

	@Test
	public void createSprintNotAdminAuthTest() {
		boolean catched = false;
		project.setAdministrators(new HashSet<Account>());
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		testAccount.setRole(Roles.ROLE_VIEWER);
		try {
			sprintCtrl.createSprint(TEST, modelMock, requestMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(catched);
	}

	@Test
	public void createSprintTest() {
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_ADMIN);
		List<Sprint> sprintList = createSprints();
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectId(1L)).thenReturn(sprintList);
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		sprintCtrl.createSprint(TEST, modelMock, requestMock, raMock);
		verify(sprintRepoMock, times(1)).save(any(Sprint.class));
	}

	@Test
	public void assignToSprintBadAuthTest() {
		boolean catched = false;
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_POWERUSER);
		when(sprintRepoMock.findById(1L)).thenReturn(null);
		Task task = createTask(TEST, 1, project);
		when(taskSrvMock.findById(TEST)).thenReturn(task);
		try {
			sprintCtrl.assignSprint(TEST, 1L, requestMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(catched);
	}

	@Test
	public void assignToSprintTest() {
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		testAccount.setRole(Roles.ROLE_ADMIN);
		project.setAdministrators(new HashSet<Account>());
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		when(sprintRepoMock.findById(1L)).thenReturn(sprint);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Task task = createTask(TEST, 1, project);
		task.setStory_points(1);
		Task resultTask = task;

		resultTask.addSprint(sprint);
		when(taskSrvMock.findById(TEST)).thenReturn(task);
		ResultData result = sprintCtrl.assignSprint(TEST, 1L, requestMock,
				raMock);
		Assert.assertEquals(ResultData.OK, result.code);
		verify(taskSrvMock, times(1)).save(resultTask);
		// Add to active sprint
		sprint.setActive(true);
		sprintCtrl.assignSprint(TEST, 1L, requestMock, raMock);
		verify(wrkLogSrvMock, times(1)).addActivityLog(task, "",
				LogType.TASKSPRINTADD);
		// Task not esstimated when adding to active
		task.setEstimated(true);
		task.setStory_points(0);
		result = sprintCtrl.assignSprint(TEST, 1L, requestMock, raMock);
		Assert.assertEquals(ResultData.WARNING, result.code);
	}

	@Test
	public void deleteSprintBadAuthTest() {
		boolean catched = false;
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_POWERUSER);
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		when(sprintRepoMock.findById(1L)).thenReturn(sprint);
		try {
			sprintCtrl.deleteSprint(1L, modelMock, requestMock, raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(catched);
	}

	@Test
	public void deleteSprintTest() {
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_ADMIN);
		project.addParticipant(testAccount);
		List<Sprint> sprintList = createSprints();
		List<Task> taskList = createTaskList(project);
		Sprint removedSprint = sprintList.get(1);
		taskList.get(0).addSprint(removedSprint);
		taskList.get(1).addSprint(removedSprint);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		when(sprintRepoMock.findById(1l)).thenReturn(sprintList.get(1));
		when(taskSrvMock.findAllBySprint(removedSprint)).thenReturn(taskList);
		sprintCtrl.deleteSprint(1L, modelMock, requestMock, raMock);
		verify(taskSrvMock, times(2)).save(any(Task.class));
	}

	@Test
	public void removeSprintBadAuthTest() {
		boolean catched = false;
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_POWERUSER);
		Task task = createTask(TEST, 1, project);
		when(taskSrvMock.findById(TEST)).thenReturn(task);
		try {
			sprintCtrl.removeFromSprint(TEST, 1L, modelMock, requestMock,
					raMock);
		} catch (TasqAuthException e) {
			catched = true;
		}
		Assert.assertTrue(catched);

	}

	@Test
	public void removeSprintTest() {
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		testAccount.setRole(Roles.ROLE_ADMIN);
		project.setAdministrators(new HashSet<Account>());
		when(sprintRepoMock.findById(1L)).thenReturn(sprint);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		Task task = createTask(TEST, 1, project);
		task.setStory_points(1);
		Task resultTask = task;
		task.addSprint(sprint);
		when(taskSrvMock.findById(TEST)).thenReturn(task);
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		ResultData result = sprintCtrl.removeFromSprint(TEST, 1L, modelMock,
				requestMock, raMock);
		Assert.assertEquals(ResultData.OK, result.code);
		verify(taskSrvMock, times(1)).save(resultTask);
		// remove from active sprint
		sprint.setActive(true);
		sprintCtrl.removeFromSprint(TEST, 1L, modelMock, requestMock, raMock);
		verify(wrkLogSrvMock, times(1)).addActivityLog(task, null,
				LogType.TASKSPRINTREMOVE);
	}

	@Test
	public void startSprintEndDateOverlapsTest() {
		Sprint sprint = createSingleSprint();
		LocalDate startDate = new LocalDate(2015, 1, 1);
		LocalDate endDate = startDate.plusDays(7);
		sprint.setStart_date(startDate.toDate());
		sprint.setEnd_date(endDate.toDate());
		sprint.setSprint_no(1L);
		List<Sprint> list = new LinkedList<Sprint>();
		list.add(sprint);
		when(sprintRepoMock.findByProjectId(1L)).thenReturn(list);
		when(projSrvMock.canAdminister(any(Project.class))).thenReturn(true);
		ResultData result = sprintCtrl.startSprint(2L, 1L, "06-01-2015",
				"12-01-2015", "12:25", "23:15");
		Assert.assertEquals(ResultData.WARNING, result.code);

	}

	@Test
	public void startSprintTest() {
		project.setAdministrators(new HashSet<Account>());
		Sprint sprint = createSingleSprint();
		Sprint sprint2 = new Sprint();
		sprint2.setProject(project);
		sprint2.setId(2L);
		LocalDate startDate = new LocalDate(2015, 1, 10);
		LocalDate endDate = startDate.plusDays(7);
		sprint2.setStart_date(startDate.toDate());
		sprint2.setEnd_date(endDate.toDate());
		sprint2.setSprint_no(2L);
		List<Sprint> list = new LinkedList<Sprint>();
		list.add(sprint);
		list.add(sprint2);
		List<Task> taskList = createTaskList(project);
		for (Task task : taskList) {
			task.addSprint(sprint2);
		}
		taskList.get(0).setState(TaskState.ONGOING);
		when(sprintRepoMock.findByProjectId(1L)).thenReturn(list);
		when(sprintRepoMock.findById(2L)).thenReturn(sprint2);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		when(sprintRepoMock.findByProjectIdAndActiveTrue(1L)).thenReturn(null);
		when(taskSrvMock.findAllBySprint(sprint2)).thenReturn(taskList);
		testAccount.setRole(Roles.ROLE_POWERUSER);
		ResultData result = sprintCtrl.startSprint(2L, 1L, "06-02-2015",
				"12-02-2015", "12:25", "23:15");
		Assert.assertEquals(ResultData.ERROR, result.code);
		testAccount.setRole(Roles.ROLE_ADMIN);
		result = sprintCtrl.startSprint(2L, 1L, "06-02-2015", "12-02-2015",
				"12:25", "23:15");
		Assert.assertEquals(ResultData.OK, result.code);
		verify(sprintRepoMock, times(1)).save(any(Sprint.class));
		verify(wrkLogSrvMock, times(1)).addWorkLogNoTask(null, project,
				LogType.SPRINT_START);
		taskList.get(1).setStory_points(0);
		result = sprintCtrl.startSprint(2L, 1L, "06-02-2015", "12-02-2015",
				"12:25", "23:15");
		Assert.assertEquals(ResultData.WARNING, result.code);
	}

	@Test
	public void finishSprint() {
		project.setAdministrators(new HashSet<Account>());
		testAccount.setRole(Roles.ROLE_ADMIN);
		Sprint sprint = createSingleSprint();
		List<Task> taskList = createTaskList(project);
		for (Task task : taskList) {
			task.addSprint(sprint);
		}
		taskList.get(0).setState(TaskState.CLOSED);
		taskList.get(1).setState(TaskState.ONGOING);
		taskList.get(2).setState(TaskState.BLOCKED);
		when(sprintRepoMock.findById(1L)).thenReturn(sprint);
		when(projSrvMock.findById(1L)).thenReturn(project);
		when(projSrvMock.canAdminister(project)).thenReturn(true);
		when(taskSrvMock.findAllBySprint(sprint)).thenReturn(taskList);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		sprintCtrl.finishSprint(1L, requestMock, raMock);
		verify(taskSrvMock, times(5)).save(any(Task.class));
		verify(wrkLogSrvMock, times(1)).addWorkLogNoTask(null, project,
				LogType.SPRINT_STOP);
		verify(sprintRepoMock, times(1)).save(sprint);
	}

	@Test
	public void showBurnDownNoActiveTest() {
		Sprint sprint = createSingleSprint();
		when(sprintRepoMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(
				sprint);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		sprintCtrl.showBurndown(TEST, 1L, modelMock, raMock);
		sprint.setEnd_date(null);
		sprint.setActive(false);
		sprintCtrl.showBurndown(TEST, 1L, modelMock, raMock);
		verify(raMock, times(2))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));
	}

	@Test
	public void showBurnDownNotStartedTest() {
		Sprint sprint = createSingleSprint();
		sprint.setStart_date(null);
		List<Sprint> list = new LinkedList<Sprint>();
		list.add(sprint);
		when(sprintRepoMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(
				sprint);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectId(1L)).thenReturn(list);
		sprintCtrl.showBurndown(TEST, 1L, modelMock, raMock);

	}

	@Test
	public void showBurnDownTest() {
		Sprint sprint = createSingleSprint();
		List<Sprint> list = new LinkedList<Sprint>();
		list.add(sprint);
		when(sprintRepoMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(
				sprint);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectId(1L)).thenReturn(list);
		sprintCtrl.showBurndown(TEST, 1L, modelMock, raMock);
	}

	@Test
	public void checkIfActiveTest() {
		when(sprintRepoMock.findById(1L)).thenReturn(createSingleSprint());
		Assert.assertTrue(sprintCtrl.checkIfActive(1L, responseMock));
	}

	@Test
	public void getSprintsTest() {
		Sprint sprint = createSingleSprint();
		sprint.setActive(false);
		List<Sprint> list = new LinkedList<Sprint>();
		list.add(sprint);
		when(sprintRepoMock.findByProjectIdAndFinished(1L, false)).thenReturn(
				list);
		List<DisplaySprint> result = sprintCtrl.showProjectSprints(1L,
				responseMock);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void showBurdownChartNotStartedTest() {
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectIdAndSprintNo(project.getId(), 1L))
				.thenReturn(null);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		SprintData result = sprintCtrl.showBurndownChart(TEST, 1L);
		Assert.assertNotNull(result.getMessage());
	}

	@Test
	public void showBurdownChartTimedTest() {
		project.setTimeTracked(true);
		Sprint sprint = createSingleSprint();
		sprint.setStart_date(new LocalDate().minusDays(5).toDate());
		sprint.setEnd_date(new LocalDate().plusDays(5).toDate());
		sprint.setTotalEstimate(new Period(10, 0, 0, 0));
		Task task = createTask(TEST, 1, project);
		task.setEstimate(new Period(10, 0, 0, 0));
		WorkLog wrk = new WorkLog();
		wrk.setActivity(new Period(1, 0, 0, 0));
		wrk.setTask(task);
		wrk.setTime(new LocalDate().minusDays(2).toDate());
		wrk.setTimeLogged(wrk.getRawTime());
		wrk.setAccount(testAccount);
		List<WorkLog> workLogs = new LinkedList<WorkLog>();
		workLogs.add(wrk);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectIdAndSprintNo(project.getId(), 1L))
				.thenReturn(sprint);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		when(wrkLogSrvMock.getAllSprintEvents(sprint)).thenReturn(workLogs);
		SprintData result = sprintCtrl.showBurndownChart(TEST, 1L);
		Assert.assertNull(result.getMessage());
	}

	@Test
	public void showBurdownChartTest() {
		Sprint sprint = createSingleSprint();
		sprint.setStart_date(new LocalDate().minusDays(5).toDate());
		sprint.setEnd_date(new LocalDate().plusDays(5).toDate());
		sprint.setTotalStoryPoints(4);
		Task task = createTask(TEST, 1, project);
		task.setEstimate(new Period(10, 0, 0, 0));
		task.setState(TaskState.CLOSED);
		WorkLog wrk = new WorkLog();
		wrk.setActivity(new Period(1, 0, 0, 0));
		wrk.setTask(task);
		wrk.setTime(new LocalDate().minusDays(2).toDate());
		wrk.setTimeLogged(wrk.getRawTime());
		wrk.setAccount(testAccount);
		WorkLog wl = new WorkLog();
		wl.setTask(task);
		wl.setAccount(testAccount);
		wl.setTime(new LocalDate().minusDays(1).toDate());
		wl.setTimeLogged(wl.getRawTime());
		wl.setType(LogType.CLOSED);
		Task task2 = createTask(TEST, 2, project);
		WorkLog wl2 = new WorkLog();
		wl2.setTask(task2);
		wl2.setAccount(testAccount);
		wl2.setTime(new LocalDate().minusDays(1).toDate());
		wl2.setTimeLogged(wl2.getRawTime());
		wl2.setType(LogType.TASKSPRINTADD);
		List<WorkLog> workLogs = new LinkedList<WorkLog>();
		workLogs.add(wrk);
		workLogs.add(wl);
		workLogs.add(wl2);
		when(projSrvMock.findByProjectId(TEST)).thenReturn(project);
		when(sprintRepoMock.findByProjectIdAndSprintNo(project.getId(), 1L))
				.thenReturn(sprint);
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("TEST");
		when(wrkLogSrvMock.getAllSprintEvents(sprint)).thenReturn(workLogs);
		SprintData result = sprintCtrl.showBurndownChart(TEST, 1L);
		Assert.assertNull(result.getMessage());
	}

	@Test
	public void equalsSprintTest() {
		Sprint sprint = createSingleSprint();
		Sprint sprint2 = createSingleSprint();
		DisplaySprint ds = new DisplaySprint(sprint);
		DisplaySprint ds2 = new DisplaySprint(sprint2);
		Assert.assertEquals(ds, ds2);
		Assert.assertEquals(ds.hashCode(), ds2.hashCode());
		Assert.assertEquals(ds.getName(), ds2.getName());
		Assert.assertEquals(sprint, sprint2);
		ds.setSprintNo(2L);
		Assert.assertEquals(1, ds.compareTo(ds2));

	}

	private Sprint createSingleSprint() {
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setId(1L);
		sprint.setSprint_no(1L);
		LocalDate startDate = new LocalDate(2015, 1, 1);
		LocalDate endDate = startDate.plusDays(7);
		sprint.setStart_date(startDate.toDate());
		sprint.setEnd_date(endDate.toDate());
		sprint.setSprint_no(1L);
		sprint.setActive(true);
		return sprint;
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
		Account account = new Account(name + "@test.com", "", Roles.ROLE_POWERUSER);
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
		task.setStory_points(2);
		task.setState(TaskState.TO_DO);
		return task;
	}

	private List<Sprint> createSprints() {
		Sprint sprint = new Sprint();
		sprint.setProject(project);
		sprint.setId(1L);
		LocalDate startDate = new LocalDate();
		startDate = startDate.minusDays(7);
		LocalDate endDate = new LocalDate();
		endDate = endDate.minusDays(3);
		sprint.setStart_date(startDate.toDate());
		sprint.setEnd_date(endDate.toDate());
		sprint.setSprint_no(1L);
		Sprint sprint2 = new Sprint();
		sprint2.setProject(project);
		sprint2.setId(2L);
		sprint2.setStart_date(endDate.plusDays(1).toDate());
		sprint2.setSprint_no(2L);
		sprint2.setActive(true);
		List<Sprint> sprintList = new LinkedList<Sprint>();
		sprintList.add(sprint);
		sprintList.add(sprint2);
		return sprintList;
	}

}
