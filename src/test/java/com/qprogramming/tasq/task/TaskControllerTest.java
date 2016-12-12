package com.qprogramming.tasq.task;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.LastVisitedService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.agile.AgileService;
import com.qprogramming.tasq.agile.Sprint;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.events.EventsService;
import com.qprogramming.tasq.manage.AppService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.support.PeriodHelper;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.comments.Comment;
import com.qprogramming.tasq.task.comments.CommentService;
import com.qprogramming.tasq.task.link.TaskLinkService;
import com.qprogramming.tasq.task.tag.TagsRepository;
import com.qprogramming.tasq.task.watched.WatchedTaskService;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.TaskResolution;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    private static final String NEW_DESCRIPTION = "newDescription";
    private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
    private static final String NEW_EMAIL = "newuser@test.com";
    private static final String NEWUSERNAME = "newuser";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private TaskController taskCtr;
    private TaskService taskSrv;
    @Mock
    private ProjectService projSrvMock;
    @Mock
    private TaskRepository taskRepoMock;
    @Mock
    private AgileService sprintSrvMock;
    @Mock
    private WorkLogService wrkLogSrv;
    @Mock
    private AppService appSrv;
    @Mock
    private CommentService commentSrvMock;
    @Mock
    private EventsService eventsSrvMock;
    @Mock
    private TaskLinkService taskLinkSrvMock;
    @Mock
    private AccountService accountServiceMock;
    @Mock
    private TagsRepository tagsRepoMock;
    @Mock
    private WatchedTaskService watchSrvMock;
    @Mock
    private EventsService eventSrvMock;
    @Mock
    private LastVisitedService visitedSrvMock;
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
    @Mock
    private EntityManager entityManagerMock;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        taskSrv = new TaskService(taskRepoMock, appSrv, sprintSrvMock, accountServiceMock, msgMock, wrkLogSrv, commentSrvMock, taskLinkSrvMock, watchSrvMock);
        taskCtr = new TaskController(taskSrv, projSrvMock, accountServiceMock, wrkLogSrv, msgMock, sprintSrvMock,
                taskLinkSrvMock, commentSrvMock, tagsRepoMock, watchSrvMock, eventSrvMock, visitedSrvMock) {
            @Override
            protected EntityManager getEntitymanager() {
                return entityManagerMock;
            }
        };
    }

    @Test
    public void startCreateTest() {
        testAccount.setRole(Roles.ROLE_VIEWER);
        boolean catched = false;
        try {
            taskCtr.startTaskCreate(modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue("AuthException not thrown on user roles", catched);
        // Auth valid
        testAccount.setRole(Roles.ROLE_USER);
        catched = false;
        try {
            taskCtr.startTaskCreate(modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue("AuthException not thrown on no project", catched);
        Project project = createProject(1L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        taskCtr.startTaskCreate(modelMock);
        verify(modelMock, times(1)).addAttribute("project", project);
        verify(modelMock, times(1)).addAttribute("projects_list", list);
    }

    @Test
    public void createTaskErrorsTest() {
        testAccount.setRole(Roles.ROLE_VIEWER);
        boolean catched = false;
        Project project = createProject(1L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        try {
            taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue("AuthException not thrown on user roles", catched);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        errors.rejectValue("name", "Error name");
        String result = taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
        Assert.assertNull("No errors", result);
    }

    @Test
    public void createTaskCantEditTest() {
        Project project = createProject(1L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(false);
        taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void createTaskBadEstimateTest() {
        Project project = createProject(1L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        form.setEstimate("TEST");
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void createTaskRejectAddSprintTest() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setActive(true);
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        Task task = createTask(TASK_NAME, 1, project);
        task.setStory_points(null);
        task.setEstimated(true);
        TaskForm form = new TaskForm(task);
        form.setAssignee(testAccount.getEmail());
        form.setAddToSprint(1L);
        form.setStory_points("");
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(accountServiceMock.findByEmail(testAccount.getEmail())).thenReturn(testAccount);
        when(taskRepoMock.save(any(Task.class))).thenReturn(task);
        when(sprintSrvMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(sprint);
        taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
        assertTrue(errors.hasErrors());
    }

    @Test
    public void createTaskTest() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setActive(true);
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Project project2 = createProject(2L);
        List<Project> list = new LinkedList<Project>();
        list.add(project);
        list.add(project2);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        form.setAssignee(testAccount.getEmail());
        form.setAddToSprint(1L);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(projSrvMock.findAllByUser()).thenReturn(list);
        when(projSrvMock.findById(1L)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(accountServiceMock.findByEmail(testAccount.getEmail())).thenReturn(testAccount);
        when(taskRepoMock.save(any(Task.class))).thenReturn(task);
        when(sprintSrvMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(sprint);
        taskCtr.createTask(form, errors, null, raMock, requestMock, modelMock);
    }

    @Test
    public void startEditNotReporterAndNotOwnerTask() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        when(taskSrv.findById(TEST_1)).thenReturn(task);
        testAccount.setRole(Roles.ROLE_VIEWER);
        boolean catched = false;
        try {
            taskCtr.startEditTask(PROJECT_ID + "-1", modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue("AuthException not thrown on not reporter", catched);
        testAccount.setRole(Roles.ROLE_USER);
        Account owner = new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER);
        task.setOwner(owner);
        catched = false;
        try {
            taskCtr.startEditTask(TEST_1, modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue("AuthException not thrown on not owner project", catched);
    }

    @Test
    public void startEditTask() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        when(taskSrv.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(any(Project.class))).thenReturn(true);
        testAccount.setRole(Roles.ROLE_USER);
        task.setOwner(testAccount);
        taskCtr.startEditTask(TEST_1, modelMock);
        verify(modelMock, times(1)).addAttribute("task", task);
        verify(modelMock, times(1)).addAttribute("project", task.getProject());
    }

    @Test
    public void editTaskErrorsTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        errors.rejectValue("name", "Error name");
        when(requestMock.getHeader("Referer")).thenReturn("test");
        when(taskSrv.findById(TEST_1)).thenReturn(task);
        String result = taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        Assert.assertNull(result);
    }

    @Test
    public void editTaskNoTaskTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(requestMock.getHeader("Referer")).thenReturn("test");
        String result = taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        Assert.assertEquals("redirect:test", result);
    }

    @Test
    public void editTaskBadAuthTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        Account owner = new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER);
        task.setOwner(owner);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(false);
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void editTaskClosedTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setState(TaskState.CLOSED);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        Account owner = new Account(NEW_EMAIL, PASSWORD, USERNAME, Roles.ROLE_POWERUSER);
        task.setOwner(owner);
        TaskForm form = new TaskForm(task);
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void editTaskTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setInSprint(true);
        task.setEstimated(true);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        Account owner = new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER);
        task.setOwner(owner);
        TaskForm form = new TaskForm(task);
        form.setName(TASK_NAME + "#");
        form.setDescription(NEW_DESCRIPTION);
        form.setEstimate("1d");
        form.setDue_date("01-05-2015");
        form.setStory_points("3");
        Task editedTask = form.createTask();
        editedTask.setProject(project);
        editedTask.setId(task.getId());
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(sprintSrvMock.taskInActiveSprint(task)).thenReturn(true);
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        verify(wrkLogSrv, times(1)).addActivityPeriodLog(any(Task.class), anyString(), any(Period.class),
                any(LogType.class));
        verify(wrkLogSrv, times(2)).addActivityLog(any(Task.class), anyString(), any(LogType.class));
    }

    @Test
    public void editTaskInvalidSPTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        TaskForm form = new TaskForm(task);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        //Negative
        form.setStory_points("-3");
        BindingResult errors = new BeanPropertyBindingResult(form, "form");
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        assertNotNull(errors.getFieldError("story_points"));
        assertTrue(errors.hasErrors());
        //wrong value
        form.setStory_points("6");
        errors = new BeanPropertyBindingResult(form, "form");
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        assertNotNull(errors.getFieldError("story_points"));
        assertTrue(errors.hasErrors());
        //too large value
        form.setStory_points("106");
        errors = new BeanPropertyBindingResult(form, "form");
        taskCtr.editTask(form, errors, raMock, requestMock, modelMock);
        assertNotNull(errors.getFieldError("story_points"));
        assertTrue(errors.hasErrors());
    }

    @Test
    public void changePointsTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        ResponseEntity<ResultData> responseEntity = taskCtr.changeStoryPoints(TEST_1, 3);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().code, ResultData.OK);
    }

    @Test
    public void changePointsInvalidPointTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        ResponseEntity<ResultData> responseEntity = taskCtr.changeStoryPoints(TEST_1, 6);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().code, ResultData.ERROR);
    }

    @Test(expected = TasqAuthException.class)
    public void changePointsCannotEditTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task = createTask(TASK_NAME, 1, project);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(false);
        ResponseEntity<ResultData> responseEntity = taskCtr.changeStoryPoints(TEST_1, 3);
        fail("Exception was not thrown");
    }


    @Test
    public void showTaskDetailsNoTaskTest() {
        taskCtr.showTaskDetails(TEST_1, modelMock, raMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }

    @Test
    public void showTaskDetailsTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(0L);
        Task task1 = createTask(TASK_NAME, 1, project);
        task1.setEstimate(new Period(1, 0, 0, 0));
        task1.setLoggedWork(new Period(0, 30, 0, 0));
        task1.setRemaining(new Period(0, 30, 0, 0));
        task1.setSubtasks(1);
        Task task2 = createTask(TASK_NAME, 2, project);
        Task task3 = createTask(TASK_NAME, 3, project);
        Task task4 = createTask(TASK_NAME, 4, project);
        Task task5 = createTask(TASK_NAME, 5, project);
        task5.setEstimate(new Period(0, 30, 0, 0));
        task5.setLoggedWork(new Period(0, 10, 0, 0));
        task5.setRemaining(new Period(0, 10, 0, 0));
        task5.setParent(TEST_1);
        List<Task> subtasks = new LinkedList<Task>();
        subtasks.add(task5);
        task1.setInSprint(true);
        task1.setOwner(testAccount);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task1);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(taskRepoMock.findByParent(TEST_1)).thenReturn(subtasks);
        taskCtr.showTaskDetails(TEST_1, modelMock, raMock);
        Assert.assertEquals(44.0F, task1.getPercentage_left(), 0);
        verify(modelMock, times(12)).addAttribute(anyString(), anyObject());
    }

    @Test
    public void showTaskListTest() {
        Project project = createProject(1L);
        Project project2 = createProject(2L);
        List<Project> projList = new LinkedList<Project>();
        projList.add(project);
        projList.add(project2);
        project.setLastTaskNo(0L);
        Task task1 = createTask(TASK_NAME, 1, project);
        task1.setEstimate(new Period(1, 0, 0, 0));
        task1.setLoggedWork(new Period(0, 30, 0, 0));
        task1.setRemaining(new Period(0, 30, 0, 0));
        task1.setSubtasks(1);
        Task task2 = createTask(TASK_NAME, 2, project);
        Task task3 = createTask(TASK_NAME, 3, project);
        Task task4 = createTask(TASK_NAME, 4, project);
        Task task5 = createTask(TASK_NAME, 5, project);
        task5.setEstimate(new Period(0, 30, 0, 0));
        task5.setLoggedWork(new Period(0, 10, 0, 0));
        task5.setRemaining(new Period(0, 10, 0, 0));
        task5.setParent(TEST_1);
        List<Task> allList = new LinkedList<Task>();
        List<Task> toDoList = new LinkedList<Task>();
        List<Task> subtasks = new LinkedList<Task>();
        subtasks.add(task5);
        allList.add(task1);
        allList.add(task2);
        allList.add(task3);
        allList.add(task4);
        allList.add(task5);
        toDoList.add(task1);
        toDoList.add(task2);
        task1.setInSprint(true);
        task1.setOwner(testAccount);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task1);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(projSrvMock.findAllByUser()).thenReturn(projList);
        when(projSrvMock.findUserActiveProject()).thenReturn(project);
        when(taskRepoMock.findByParent(TEST_1)).thenReturn(subtasks);
        when(taskRepoMock.findAllByProjectAndParentIsNull(project)).thenReturn(allList);
        taskCtr.listTasks(null, null, null, null, null, null, modelMock);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        taskCtr.listTasks(PROJECT_ID, "TO_DO", null, null, null, null, modelMock);
        when(taskRepoMock.findByProjectAndStateAndParentIsNull(project, TaskState.TO_DO)).thenReturn(toDoList);
        taskCtr.listTasks(PROJECT_ID, null, "tas", "MAJOR", null, null, modelMock);
        verify(modelMock, times(7)).addAttribute(anyString(), anyObject());
    }

    @Test
    public void startSubtaskcreateTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        taskCtr.startSubTaskCreate(TEST_1, modelMock);
        verify(modelMock, times(2)).addAttribute(anyString(), anyObject());
    }

    @Test
    public void createSubTaskAuthErrorTest() {
        testAccount.setRole(Roles.ROLE_VIEWER);
        boolean catched = false;
        try {
            taskCtr.createSubTask(null, null, null, raMock, requestMock, modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue(TASQ_AUTH_MSG, catched);
    }

    @Test
    public void createSubTaskErrorsTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setSubtasks(1);
        task.setInSprint(true);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        Task subtask = createTask(TASK_NAME, 2, project);
        TaskForm form = new TaskForm(subtask);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        errors.reject("name", "Error");
        String result = taskCtr.createSubTask(TEST_1, form, errors, raMock, requestMock, modelMock);
        Assert.assertNull(result);
    }

    @Test
    public void createSubTaskCantEditTest() {
        Project project = createProject(1L);
        project.setLastTaskNo(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setSubtasks(1);
        task.setInSprint(true);
        Task subtask = createTask(TASK_NAME, 2, project);
        TaskForm form = new TaskForm(subtask);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        when(projSrvMock.canEdit(project)).thenReturn(false);
        taskCtr.createSubTask(TEST_1, form, errors, raMock, requestMock, modelMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));

    }

    @Test
    public void createSubTaskTest() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setActive(true);
        Project project = createProject(1L);
        project.setLastTaskNo(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setSubtasks(1);
        task.setInSprint(true);
        Task subtask = createTask(TASK_NAME, 2, project);
        TaskForm form = new TaskForm(subtask);
        form.setAssignee(testAccount.getEmail());
        form.setAddToSprint(1L);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.findByProjectId(TestUtils.PROJECT_ID)).thenReturn(project);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(accountServiceMock.findByEmail(testAccount.getEmail())).thenReturn(testAccount);
        when(sprintSrvMock.findByProjectIdAndSprintNo(1L, 1L)).thenReturn(sprint);
        taskCtr.createSubTask(TEST_1, form, errors, raMock, requestMock, modelMock);
        verify(taskRepoMock, times(3)).save(any(Task.class));
    }

    @Test
    public void logWorkCantEditTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_USER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(false);
        taskCtr.logWork(TEST_1, null, null, null, null, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void logWorkTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setEstimate(new Period(1, 20, 0, 0));
        Task loggedTask = createTask(TASK_NAME, 1, project);
        task.setEstimate(new Period(1, 20, 0, 0));
        task.addLoggedWork(new Period().withDays(1));
        task.setRemaining(new Period().withMinutes(10));
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(wrkLogSrv.addTimedWorkLog(any(Task.class), anyString(), any(Date.class), any(Period.class), any(Period.class), any(LogType.class))).thenReturn(loggedTask);
        when(taskRepoMock.save(loggedTask)).thenReturn(loggedTask);
        taskCtr.logWork(TEST_1, "1d", "10m", "1-05-2015", "12:00", raMock, requestMock);
        verify(wrkLogSrv, times(1)).addDatedWorkLog(any(Task.class), anyString(), any(Date.class), any(LogType.class));
        verify(wrkLogSrv, times(1)).addTimedWorkLog(any(Task.class), anyString(), any(Date.class), any(Period.class),
                any(Period.class), any(LogType.class));
    }

    @Test
    public void logWorkTotalTooLongTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.addLoggedWork(PeriodHelper.inFormat("27d 7h"));
        Task loggedTask = createTask(TASK_NAME, 1, project);
        loggedTask.addLoggedWork(PeriodHelper.inFormat("27d 7h"));
        loggedTask.addLoggedWork(new Period(5, 0, 0, 0));
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(taskSrv.save(loggedTask)).thenReturn(loggedTask);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        when(wrkLogSrv.addTimedWorkLog(any(Task.class), anyString(), any(Date.class), any(Period.class), any(Period.class), any(LogType.class))).thenReturn(loggedTask);
        taskCtr.logWork(TEST_1, "5h", null, null, null, raMock, requestMock);
        verify(wrkLogSrv, times(1)).addTimedWorkLog(any(Task.class), anyString(), any(Date.class), any(Period.class),
                any(Period.class), any(LogType.class));
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.WARNING, new Object[]{}));
    }


    @Test
    public void logWorkNegativeTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        taskCtr.logWork(TEST_1, "-1h", null, null, null, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void logWorkTooMuchTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        testAccount.setRole(Roles.ROLE_POWERUSER);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        taskCtr.logWork(TEST_1, "52h", null, null, null, raMock, requestMock);
        taskCtr.logWork(TEST_1, "1m", null, null, null, raMock, requestMock);
        verify(raMock, times(2)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }


    @Test
    public void changeStateAndSPNoAuthTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(false);
        testAccount.setRole(Roles.ROLE_VIEWER);
        ResponseEntity<ResultData> data = taskCtr.changeState(TEST_1, TaskState.COMPLETE, null, null, null, null);
        Assert.assertEquals(ResultData.ERROR, data.getBody().code);
        boolean catched = false;
        try {
            taskCtr.changeStoryPoints(TEST_1, 2);
        } catch (TasqAuthException e) {
            catched = true;
        }
        assertTrue(TASQ_AUTH_MSG, catched);

    }

    @Test
    public void changeStateTODOTest() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setLoggedWork(new Period(0, 10, 0, 0));
        task.setComments(new HashSet<Comment>());
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        ResponseEntity<ResultData> data = taskCtr.changeState(TEST_1, TaskState.TO_DO, true, null, "Done", null);
        Assert.assertEquals(ResultData.WARNING, data.getBody().code);
    }

    @Test
    public void changeStateCLOSEDTest() {
        Project project = createProject(1L);
        project.setAgile(Project.AgileType.KANBAN.toString());
        Task task = createTask(TASK_NAME, 1, project);
        task.setState(TaskState.CLOSED);
        task.setComments(new HashSet<Comment>());
        Account account = new Account(NEW_EMAIL, PASSWORD, NEWUSERNAME, Roles.ROLE_POWERUSER);
        Object[] activeTask = {task.getId()};
        account.setActive_task(activeTask);
        List<Account> accounts = new LinkedList<Account>();
        accounts.add(testAccount);
        accounts.add(account);
        when(accountServiceMock.findAll()).thenReturn(accounts);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        ResponseEntity<ResultData> data = taskCtr.changeState(TEST_1, TaskState.CLOSED, true, null, "Done", TaskResolution.CANNOT_REPRODUCE);
        Assert.assertEquals(ResultData.WARNING, data.getBody().code);
    }

    @Test
    public void changeState() {
        Project project = createProject(1L);
        Task task = createTask(TASK_NAME, 1, project);
        task.setComments(new HashSet<Comment>());
        task.setSubtasks(1);
        Task subtask = createTask(TASK_NAME, 2, project);
        subtask.setParent(TEST_1);
        List<Task> subtasks = new LinkedList<Task>();
        subtasks.add(subtask);
        Object[] activeTask = {task.getId()};
        testAccount.setActive_task(activeTask);
        when(taskRepoMock.findByParent(TEST_1)).thenReturn(subtasks);
        when(taskRepoMock.findById(TEST_1)).thenReturn(task);
        when(projSrvMock.canEdit(project)).thenReturn(true);
        ResponseEntity<ResultData> result = taskCtr.changeState(TEST_1, TaskState.CLOSED, true, true, "Done", TaskResolution.FINISHED);
        verify(wrkLogSrv, times(1)).addActivityLog(subtask, "", LogType.CLOSED);
        verify(taskRepoMock, times(2)).save(any(Task.class));
        Assert.assertEquals(ResultData.OK, result.getBody().code);
    }

    @Test
    public void displayTaskTest() {
        Project project = createProject(1L);
        Task task1 = createTask(TASK_NAME, 1, project);
        task1.setEstimate(new Period(1, 0, 0, 0));
        task1.setLoggedWork(new Period(0, 30, 0, 0));
        task1.setRemaining(new Period(0, 30, 0, 0));
        Assert.assertEquals(50.0F, task1.getPercentage_left(), 0);
        Task task2 = createTask(TASK_NAME, 2, project);
        Assert.assertNotEquals(task1, task2);
        DisplayTask disp1 = new DisplayTask(task1);
        DisplayTask disp2 = new DisplayTask(task1);
        Assert.assertEquals(disp1, disp2);
        List<Task> list = new LinkedList<Task>();
        list.add(task1);
        list.add(task2);
        List<DisplayTask> result = DisplayTask.convertToDisplayTasks(list);
        Assert.assertEquals(2, result.size());
        Assert.assertNotEquals(result.get(1).getPercentage(), disp1.getPercentage());
        Assert.assertNotEquals(result.get(1).hashCode(), disp1.hashCode());

    }
}
