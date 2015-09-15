package com.qprogramming.tasq.task.worklog;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.Period;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class WorkLogTest {

	private static final String TEST_1 = "TEST-1";
	private static final String TEST_2 = "TEST-2";
	private static final String EMAIL = "user@test.com";
	private static final String TASK_NAME = "taskName";
	private static final String PROJECT_NAME = "TestProject";
	private static final String PROJECT_ID = "TEST";
	private static final String PROJECT_DESCRIPTION = "Description";

	private Account testAccount;
	private Task task1;
	private Task task2;

	private WorkLogService wlSrv;

	@Mock
	private WorkLogRepository wlRepoMock;
	@Mock
	private ProjectService projSrvMock;
	@Mock
	private EventsService eventSrvMock;
	@Mock
	private TaskService taskSrvMock;
	@Mock
	private WorkLogService wrkLogSrvMock;
	@Mock
	private MessageSource msgMock;
	@Mock
	private AccountService accountServiceMock;
	@Mock
	private MockSecurityContext securityMock;
	@Mock
	private Authentication authMock;
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
		testAccount = new Account(EMAIL, "", "user", Roles.ROLE_ADMIN);
		testAccount.setLanguage("en");
		when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		Project project = createProject();
		task1 = createTask(TASK_NAME, 1, project);
		task2 = createTask(TASK_NAME, 2, project);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
		SecurityContextHolder.setContext(securityMock);
		wlSrv = new WorkLogService(wlRepoMock, taskSrvMock, projSrvMock, eventSrvMock);

	}

	@Test
	public void addTimedWorkLogTest() {
		task1.setRemaining(new Period(2, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addTimedWorkLog(task1, "", new Date(), null, new Period(1, 0, 0, 0), LogType.LOG);
		Assert.assertEquals(1, task1.getRawRemaining().getHours());
		verify(taskSrvMock, times(2)).save(any(Task.class));
		verify(eventSrvMock, times(2)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addTimedWorkLogWithRemainingTest() {
		task1.setRemaining(new Period(3, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addTimedWorkLog(task1, "", new Date(), new Period(1, 0, 0, 0), new Period(1, 0, 0, 0), LogType.LOG);
		Assert.assertEquals(1, task1.getRawRemaining().getHours());
		verify(taskSrvMock, times(2)).save(any(Task.class));
		verify(eventSrvMock, times(2)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addDatedWorkLogTest() {
		task1.setRemaining(new Period(3, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.ESTIMATE, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addDatedWorkLog(task1, null, new DateTime().minusDays(1).toDate(), LogType.ESTIMATE);
		verify(taskSrvMock, times(1)).save(any(Task.class));
		verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addActivityLogTest() {
		task1.setRemaining(new Period(3, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addActivityLog(task1, null, LogType.LOG);
		verify(taskSrvMock, times(1)).save(any(Task.class));
		verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addActivityPeriodLogTest() {
		task1.setRemaining(new Period(3, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.ESTIMATE, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addActivityPeriodLog(task1, "1h", new Period(3, 0, 0, 0), LogType.ESTIMATE);
		verify(taskSrvMock, times(1)).save(any(Task.class));
		verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addNormalTimedWorkLogTest() {
		task1.setRemaining(new Period(3, 0, 0, 0));
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
		when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
		wlSrv.addNormalWorkLog(task1, "", new Period(1, 0, 0, 0), LogType.LOG);
		wlSrv.addNormalWorkLog(task1, "", new Period(1, 0, 0, 0), LogType.ESTIMATE);
		Assert.assertEquals(1, task1.getRawRemaining().getHours());
		verify(taskSrvMock, times(3)).save(any(Task.class));
		verify(eventSrvMock, times(3)).addWatchEvent(any(WorkLog.class), anyString(), any(Date.class));
	}

	@Test
	public void addWorkLogNoTaskTest() {
		wlSrv.addWorkLogNoTask("", createProject(), LogType.SPRINT_START);
		verify(wlRepoMock, times(1)).save(any(WorkLog.class));
	}

	@Test
	public void getProjectEventsTest() {
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(3).toDate());
		WorkLog worklog1 = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog2 = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(1).toDate());
		WorkLog worklog3 = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(1).toDate());
		List<WorkLog> list = new ArrayList<WorkLog>();
		list.add(worklog);
		list.add(worklog1);
		list.add(worklog2);
		list.add(worklog3);
		when(wlRepoMock.findByProjectId(1L)).thenReturn(list);
		List<DisplayWorkLog> result = wlSrv.getProjectEvents(createProject());
		Assert.assertEquals(4, result.size());
	}

	@Test
	public void getSprintEventsTest() {
		Sprint sprint = new Sprint();
		sprint.setId(1L);
		sprint.setSprint_no(1L);
		sprint.setStart_date(new DateTime().minusDays(2).toDate());
		sprint.setEnd_date(new DateTime().plusDays(2).toDate());
		sprint.setProject(createProject());
		task1.setInSprint(true);
		task1.addSprint(sprint);
		task2.setInSprint(false);
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog2 = createWorkLog(task1, LogType.TASKSPRINTADD, null, new DateTime().minusDays(1).toDate());
		WorkLog worklog3 = createWorkLog(task1, LogType.TASKSPRINTREMOVE, null, new DateTime().toDate());
		WorkLog worklog4 = createWorkLog(task2, LogType.CLOSED, null, new DateTime().toDate());
		WorkLog worklog5 = createWorkLog(task1, LogType.EDITED, null, new DateTime().toDate());
		WorkLog worklog6 = createWorkLog(task1, LogType.ESTIMATE, null, new DateTime().toDate());
		WorkLog worklog7 = createWorkLog(task1, LogType.REOPEN, null, new DateTime().toDate());
		WorkLog worklog8 = createWorkLog(task1, LogType.CLOSED, null, new DateTime().toDate());

		List<WorkLog> list = new ArrayList<WorkLog>();
		list.add(worklog);
		list.add(worklog1);
		list.add(worklog2);
		list.add(worklog3);
		list.add(worklog4);
		list.add(worklog5);
		list.add(worklog6);
		list.add(worklog7);
		list.add(worklog8);
		when(wlRepoMock.findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(anyLong(), any(Date.class),
				any(Date.class))).thenReturn(list);
		List<WorkLog> result = wlSrv.getAllSprintEvents(sprint);
		Assert.assertEquals(7, result.size());
	}

	@Test
	public void finProjectCreateCLoseEventsTest() {
		Project project = createProject();
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog2 = createWorkLog(task1, LogType.TASKSPRINTADD, null, new DateTime().minusDays(1).toDate());
		WorkLog worklog3 = createWorkLog(task1, LogType.TASKSPRINTREMOVE, null, new DateTime().toDate());
		WorkLog worklog4 = createWorkLog(task2, LogType.CLOSED, null, new DateTime().toDate());
		WorkLog worklog5 = createWorkLog(task1, LogType.EDITED, null, new DateTime().toDate());
		WorkLog worklog6 = createWorkLog(task1, LogType.ESTIMATE, null, new DateTime().toDate());
		WorkLog worklog7 = createWorkLog(task1, LogType.REOPEN, null, new DateTime().toDate());
		WorkLog worklog8 = createWorkLog(task1, LogType.CLOSED, null, new DateTime().toDate());
		List<WorkLog> list = new ArrayList<WorkLog>();
		list.add(worklog);
		list.add(worklog1);
		list.add(worklog2);
		list.add(worklog3);
		list.add(worklog4);
		list.add(worklog5);
		list.add(worklog6);
		list.add(worklog7);
		list.add(worklog8);
		when(wlRepoMock.findByProjectIdOrderByTimeAsc(anyLong())).thenReturn(list);
		List<WorkLog> result = wlSrv.findProjectCreateCloseEvents(project,false);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void testWorklogs() {
		WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new DateTime().minusDays(2).toDate());
		WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null, new DateTime().minusDays(2).toDate());
		worklog.setTask(task1);
		worklog.setAccount(testAccount);
		Assert.assertNotEquals(worklog, worklog1);

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

	private Project createProject() {
		Project project = new Project(PROJECT_NAME, testAccount);
		project.setDescription(PROJECT_DESCRIPTION);
		project.setProjectId(PROJECT_ID);
		project.setId(1L);
		return project;
	}

	private WorkLog createWorkLog(Task task, LogType type, String msg, Date date) {
		WorkLog wl = new WorkLog();
		wl.setTask(task);
		wl.setProject_id(task.getProject().getId());
		wl.setAccount(Utils.getCurrentAccount());
		wl.setTimeLogged(new Date());
		wl.setTime(new Date());
		wl.setType(type);
		wl.setMessage(msg);
		return wl;
	}

}
