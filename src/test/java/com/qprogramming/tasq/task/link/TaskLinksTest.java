package com.qprogramming.tasq.task.link;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.DisplayTask;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.qprogramming.tasq.test.TestUtils.TEST_1;
import static com.qprogramming.tasq.test.TestUtils.TEST_2;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskLinksTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Account testAccount;
    private Task task1;
    private Task task2;
    private TaskLinkController taskLinkController;
    private TaskLinkService taskLinkSrv;
    @Mock
    private TaskLinkRepository taskLinkRepoMock;
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

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        Project project = TestUtils.createProject();
        task1 = TestUtils.createTask(TestUtils.TASK_NAME, 1, project);
        task2 = TestUtils.createTask(TestUtils.TASK_NAME, 2, project);
        when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
        when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
        SecurityContextHolder.setContext(securityMock);
        taskLinkSrv = new TaskLinkService(taskLinkRepoMock);
        taskLinkController = new TaskLinkController(taskSrvMock, wrkLogSrvMock, msgMock, taskLinkSrv);

    }

    @Test
    public void linkTasksLinkExistsTest() {
        TaskLink link = new TaskLink();
        link.setTaskA(TEST_1);
        link.setTaskA(TEST_2);
        link.setLinkType(TaskLinkType.RELATES_TO);
        when(taskLinkRepoMock.findByTaskAAndTaskBAndLinkType(TEST_1, TEST_2, TaskLinkType.RELATES_TO)).thenReturn(link);
        when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
        when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
        taskLinkController.linkTasks(TEST_1, TEST_2, TaskLinkType.RELATES_TO, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void linkTasksLinkABlocksBTest() {
        taskLinkController.linkTasks(TEST_1, TEST_2, TaskLinkType.BLOCKS, raMock, requestMock);
        Assert.assertTrue(task2.getState().equals(TaskState.BLOCKED));
        verify(taskSrvMock, times(1)).save(any(Task.class));
        verify(taskLinkRepoMock, times(1)).save(any(TaskLink.class));
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.SUCCESS, new Object[]{}));
    }

    @Test
    public void linkTasksLinkAIsBlockedBTest() {
        taskLinkController.linkTasks(TEST_2, TEST_1, TaskLinkType.IS_BLOCKED_BY, raMock, requestMock);
        Assert.assertTrue(task2.getState().equals(TaskState.BLOCKED));
    }

    @Test
    public void deleteLinkEmptyTest() {
        taskLinkController.deleteLinks(TEST_1, TEST_2, TaskLinkType.BLOCKS, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.DANGER, new Object[]{}));
    }

    @Test
    public void deleteLinkTest() {
        TaskLink link = new TaskLink();
        link.setTaskA(TEST_1);
        link.setTaskB(TEST_2);
        link.setLinkType(TaskLinkType.DUPLICATES);
        when(taskLinkRepoMock.findByTaskBAndTaskAAndLinkType(TEST_1, TEST_2, TaskLinkType.DUPLICATES)).thenReturn(link);
        taskLinkController.deleteLinks(TEST_1, TEST_2, TaskLinkType.DUPLICATES, raMock, requestMock);
        verify(raMock, times(1)).addFlashAttribute(anyString(),
                new Message(anyString(), Message.Type.SUCCESS, new Object[]{}));
        verify(taskLinkRepoMock, times(1)).delete(any(TaskLink.class));
    }

    @Test
    public void deleteLinksTest() {
        TaskLink link = new TaskLink();
        link.setTaskA(TEST_1);
        link.setTaskB(TEST_2);
        link.setLinkType(TaskLinkType.DUPLICATES);
        List<TaskLink> list = new ArrayList<TaskLink>();
        list.add(link);
        when(taskLinkRepoMock.findByTaskA(TEST_1)).thenReturn(list);
        taskLinkSrv.deleteTaskLinks(task1);
        verify(taskLinkRepoMock, times(1)).delete(list);
    }

    @Test
    public void findTaskLinksTest() {
        TaskLink link = new TaskLink();
        link.setTaskA(TEST_1);
        link.setTaskB(TEST_2);
        link.setLinkType(TaskLinkType.DUPLICATES);
        TaskLink link2 = new TaskLink();
        link2.setTaskA(TEST_1);
        link2.setTaskB(TEST_2);
        link2.setLinkType(TaskLinkType.BLOCKS);
        TaskLink link3 = new TaskLink();
        link3.setTaskA(TEST_1);
        link3.setTaskB(TEST_2);
        link3.setLinkType(TaskLinkType.RELATES_TO);
        TaskLink link4 = new TaskLink();
        link4.setTaskA(TEST_1);
        link4.setTaskB(TEST_2);
        link4.setLinkType(TaskLinkType.DUPLICATES);
        List<TaskLink> list = new ArrayList<TaskLink>();
        List<TaskLink> list2 = new ArrayList<TaskLink>();
        list.add(link);
        list.add(link2);
        list.add(link3);
        list2.add(link4);
        when(taskLinkRepoMock.findByTaskA(TEST_1)).thenReturn(list);
        when(taskLinkRepoMock.findByTaskB(TEST_1)).thenReturn(list2);
        Map<TaskLinkType, List<String>> map = taskLinkSrv.findTaskLinks(TEST_1);
        Assert.assertTrue(map.size() == 4);
    }
}
