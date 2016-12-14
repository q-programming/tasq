package com.qprogramming.tasq.home;

import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectRepository;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
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
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllersTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
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
    private AppService appSrvMock;
    @Mock
    private LastVisitedService visitedSrvMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private Authentication authMock;
    @Mock
    private Model model;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private SessionLocaleResolver localeResolverMock;
    private Account testAccount;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        projSrv = new ProjectService(projRepoMock, accSrvMock, usrSrvMock);
        homeCtrl = new HomeController(taskSrvMock, projSrv, appSrvMock, eventSrvMock, localeResolverMock);
        homeAdvCtrl = new HomeControllerAdvice(visitedSrvMock);
    }

    @Test
    public void newUserLoggedTest() {
        when(projRepoMock.findByParticipants_Id(1L)).thenReturn(new LinkedList<Project>());
        testAccount.setRole(Roles.ROLE_VIEWER);
        Assert.assertEquals("homeNewUser", homeCtrl.index(testAccount, model));

    }

    @Test
    public void notLoggedTest() {
        when(appSrvMock.getProperty(AppService.DEFAULTLANG)).thenReturn("en");
        Assert.assertEquals("homeNotSignedIn", homeCtrl.index(null, model));

    }

    @Test
    public void userLoggedTest() {
        List<Project> projectList = new LinkedList<Project>();
        Project project = createProject();
        projectList.add(project);
        when(projRepoMock.findByParticipants_Id(anyLong())).thenReturn(projectList);
        when(taskSrvMock.findAllByProject(project)).thenReturn(createTaskList(project));
        Assert.assertEquals("homeSignedIn", homeCtrl.index(testAccount, model));

    }

    @Test
    public void getLastProjectsTest() {
        Project project = createProject();
        Project project2 = createProject(2L, "NEW");
        List<LastVisited> lastVisitedProjects = new LinkedList<>();
        lastVisitedProjects.add(new LastVisited(project, testAccount.getId()));
        lastVisitedProjects.add(new LastVisited(project2, testAccount.getId()));
        testAccount.setActiveProject(project.getProjectId());
        when(accSrvMock.findByUsername(anyString())).thenReturn(testAccount);
        when(visitedSrvMock.getAccountLastProjects(testAccount.getId())).thenReturn(lastVisitedProjects);
        Assert.assertEquals(PROJECT_NAME, homeAdvCtrl.getLastProjects().get(0).getItemName());
    }

    @Test
    public void getLastTasksTest() {
        when(accSrvMock.findByUsername(anyString())).thenReturn(testAccount);
        when(visitedSrvMock.getAccountLastTasks(testAccount.getId())).thenReturn(TestUtils.createLastVisitedTasks(3));
        Assert.assertEquals("TEST-0", homeAdvCtrl.getLastTasks().get(0).getItemId());
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
}
