package com.qprogramming.tasq.agile;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.joda.time.DateTime;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.qprogramming.tasq.test.TestUtils.PROJECT_ID;
import static com.qprogramming.tasq.test.TestUtils.createAccountList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@PropertySource("classpath:/project.properties")
public class KanbanControllerTest {

    private static final String LAMB = "Lamb";
    private static final String ZOE = "Zoe";
    private static final String ART = "Art";
    private static final String DOE = "Doe";
    private static final String KATE = "Kate";
    private static final String JOHN = "John";
    private static final String ADAM = "Adam";
    private static final String MARRY = "Marry";
    private static final String RELEASE = "1.0.0";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    PeriodHelper periodHelper;
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
    private AgileService agileSrvMock;
    @Mock
    private WorkLogService wrkLogSrvMock;
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
    private KanbanController kanbanCtrl;

    @Before
    public void setUp() {
        // ReflectionTestUtils.setField(PeriodHelper.class, "hours", 8);
        testAccount = TestUtils.createAccount();
        project = TestUtils.createProject();
        Set<Account> participants = new HashSet<>(createAccountList());
        project.setParticipants(participants);
        kanbanCtrl = new KanbanController(taskSrvMock, projSrvMock, wrkLogSrvMock, msgMock, agileSrvMock);
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);

    }

    @Test
    public void notParticipantTest() {
        boolean catched = false;
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        try {
            kanbanCtrl.showBoard(PROJECT_ID, modelMock, requestMock, raMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue("TasqAuthException not thrown", catched);
        catched = false;
        try {
            kanbanCtrl.newRelease(PROJECT_ID, "1.0.0", null, requestMock, raMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(catched);
    }

    @Test
    public void showBoardTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(taskSrvMock.findAllWithoutRelease(project)).thenReturn(createTaskList(project));
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("TEST");
        Assert.assertEquals("/kanban/board", kanbanCtrl.showBoard(PROJECT_ID, modelMock, requestMock, raMock));
        verify(modelMock, times(3)).addAttribute(anyString(), anyObject());
    }

    @Test
    public void newReleaseNoClosedTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canAdminister(project)).thenReturn(true);
        when(taskSrvMock.findAllToRelease(project)).thenReturn(new LinkedList<Task>());
        kanbanCtrl.newRelease(PROJECT_ID, RELEASE, null, requestMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void newReleaseNotUniqueTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canAdminister(project)).thenReturn(true);
        when(agileSrvMock.findByProjectIdAndRelease(1L, RELEASE)).thenReturn(new Release(project, RELEASE, null));
        kanbanCtrl.newRelease(PROJECT_ID, RELEASE, null, requestMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void newReleaseTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canAdminister(project)).thenReturn(true);
        Release release = new Release(project, RELEASE, null);
        when(agileSrvMock.save(release)).thenReturn(release);
        List<Task> taskList = createTaskList(project);
        taskList.get(0).setState(TaskState.CLOSED);
        taskList.get(1).setState(TaskState.CLOSED);
        taskList.get(4).setState(TaskState.CLOSED);
        when(taskSrvMock.findAllToRelease(project)).thenReturn(taskList);
        kanbanCtrl.newRelease(PROJECT_ID, RELEASE, null, requestMock, raMock);
        Assert.assertTrue(taskList.get(0).getRelease() != null);
        Assert.assertTrue(taskList.get(1).getRelease() != null);
        Assert.assertTrue(taskList.get(4).getRelease() != null);
        Assert.assertEquals(taskList.get(0).getRelease(), taskList.get(1).getRelease());
    }

    @Test
    public void showProjectReleasesTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findById(1L)).thenReturn(project);
        Release release = new Release(project, RELEASE, null);
        List<Release> list = new LinkedList<Release>();
        list.add(release);
        when(agileSrvMock.findReleaseByProjectIdOrderByDateDesc(project.getId())).thenReturn(list);
        ResponseEntity<List<Release>> result = kanbanCtrl.showProjectReleases(project.getId(), responseMock);
        Assert.assertFalse(result.getBody().isEmpty());
    }

    @Test
    public void getReportTest() {
        project.addParticipant(testAccount);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        Release release = new Release(project, RELEASE, null);
        List<Release> releaseList = new LinkedList<Release>();
        releaseList.add(release);
        when(agileSrvMock.findReleaseByProjectIdOrderByDateDesc(project.getId())).thenReturn(releaseList);
        kanbanCtrl.showReport(PROJECT_ID, null, modelMock, requestMock, raMock);
        verify(modelMock, times(2)).addAttribute(anyString(), anyObject());
    }

    @Test
    public void showChartTest() {
        project.setStartDate(new LocalDate().minusDays(4).toDate());
        Release release = new Release(project, RELEASE, null);
        Task task = TestUtils.createTask(PROJECT_ID, 1, project);
        task.setRelease(release);
        WorkLog wl0 = new WorkLog();
        wl0.setTask(task);
        wl0.setAccount(testAccount);
        wl0.setTime(new LocalDate().minusDays(3).toDate());
        wl0.setTimeLogged(wl0.getRawTime());
        wl0.setType(LogType.CREATE);
        WorkLog wl1 = new WorkLog();
        wl1.setActivity(new Period(1, 0, 0, 0));
        wl1.setTask(task);
        wl1.setTime(new LocalDate().minusDays(2).toDate());
        wl1.setTimeLogged(wl1.getRawTime());
        wl1.setAccount(testAccount);
        WorkLog wl2 = new WorkLog();
        wl2.setTask(task);
        wl2.setAccount(testAccount);
        wl2.setTime(new LocalDate().minusDays(1).toDate());
        wl2.setTimeLogged(wl2.getRawTime());
        wl2.setType(LogType.CLOSED);
        task.setState(TaskState.CLOSED);
        task.setFinishDate(new LocalDate().minusDays(1).toDate());
        Task task2 = TestUtils.createTask(PROJECT_ID, 2, project);
        task2.setRelease(release);
        WorkLog wl3 = new WorkLog();
        wl3.setTask(task2);
        wl3.setAccount(testAccount);
        wl3.setTime(new LocalDate().minusDays(1).toDate());
        wl3.setTimeLogged(wl3.getRawTime());
        wl3.setType(LogType.REOPEN);
        List<WorkLog> workLogs = new LinkedList<WorkLog>();
        workLogs.add(wl0);
        workLogs.add(wl1);
        workLogs.add(wl2);
        workLogs.add(wl3);
        List<Task> taskList = new LinkedList<Task>();
        taskList.add(task);
        taskList.add(task2);
        List<LocalDate> freeDays = new LinkedList<>();
        freeDays.add(new LocalDate().minusDays(2));
        when(projSrvMock.getFreeDays(any(Project.class), any(DateTime.class), any(DateTime.class))).thenReturn(freeDays);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(agileSrvMock.findByProjectIdAndRelease(1L, RELEASE)).thenReturn(release);
        when(wrkLogSrvMock.getAllReleaseEvents(release)).thenReturn(workLogs);
        when(taskSrvMock.findAllByRelease(release)).thenReturn(taskList);
        when(taskSrvMock.findAllByRelease(null)).thenReturn(taskList);
        KanbanData data = kanbanCtrl.showBurndownChart(PROJECT_ID, RELEASE).getBody();
        Assert.assertNotNull(data.getClosed());
        Assert.assertNotNull(data.getOpen());
        Assert.assertNotNull(data.getTimeBurned());
        data = kanbanCtrl.showBurndownChart(PROJECT_ID, null).getBody();
        Assert.assertNotNull(data.getClosed());
        Assert.assertNotNull(data.getOpen());
        Assert.assertNotNull(data.getTimeBurned());
    }

    @Test
    public void checkReleases() {
        Release release = new Release(project, RELEASE, null);
        release.setId(1L);
        Release release2 = new Release(project, RELEASE, null);
        release2.setId(2L);
        Assert.assertNotEquals(release, release2);
        Assert.assertNotEquals(release.hashCode(), release2.hashCode());
    }

    private List<Task> createTaskList(Project project) {
        List<Task> taskList = new LinkedList<Task>();
        for (int i = 0; i < 5; i++) {
            taskList.add(TestUtils.createTask("TASK" + i, i, project));
        }
        return taskList;
    }
}
