package com.qprogramming.tasq.events;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.config.ResourceService;
import com.qprogramming.tasq.mail.MailMail;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.watched.WatchedTask;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.*;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private EventsService eventsService;
    private EventsController eventsController;
    @Mock
    private ProjectService projSrvMock;
    @Mock
    private EventsRepository eventsRepoMock;
    @Mock
    private MailMail mailerMock;
    @Mock
    private TaskService taskSrvMock;
    @Mock
    private WatchedTaskService watchedTaskSrvMock;
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
    @Mock
    private VelocityEngine velMock;
    @Mock
    private ResourceService resourceMock;
    @Mock
    private AppService appSrvMock;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        testAccount.setId(1L);
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        eventsService = new EventsService(eventsRepoMock, watchedTaskSrvMock, mailerMock, msgMock, velMock,
                resourceMock, appSrvMock);
        eventsController = new EventsController(eventsService, msgMock);
    }

    @Test
    public void getEvetnsPagedTest() {
        Event event = new Event();
        event.setAccount(testAccount);
        event.setDate(new Date());
        event.setLogtype(LogType.COMMENT);
        List<Event> list = new LinkedList<Event>();
        list.add(event);
        Page<Event> page = new PageImpl<Event>(list);
        Pageable pageSpecification = new PageRequest(0, 5);
        when(eventsRepoMock.findByAccountId(testAccount.getId(), pageSpecification)).thenReturn(page);
        Page<DisplayEvent> result = eventsController.eventsPaged(null, pageSpecification);
        Assert.assertTrue(result.hasContent());
    }

    @Test
    public void readEvetnTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        when(eventsRepoMock.findById(1L)).thenReturn(event);
        ResultData result = eventsController.readEvent(1L);
        Assert.assertTrue(result.code.equals(ResultData.Code.OK));
        Assert.assertFalse(event.isUnread());
        verify(eventsRepoMock, times(1)).save(any(Event.class));
    }

    @Test
    public void readAllEvetnTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        Event event1 = new Event();
        event1.setId(2L);
        event1.setUnread(true);
        List<Event> list = new LinkedList<Event>();
        list.add(event);
        list.add(event1);
        when(eventsRepoMock.findByAccountIdOrderByDateDesc(testAccount.getId())).thenReturn(list);
        ResultData result = eventsController.readAllEvents();
        Assert.assertTrue(result.code.equals(ResultData.Code.OK));
        Assert.assertFalse(event.isUnread());
        verify(eventsRepoMock, times(2)).save(any(Event.class));
    }

    @Test
    public void deleteEvetnTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        when(eventsRepoMock.findById(1L)).thenReturn(event);
        ResultData result = eventsController.deleteEvent(1L);
        Assert.assertTrue(result.code.equals(ResultData.Code.OK));
        verify(eventsRepoMock, times(1)).delete(any(Event.class));
    }

    @Test
    public void deleteAllEvetnTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        Event event1 = new Event();
        event1.setId(2L);
        event1.setUnread(true);
        List<Event> list = new LinkedList<Event>();
        list.add(event);
        list.add(event1);
        when(eventsRepoMock.findByAccountIdOrderByDateDesc(testAccount.getId())).thenReturn(list);
        ResultData result = eventsController.deleteAllEvents();
        Assert.assertTrue(result.code.equals(ResultData.Code.OK));
        verify(eventsRepoMock, times(1)).delete(list);
    }

    @Test
    public void eventsTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        event.setAccount(testAccount);
        event.setDate(new Date());
        event.setLogtype(LogType.COMMENT);
        event.setTask(TASK_NAME);
        Event event1 = new Event();
        event1.setId(2L);
        event1.setUnread(true);
        event1.setAccount(testAccount);
        event1.setDate(new Date());
        event1.setLogtype(LogType.COMMENT);
        event1.setTask(TASK_NAME);
        Assert.assertNotEquals(event.hashCode(), event1.hashCode());
        Assert.assertNotEquals(event, event1);
    }

    @Test
    public void getUnreadTest() {
        Event event = new Event();
        event.setId(1L);
        event.setUnread(true);
        event.setAccount(testAccount);
        event.setDate(new Date());
        event.setLogtype(LogType.COMMENT);
        event.setTask(TASK_NAME);
        List<Event> list = new LinkedList<Event>();
        list.add(event);
        when(eventsRepoMock.findByAccountIdAndUnreadTrue(testAccount.getId())).thenReturn(list);
        List<Event> result = eventsService.getUnread();
        Assert.assertFalse(result.isEmpty());

    }

    @Test
    public void addWatchEventTest() {
        Project project = new Project(PROJECT_NAME, testAccount);
        testAccount.setWatchnotification(true);
        Set<Account> watchers = new HashSet<Account>();
        Account newAccount = new Account(EMAIL, "", USERNAME, Roles.ROLE_POWERUSER);
        newAccount.setWatchnotification(true);
        watchers.add(testAccount);
        watchers.add(newAccount);
        WatchedTask watched = new WatchedTask();
        watched.setId(TEST_1);
        watched.setWatchers(watchers);
        watched.setType(TaskType.ISSUE);
        Task task = TestUtils.createTask(TASK_NAME, 1, TestUtils.createProject());
        WorkLog worklog = new WorkLog();
        worklog.setAccount(testAccount);
        worklog.setActivity(new Period());
        worklog.setType(LogType.LOG);
        worklog.setTask(task);
        when(watchedTaskSrvMock.getByTask(TEST_1)).thenReturn(watched);
        Utils.setHttpRequest(requestMock);
        eventsService.addWatchEvent(worklog, "", new Date());
        eventsService.addProjectEvent(newAccount, LogType.ASSIGN_TO_PROJ, project);
        verify(eventsRepoMock, times(2)).save(any(Event.class));
    }

    @Test
    public void addSystemEventTest() {
        Account owner = TestUtils.createAccount("Jon", "Doe");
        owner.setSystemnotification(true);
        Utils.setHttpRequest(requestMock);
        when(resourceMock.getBasicResourceMap()).thenReturn(new HashMap<>());
        eventsService.addSystemEvent(owner, LogType.DELETED, "message", "moreDetails");
        verify(eventsRepoMock, times(1)).save(any(Event.class));
        verify(velMock, times(1)).mergeTemplate(anyString(), anyString(), any(VelocityContext.class), any(StringWriter.class));
        verify(mailerMock, times(1)).sendMail(anyInt(), anyString(), anyString(), anyString(), anyMapOf(String.class, Resource.class));
    }


    // @Test
    // public void addTimedWorkLogTest() {
    // task1.setRemaining(new Period(2, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addTimedWorkLog(task1, "", new Date(), null, new Period(1, 0, 0,
    // 0), LogType.LOG);
    // Assert.assertEquals(1, task1.getRawRemaining().getHours());
    // verify(taskSrvMock, times(2)).save(any(Task.class));
    // verify(eventSrvMock, times(2)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addTimedWorkLogWithRemainingTest() {
    // task1.setRemaining(new Period(3, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addTimedWorkLog(task1, "", new Date(), new Period(1, 0, 0, 0),
    // new Period(1, 0, 0, 0), LogType.LOG);
    // Assert.assertEquals(1, task1.getRawRemaining().getHours());
    // verify(taskSrvMock, times(2)).save(any(Task.class));
    // verify(eventSrvMock, times(2)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addDatedWorkLogTest() {
    // task1.setRemaining(new Period(3, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.ESTIMATE, null,
    // new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addDatedWorkLog(task1, null,
    // new DateTime().minusDays(1).toDate(), LogType.ESTIMATE);
    // verify(taskSrvMock, times(1)).save(any(Task.class));
    // verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addActivityLogTest() {
    // task1.setRemaining(new Period(3, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addActivityLog(task1, null, LogType.LOG);
    // verify(taskSrvMock, times(1)).save(any(Task.class));
    // verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addActivityPeriodLogTest() {
    // task1.setRemaining(new Period(3, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.ESTIMATE, null,
    // new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addActivityPeriodLog(task1, "1h", new Period(3, 0, 0, 0),
    // LogType.ESTIMATE);
    // verify(taskSrvMock, times(1)).save(any(Task.class));
    // verify(eventSrvMock, times(1)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addNormalTimedWorkLogTest() {
    // task1.setRemaining(new Period(3, 0, 0, 0));
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null, new Date());
    // when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
    // when(wlRepoMock.save(any(WorkLog.class))).thenReturn(worklog);
    // wlSrv.addNormalWorkLog(task1, "", new Period(1, 0, 0, 0), LogType.LOG);
    // wlSrv.addNormalWorkLog(task1, "", new Period(1, 0, 0, 0),
    // LogType.ESTIMATE);
    // Assert.assertEquals(1, task1.getRawRemaining().getHours());
    // verify(taskSrvMock, times(3)).save(any(Task.class));
    // verify(eventSrvMock, times(3)).addWatchEvent(any(WorkLog.class),
    // anyString(), any(Date.class));
    // }
    //
    // @Test
    // public void addWorkLogNoTaskTest() {
    // wlSrv.addWorkLogNoTask("", createProject(), LogType.SPRINT_START);
    // verify(wlRepoMock, times(1)).save(any(WorkLog.class));
    // }
    //
    // @Test
    // public void getProjectEventsTest() {
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(3).toDate());
    // WorkLog worklog1 = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog2 = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(1).toDate());
    // WorkLog worklog3 = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(1).toDate());
    // List<WorkLog> list = new ArrayList<WorkLog>();
    // list.add(worklog);
    // list.add(worklog1);
    // list.add(worklog2);
    // list.add(worklog3);
    // when(wlRepoMock.findByProjectId(1L)).thenReturn(list);
    // List<DisplayWorkLog> result = wlSrv.getProjectEvents(createProject());
    // Assert.assertEquals(4, result.size());
    // }
    //
    // @Test
    // public void getSprintEventsTest() {
    // Sprint sprint = new Sprint();
    // sprint.setId(1L);
    // sprint.setSprint_no(1L);
    // sprint.setStart_date(new DateTime().minusDays(2).toDate());
    // sprint.setEnd_date(new DateTime().plusDays(2).toDate());
    // sprint.setProject(createProject());
    // task1.setInSprint(true);
    // task1.addSprint(sprint);
    // task2.setInSprint(false);
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog2 = createWorkLog(task1, LogType.TASKSPRINTADD, null,
    // new DateTime().minusDays(1).toDate());
    // WorkLog worklog3 = createWorkLog(task1, LogType.TASKSPRINTREMOVE, null,
    // new DateTime().toDate());
    // WorkLog worklog4 = createWorkLog(task2, LogType.CLOSED, null,
    // new DateTime().toDate());
    // WorkLog worklog5 = createWorkLog(task1, LogType.EDITED, null,
    // new DateTime().toDate());
    // WorkLog worklog6 = createWorkLog(task1, LogType.ESTIMATE, null,
    // new DateTime().toDate());
    // WorkLog worklog7 = createWorkLog(task1, LogType.REOPEN, null,
    // new DateTime().toDate());
    // WorkLog worklog8 = createWorkLog(task1, LogType.CLOSED, null,
    // new DateTime().toDate());
    //
    // List<WorkLog> list = new ArrayList<WorkLog>();
    // list.add(worklog);
    // list.add(worklog1);
    // list.add(worklog2);
    // list.add(worklog3);
    // list.add(worklog4);
    // list.add(worklog5);
    // list.add(worklog6);
    // list.add(worklog7);
    // list.add(worklog8);
    // when(
    // wlRepoMock
    // .findByProjectIdAndTimeBetweenAndWorklogtaskNotNullOrderByTimeAsc(
    // anyLong(), any(Date.class), any(Date.class)))
    // .thenReturn(list);
    // List<WorkLog> result = wlSrv.getAllSprintEvents(sprint);
    // Assert.assertEquals(7, result.size());
    // }
    //
    // @Test
    // public void finProjectCreateCLoseEventsTest() {
    // Project project = createProject();
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog2 = createWorkLog(task1, LogType.TASKSPRINTADD, null,
    // new DateTime().minusDays(1).toDate());
    // WorkLog worklog3 = createWorkLog(task1, LogType.TASKSPRINTREMOVE, null,
    // new DateTime().toDate());
    // WorkLog worklog4 = createWorkLog(task2, LogType.CLOSED, null,
    // new DateTime().toDate());
    // WorkLog worklog5 = createWorkLog(task1, LogType.EDITED, null,
    // new DateTime().toDate());
    // WorkLog worklog6 = createWorkLog(task1, LogType.ESTIMATE, null,
    // new DateTime().toDate());
    // WorkLog worklog7 = createWorkLog(task1, LogType.REOPEN, null,
    // new DateTime().toDate());
    // WorkLog worklog8 = createWorkLog(task1, LogType.CLOSED, null,
    // new DateTime().toDate());
    // List<WorkLog> list = new ArrayList<WorkLog>();
    // list.add(worklog);
    // list.add(worklog1);
    // list.add(worklog2);
    // list.add(worklog3);
    // list.add(worklog4);
    // list.add(worklog5);
    // list.add(worklog6);
    // list.add(worklog7);
    // list.add(worklog8);
    // when(wlRepoMock.findByProjectIdOrderByTimeAsc(anyLong())).thenReturn(
    // list);
    // List<WorkLog> result = wlSrv.findProjectCreateCloseEvents(project);
    // Assert.assertEquals(3, result.size());
    // }
    //
    // @Test
    // public void testWorklogs() {
    // WorkLog worklog = createWorkLog(task1, LogType.LOG, null,
    // new DateTime().minusDays(2).toDate());
    // WorkLog worklog1 = createWorkLog(task1, LogType.DELETED, null,
    // new DateTime().minusDays(2).toDate());
    // worklog.setTask(task1);
    // worklog.setAccount(testAccount);
    // Assert.assertNotEquals(worklog, worklog1);
    //
    // }


}
