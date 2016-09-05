package com.qprogramming.tasq.task.importexport;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.*;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportExportControllerTest {

    private static final String TEST_1 = "TEST-1";
    private static final String TEST_2 = "TEST-2";
    private static final String TEST_3 = "TEST-3";
    private static final String EMAIL = "user@test.com";
    private static final String TASK_NAME = "taskName";
    private static final String PROJECT_NAME = "TestProject";
    private static final String PROJECT_ID = "TEST";
    private static final String PROJECT_DESCRIPTION = "Description";
    private static final String TEMPLATE_XLS = "template.xls";
    private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private Task task1;
    private Task task2;
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
        testAccount = new Account(EMAIL, "", "user", Roles.ROLE_ADMIN);
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        Project project = createProject(1L);
        task1 = createTask(TASK_NAME, 1, project);
        task2 = createTask(TASK_NAME, 2, project);
        when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
        when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
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
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void startImportFailedTaskTest() {
        boolean catched = false;
        testAccount.setRole(Roles.ROLE_VIEWER);
        try {
            importExportCtrl.startImportTasks(modelMock);
        } catch (TasqAuthException e) {
            catched = true;
        }
        Assert.assertTrue("Exception not catched", catched);
    }

    @Test
    public void startImportTaskTest() {
        importExportCtrl.startImportTasks(modelMock);
        verify(modelMock, times(1)).addAttribute(anyString(), Matchers.anyListOf(Project.class));
    }

    @Test
    public void importTasksTest() {
        URL fileURL = getClass().getResource("/sampleImport.xls");
        Project project = createProject(1L);
        when(projSrvMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        try {
            mockMultipartFile = new MockMultipartFile("content", fileURL.getFile(), "text/plain",
                    getClass().getResourceAsStream("/sampleImport.xls"));
            importExportCtrl.importTasks(mockMultipartFile, PROJECT_ID, modelMock);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void exportTasksTest() {
        try {
            List<Task> list = new LinkedList<Task>();
            task1.setEstimate(new Period());
            list.add(task1);
            list.add(task2);
            String[] idList = {TEST_1, TEST_2};
            when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
            when(taskSrvMock.finAllById(Arrays.asList(idList))).thenReturn(list);
            when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
            when(projSrvMock.canView(task1.getProject())).thenReturn(true);
            importExportCtrl.exportTasks(idList, "XLS", responseMock, requestMock);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void exportTasksExceptionTest() {
        boolean catched = false;
        try {
            List<Task> list = new LinkedList<Task>();
            Task task3 = createTask(TASK_NAME, 3, createProject(2L));
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
            } catch (TasqAuthException e) {
                catched = true;
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertTrue(TASQ_AUTH_MSG, catched);
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

    private Project createProject(Long id) {
        Project project = new Project(PROJECT_NAME, testAccount);
        project.setDescription(PROJECT_DESCRIPTION);
        project.setProjectId(PROJECT_ID);
        project.setId(id);
        return project;
    }

}
