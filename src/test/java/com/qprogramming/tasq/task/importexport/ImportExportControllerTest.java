package com.qprogramming.tasq.task.importexport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.error.TasqAuthException;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class ImportExportControllerTest {

	private static final String TEST_1 = "TEST-1";
	private static final String TEST_2 = "TEST-2";
	private static final String EMAIL = "user@test.com";
	private static final String TASK_NAME = "taskName";
	private static final String PROJECT_NAME = "TestProject";
	private static final String PROJECT_ID = "TEST";
	private static final String PROJECT_DESCRIPTION = "Description";
	private static final String TEMPLATE_XLS = "template.xls";

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
		Project project = createProject();
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
			String[] idList = { TEST_1, TEST_2 };
			when(responseMock.getOutputStream()).thenReturn(outputStreamMock);
			when(taskSrvMock.finAllById(Arrays.asList(idList))).thenReturn(list);
			when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
			importExportCtrl.exportTasks(idList, "XLS", responseMock, requestMock);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	// @Test
	// public void linkTasksLinkExistsTest() {
	// TaskLink link = new TaskLink();
	// link.setTaskA(TEST_1);
	// link.setTaskA(TEST_2);
	// link.setLinkType(TaskLinkType.RELATES_TO);
	// when(
	// taskLinkRepoMock.findByTaskAAndTaskBAndLinkType(TEST_1, TEST_2,
	// TaskLinkType.RELATES_TO)).thenReturn(link);
	// when(taskSrvMock.findById(TEST_1)).thenReturn(task1);
	// when(taskSrvMock.findById(TEST_2)).thenReturn(task2);
	// taskLinkController.linkTasks(TEST_1, TEST_2, TaskLinkType.RELATES_TO,
	// raMock, requestMock);
	// verify(raMock, times(1)).addFlashAttribute(anyString(),
	// new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	// }
	//
	// @Test
	// public void linkTasksLinkABlocksBTest() {
	// taskLinkController.linkTasks(TEST_1, TEST_2, TaskLinkType.BLOCKS,
	// raMock, requestMock);
	// Assert.assertTrue(task2.getState().equals(TaskState.BLOCKED));
	// verify(taskSrvMock, times(1)).save(any(Task.class));
	// verify(taskLinkRepoMock, times(1)).save(any(TaskLink.class));
	// verify(raMock, times(1))
	// .addFlashAttribute(
	// anyString(),
	// new Message(anyString(), Message.Type.SUCCESS,
	// new Object[] {}));
	// }
	//
	// @Test
	// public void linkTasksLinkAIsBlockedBTest() {
	// taskLinkController.linkTasks(TEST_2, TEST_1,
	// TaskLinkType.IS_BLOCKED_BY, raMock, requestMock);
	// Assert.assertTrue(task2.getState().equals(TaskState.BLOCKED));
	// }
	//
	// @Test
	// public void deleteLinkEmptyTest() {
	// taskLinkController.deleteLinks(TEST_1, TEST_2, TaskLinkType.BLOCKS,
	// raMock, requestMock);
	// verify(raMock, times(1)).addFlashAttribute(anyString(),
	// new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	// }
	//
	// @Test
	// public void deleteLinkTest() {
	// TaskLink link = new TaskLink();
	// link.setTaskA(TEST_1);
	// link.setTaskB(TEST_2);
	// link.setLinkType(TaskLinkType.DUPLICATES);
	// when(
	// taskLinkRepoMock.findByTaskBAndTaskAAndLinkType(TEST_1, TEST_2,
	// TaskLinkType.DUPLICATES)).thenReturn(link);
	// taskLinkController.deleteLinks(TEST_1, TEST_2, TaskLinkType.DUPLICATES,
	// raMock, requestMock);
	// verify(raMock, times(1))
	// .addFlashAttribute(
	// anyString(),
	// new Message(anyString(), Message.Type.SUCCESS,
	// new Object[] {}));
	// verify(taskLinkRepoMock, times(1)).delete(any(TaskLink.class));
	// }
	//
	// @Test
	// public void deleteLinksTest() {
	// TaskLink link = new TaskLink();
	// link.setTaskA(TEST_1);
	// link.setTaskB(TEST_2);
	// link.setLinkType(TaskLinkType.DUPLICATES);
	// List<TaskLink> list = new ArrayList<TaskLink>();
	// list.add(link);
	// when(taskLinkRepoMock.findByTaskA(TEST_1)).thenReturn(list);
	// taskLinkSrv.deleteTaskLinks(task1);
	// verify(taskLinkRepoMock, times(1)).delete(list);
	// }
	//
	// @Test
	// public void findTaskLinksTest() {
	// TaskLink link = new TaskLink();
	// link.setTaskA(TEST_1);
	// link.setTaskB(TEST_2);
	// link.setLinkType(TaskLinkType.DUPLICATES);
	// TaskLink link2 = new TaskLink();
	// link2.setTaskA(TEST_1);
	// link2.setTaskB(TEST_2);
	// link2.setLinkType(TaskLinkType.BLOCKS);
	// TaskLink link3 = new TaskLink();
	// link3.setTaskA(TEST_1);
	// link3.setTaskB(TEST_2);
	// link3.setLinkType(TaskLinkType.RELATES_TO);
	// TaskLink link4 = new TaskLink();
	// link4.setTaskA(TEST_1);
	// link4.setTaskB(TEST_2);
	// link4.setLinkType(TaskLinkType.DUPLICATES);
	// List<TaskLink> list = new ArrayList<TaskLink>();
	// List<TaskLink> list2 = new ArrayList<TaskLink>();
	// list.add(link);
	// list.add(link2);
	// list.add(link3);
	// list2.add(link4);
	// when(taskLinkRepoMock.findByTaskA(TEST_1)).thenReturn(list);
	// when(taskLinkRepoMock.findByTaskB(TEST_1)).thenReturn(list2);
	// Map<TaskLinkType, List<DisplayTask>> map = taskLinkSrv
	// .findTaskLinks(TEST_1);
	// Assert.assertTrue(map.size() == 4);
	// }

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

}
