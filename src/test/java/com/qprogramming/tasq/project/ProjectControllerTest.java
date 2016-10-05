package com.qprogramming.tasq.project;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.projects.*;
import com.qprogramming.tasq.projects.holiday.HolidayService;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.*;
import com.qprogramming.tasq.task.worklog.DisplayWorkLog;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLog;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
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
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

    private static final String NEW_DESCRIPTION = "newDescription";
    private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "user@test.com";
    private static final String NEW_EMAIL = "newuser@test.com";
    private static final String NEW_NAME = "newName";
    private static final String NEWUSERNAME = "newUser";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private ProjectController projectCtr;
    @Mock
    private ProjectService projSrv;
    @Mock
    private TaskService taskSrv;
    @Mock
    private AgileService sprintSrvMock;
    @Mock
    private WorkLogService wrkLogSrv;
    @Mock
    private EventsService eventsSrvMock;
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
    private HolidayService holidayServiceMock;
    @Mock
    private LastVisitedService visitedSrvMock;

    @Mock
    private RedirectAttributes raMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        projectCtr = new ProjectController(projSrv, accountServiceMock, taskSrv, sprintSrvMock, wrkLogSrv, msg,
                eventsSrvMock, holidayServiceMock, visitedSrvMock);
    }

    @Test
    public void showDetailsTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
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
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(taskSrv.findByProjectAndOpen(project)).thenReturn(openTaskList);
        projectCtr.showDetails(PROJECT_ID, null, modelMock, raMock);
        verify(modelMock, times(1)).addAttribute("TO_DO", 1);
        verify(modelMock, times(1)).addAttribute("ONGOING", 1);
        verify(modelMock, times(1)).addAttribute("CLOSED", 1);
        verify(modelMock, times(1)).addAttribute("BLOCKED", 0);
        verify(modelMock, times(1)).addAttribute("tasks", openTaskList);
        verify(modelMock, times(1)).addAttribute("project", project);
    }

    @Test
    public void showDetailsNoProjectsTest() {
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.showDetails(PROJECT_ID, null, modelMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));

    }

    @Test
    public void showDetailsBadAuthTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        testAccount.setRole(Roles.ROLE_POWERUSER);
        project.removeParticipant(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        boolean catched = false;
        try {
            projectCtr.showDetails(PROJECT_ID, null, modelMock, raMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);

    }

    @Test
    public void getProjectEventsAndChartTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        Task task = TestUtils.createTask(PROJECT_NAME, 1, project);
        project.addParticipant(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
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
        when(wrkLogSrv.findByProjectId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(wrkLogSrv.findProjectCreateCloseEvents(project, false)).thenReturn(list);
        Pageable p = new PageRequest(0, 5, new Sort(Sort.Direction.ASC, "time"));
        ResponseEntity<Page<DisplayWorkLog>> result = projectCtr.getProjectEvents(PROJECT_ID, p);
        ResponseEntity<ProjectChart> chart = projectCtr.getProjectChart(PROJECT_ID, false, responseMock);
        String today = new LocalDate().toString();
        Assert.assertEquals(Integer.valueOf(1), chart.getBody().getClosed().get(today));
        Assert.assertEquals(Integer.valueOf(5), chart.getBody().getCreated().get(today));
        Assert.assertEquals(8L, result.getBody().getTotalElements());

    }

    @Test
    public void getProjectEventsUnahtorizedTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_POWERUSER);
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setParticipants(new HashSet<Account>());
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        try {
            projectCtr.getProjectEvents(PROJECT_ID, null);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);

    }

    @Test
    public void getProjectEventsNoProjectTest() {
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        Assert.assertNull(projectCtr.getProjectEvents(PROJECT_ID, null));
    }

    @Test
    public void listProjectsTest() {
        List<Project> list = createList(5);
        testAccount.setActiveProject(PROJECT_ID + 3);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(projSrv.findAllByUser()).thenReturn(list);
        when(projSrv.findAll()).thenReturn(list);
        when(msg.getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("MSG");
        projectCtr.listProjects(modelMock);
        testAccount.setRole(Roles.ROLE_ADMIN);
        projectCtr.listProjects(modelMock);
        verify(modelMock, times(2)).addAttribute("projects", list);
        Assert.assertEquals(new Long(4), list.get(0).getId());
    }

    @Test
    public void createProjectSuccessTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        List<Project> list = createList(1);
        Project project = form.createProject();
        Assert.assertEquals(form.getProject_id(), new NewProjectForm(project).getProject_id());
        when(projSrv.findByName(anyString())).thenReturn(null);
        when(projSrv.findAllByUser()).thenReturn(list);
        when(msg.getMessage(anyString(), any(Object[].class), anyString(), any(Locale.class))).thenReturn("MSG");
        when(projSrv.save(any(Project.class))).thenReturn(project);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        projectCtr.createProject(form, errors, raMock, requestMock);
    }

    @Test
    public void createProjectBadAuthTest() {
        boolean catched = false;
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
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
            projectCtr.manageProject(PROJECT_ID, modelMock, raMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
    }

    @Test
    public void createProjectIDLongErrorsTest() {
        NewProjectForm form = createForm(PROJECT_NAME, "TESTTEST");
        Errors errors = new BeanPropertyBindingResult(form, "form");
        projectCtr.createProject(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void createProjectIDDigitsErrorsTest() {
        NewProjectForm form = createForm(PROJECT_NAME, "TEST2");
        Errors errors = new BeanPropertyBindingResult(form, "form");
        projectCtr.createProject(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void createProjectErrorsTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        errors.rejectValue("name", "Error name");
        projectCtr.createProject(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasErrors());
    }

    @Test
    public void createProjectNameExistsTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        Project project = form.createProject();
        when(projSrv.findByName(PROJECT_NAME)).thenReturn(project);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        projectCtr.createProject(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasFieldErrors("name"));
    }

    @Test
    public void createProjectIDExistsTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        Project project = form.createProject();
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        projectCtr.createProject(form, errors, raMock, requestMock);
        Assert.assertTrue(errors.hasFieldErrors("project_id"));
    }

    @Test
    public void projectManageNoProjectTest() {
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.manageProject(PROJECT_ID, modelMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void projectManageTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        Project project = form.createProject();
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.manageProject(PROJECT_ID, modelMock, raMock);
        verify(modelMock, times(1)).addAttribute("project", project);
    }

    @Test
    public void updatePropertiesAuthErrorTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_VIEWER);
        try {
            projectCtr.updateProperties(PROJECT_ID, true, TaskPriority.BLOCKER, TaskType.BUG, 1L, raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
    }

    @Test
    public void updatePropertiesNoProjectTest() {
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.updateProperties(PROJECT_ID, true, TaskPriority.BLOCKER, TaskType.BUG, 1L, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void updatePropertiesActiveSprintTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(sprintSrvMock.findByProjectIdAndActiveTrue(1L)).thenReturn(new Sprint());
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        projectCtr.updateProperties(PROJECT_ID, true, TaskPriority.BLOCKER, TaskType.BUG, 1L, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void updatePropertiesTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(sprintSrvMock.findByProjectIdAndActiveTrue(1L)).thenReturn(null);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        projectCtr.updateProperties(PROJECT_ID, true, TaskPriority.BLOCKER, TaskType.BUG, 1L, raMock, requestMock);
        verify(projSrv, times(1)).save(project);
    }

    @Test
    public void activateProjectFailTest() {
        when(projSrv.activateForCurrentUser(PROJECT_ID)).thenReturn(null);
        projectCtr.activate(PROJECT_ID, requestMock, raMock);
        verify(raMock, never()).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.SUCCESS, new Object[]{}));
    }

    @Test
    public void activateProjectSuccesTest() {
        NewProjectForm form = createForm(PROJECT_NAME, PROJECT_ID);
        Project project = form.createProject();
        when(projSrv.activateForCurrentUser(PROJECT_ID)).thenReturn(project);
        projectCtr.activate(PROJECT_ID, requestMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.SUCCESS, new Object[]{}));
    }

    @Test
    public void addParticipantTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        testAccount.setName("John");
        testAccount.setSurname("Doe");
        project.setId(1L);
        project.addParticipant(testAccount);
        when(accountServiceMock.findByEmail(NEW_EMAIL))
                .thenReturn(new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER));
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.addParticipant(PROJECT_ID, NEW_EMAIL, raMock, requestMock);
        verify(accountServiceMock, times(1)).findByEmail(anyString());
        verify(accountServiceMock, times(1)).update(any(Account.class));
        verify(projSrv, times(1)).save(project);
    }


    @Test
    public void addParticipantAuthErrorTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_VIEWER);
        try {
            projectCtr.addParticipant(PROJECT_ID, "", raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
    }

    @Test
    public void addParticipantNoProjectTest() {
        when(accountServiceMock.findByEmail(NEW_EMAIL))
                .thenReturn(new Account(NEW_EMAIL, PASSWORD, USERNAME, Roles.ROLE_POWERUSER));
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.addParticipant(PROJECT_ID, NEW_EMAIL, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void removeParticipantNoProjectTest() {
        when(accountServiceMock.findById(1L))
                .thenReturn(new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER));
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.removeParticipant(PROJECT_ID, 1L, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void removeParticipantAuthErrorTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_VIEWER);
        try {
            projectCtr.removeParticipant(PROJECT_ID, 1L, raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
    }

    @Test
    public void removeParticipantLastAdminTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.addAdministrator(testAccount);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.removeParticipant(PROJECT_ID, 1L, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void removeParticipantTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        Account newUser = new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_ADMIN);
        project.addAdministrator(newUser);
        project.addParticipant(newUser);
        project.addParticipant(testAccount);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.removeParticipant(PROJECT_ID, 1L, raMock, requestMock);
        verify(projSrv, times(1)).save(project);
    }

    @Test
    public void grantAndRemoveAdminAuthErrorTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_VIEWER);
        try {
            projectCtr.grantAdmin(PROJECT_ID, 1L, raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
        catched = false;
        try {
            projectCtr.removeAdmin(PROJECT_ID, 1L, raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);

    }

    @Test
    public void grantAndRemoveAdminNoProjectTest() {
        when(accountServiceMock.findById(1L))
                .thenReturn(new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER));
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.grantAdmin(PROJECT_ID, 1L, raMock, requestMock);
        projectCtr.removeAdmin(PROJECT_ID, 1L, raMock, requestMock);
        verify(raMock, times(2)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void grantAndRemoveAdminTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.addParticipant(testAccount);
        when(accountServiceMock.findById(1L))
                .thenReturn(new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER));
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.grantAdmin(PROJECT_ID, 1L, raMock, requestMock);
        projectCtr.removeAdmin(PROJECT_ID, 1L, raMock, requestMock);
        verify(projSrv, times(2)).save(project);
    }

    @Test
    public void grantAndRemoveLastAdminTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.addParticipant(testAccount);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        projectCtr.removeAdmin(PROJECT_ID, 1L, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void getDefaultAssigneeTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.setDefaultAssigneeID(1L);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findById(1L)).thenReturn(project);
        Assert.assertNotNull(projectCtr.getDefaults(1L, responseMock));
        project.setDefaultAssigneeID(null);
        Assert.assertNull(projectCtr.getDefaults(1L, responseMock).getBody().getDefaultAssignee());
    }

    @Test
    public void changeDescriptionNoProjectsTest() {
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(null);
        projectCtr.editDescriptions(PROJECT_ID, NEW_NAME, NEW_DESCRIPTION, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void changeDescriptionBadAuthTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.addParticipant(testAccount);
        testAccount.setRole(Roles.ROLE_VIEWER);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        boolean catched = false;
        try {
            projectCtr.editDescriptions(PROJECT_ID, NEW_NAME, NEW_DESCRIPTION, raMock, requestMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        projectCtr.editDescriptions(PROJECT_ID, NEW_NAME, NEW_DESCRIPTION, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void changeDescriptionTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        project.setId(1L);
        project.addParticipant(testAccount);
        when(accountServiceMock.findById(1L)).thenReturn(testAccount);
        when(projSrv.findByProjectId(PROJECT_ID)).thenReturn(project);
        when(projSrv.canEdit(1L)).thenReturn(true);
        projectCtr.editDescriptions(PROJECT_ID, NEW_NAME, NEW_DESCRIPTION, raMock, requestMock);
        Project newProject = project;
        newProject.setDescription(NEW_DESCRIPTION);
        verify(projSrv, times(1)).findByProjectId(PROJECT_ID);
        verify(projSrv, times(1)).save(newProject);
    }

    private List<Project> createList(int count) {
        List<Project> list = new LinkedList<Project>();
        for (int i = 0; i < count; i++) {
            Project project = createForm(PROJECT_NAME + i, PROJECT_ID + i).createProject();
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
}
