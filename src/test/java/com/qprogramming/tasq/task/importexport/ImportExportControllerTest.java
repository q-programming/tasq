package com.qprogramming.tasq.task.importexport;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.qprogramming.tasq.test.TestUtils.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportExportControllerTest {

    private static final String TEST_1 = "TEST-1";
    private static final String TEST_2 = "TEST-2";
    private static final String TEST_3 = "TEST-3";
    private static final String TEMPLATE_XLS = "template.xls";
    private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private Task task1;
    private Task task2;
    private Project project;
    private ImportExportController importExportCtrl;
    @Mock
    private ProjectService projSrvMock;
    @Mock
    private TaskService taskSrvMock;
    @Mock
    private WorkLogService wlSrvMock;
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
    @Mock
    private ServletOutputStream outputStreamMock;
    @Mock
    private MockMultipartFile mockMultipartFile;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        project = TestUtils.createProject();
        task1 = createTask(TASK_NAME, 1, project);
        task2 = createTask(TASK_NAME, 2, project);
        when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
        when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
        when(taskSrvMock.save(any(Task.class))).thenAnswer((Answer<Task>) invocationOnMock -> (Task) invocationOnMock.getArguments()[0]);
        when(taskSrvMock.createSubTask(any(Project.class), any(Task.class), any(Task.class))).thenAnswer((Answer<Task>) invocationOnMock -> {
            Task parentTask = (Task) invocationOnMock.getArguments()[1];
            Task subTask = (Task) invocationOnMock.getArguments()[2];
            String subId = taskSrvMock.createSubId(parentTask.getId(), "1");
            subTask.setId(subId);
            return subTask;
        });
        when(taskSrvMock.createSubId(anyString(), anyString())).thenCallRealMethod();
        SecurityContextHolder.setContext(securityMock);
        importExportCtrl = new ImportExportController(projSrvMock, taskSrvMock, wlSrvMock, msgMock);
    }

    @Test
    public void downloadTemplateTest() {
        try {
            when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
            importExportCtrl.downloadTemplate(requestMock, responseMock);
            verify(responseMock, times(1)).setHeader("content-Disposition", "attachment; filename=" + TEMPLATE_XLS);
            verify(outputStreamMock, times(7)).write(any(byte[].class), anyInt(), anyInt());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = TasqAuthException.class)
    public void startImportFailedTaskTest() {
        testAccount.setRole(Roles.ROLE_VIEWER);
        importExportCtrl.startImportTasks(modelMock);
    }

    @Test
    public void startImportTaskTest() {
        importExportCtrl.startImportTasks(modelMock);
        verify(modelMock, times(1)).addAttribute(anyString(), Matchers.anyListOf(Project.class));
    }

    @Test
    public void importTasksTest() {
        URL fileURL = getClass().getResource("sampleImport.xls");
        Project project = TestUtils.createProject();
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        try {
            mockMultipartFile = new MockMultipartFile("content", fileURL.getFile(), "text/plain",
                    getClass().getResourceAsStream("sampleImport.xls"));
            importExportCtrl.importTasks(mockMultipartFile, PROJECT_ID, modelMock);
            verify(taskSrvMock, times(1)).save(any(Task.class));
            verify(taskSrvMock, times(3)).createSubTask(any(Project.class), any(Task.class), any(Task.class));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void importXMLTasksTest() {
        URL fileURL = getClass().getResource("sampleImport.xml");
        Project project = TestUtils.createProject();
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        try {
            mockMultipartFile = new MockMultipartFile("content", fileURL.getFile(), "text/plain",
                    getClass().getResourceAsStream("sampleImport.xml"));
            importExportCtrl.importTasks(mockMultipartFile, PROJECT_ID, modelMock);
            verify(taskSrvMock, times(2)).save(any(Task.class));
            verify(taskSrvMock, times(3)).createSubTask(any(Project.class), any(Task.class), any(Task.class));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void exportTasksTest() {
        try {
            List<Task> list = new LinkedList<>();
            task1.setEstimate(new Period());
            task1.setSubtasks(2);
            List<Task> subtasklist = new LinkedList<>();
            Task subTask1 = createTask(TASK_NAME, 3, project);
            Task subTask2 = createTask(TASK_NAME, 3, project);
            subTask1.setParent(TestUtils.TEST_1);
            subTask2.setParent(TestUtils.TEST_2);
            subtasklist.add(subTask1);
            subtasklist.add(subTask2);
            list.add(task1);
            list.add(task2);
            String[] idList = {TEST_1, TEST_2};
            when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
            when(taskSrvMock.finAllById(Arrays.asList(idList))).thenReturn(list);
            when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
            when(taskSrvMock.findSubtasks(task1)).thenReturn(subtasklist);
            when(projSrvMock.canView(task1.getProject())).thenReturn(true);
            importExportCtrl.exportTasks(idList, "xls", responseMock, requestMock);
        } catch (IOException | InvalidFormatException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void exportTasksXMLTest() {
        try {
            List<Task> list = new LinkedList<Task>();
            task1.setEstimate(new Period());
            task1.setSubtasks(2);
            List<Task> subtasklist = new LinkedList<>();
            Task subTask1 = createTask(TASK_NAME, 3, project);
            Task subTask2 = createTask(TASK_NAME, 3, project);
            subTask1.setParent(TestUtils.TEST_1);
            subTask2.setParent(TestUtils.TEST_2);
            subtasklist.add(subTask1);
            subtasklist.add(subTask2);
            list.add(task1);
            list.add(task2);
            String[] idList = {TEST_1, TEST_2};
            when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
            when(taskSrvMock.finAllById(Arrays.asList(idList))).thenReturn(list);
            when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
            when(taskSrvMock.findSubtasks(task1)).thenReturn(subtasklist);
            when(projSrvMock.canView(task1.getProject())).thenReturn(true);
            importExportCtrl.exportTasks(idList, "xml", responseMock, requestMock);
        } catch (IOException | InvalidFormatException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void exportTasksExceptionTest() {
        boolean catched = false;
        try {
            List<Task> list = new LinkedList<Task>();
            Task task3 = createTask(TASK_NAME, 3, TestUtils.createProject(2L));
            task1.setEstimate(new Period());
            list.add(task1);
            list.add(task2);
            list.add(task3);
            String[] idList = {TEST_1, TEST_2, TEST_3};
            when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
            when(taskSrvMock.finAllById(Arrays.asList(idList))).thenReturn(list);
            when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
            when(projSrvMock.canView(task1.getProject())).thenReturn(true);
            try {
                importExportCtrl.exportTasks(idList, "XLS", responseMock, requestMock);
            } catch (TasqAuthException | InvalidFormatException e) {
                catched = true;
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
    }
}
