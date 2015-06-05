package com.qprogramming.tasq.task.comments;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.web.Message;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.worklog.LogType;
import com.qprogramming.tasq.task.worklog.WorkLogService;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class CommentsControllerTest {

	private static final String TEST_1 = "TEST-1";
	private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";
	private static final String TASK_NAME = "taskName";
	private static final String PROJECT_NAME = "TestProject";
	private static final String PROJECT_ID = "TEST";
	private static final String PROJECT_DESCRIPTION = "Description";

	private Account testAccount;

	private CommentsController commentsController;

	@Mock
	private CommentsRepository commentsRepoMock;
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
		testAccount = new Account(EMAIL, "", Roles.ROLE_ADMIN);
		testAccount.setLanguage("en");
		when(
				msgMock.getMessage(anyString(), any(Object[].class),
						any(Locale.class))).thenReturn("MESSAGE");
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		commentsController = new CommentsController(commentsRepoMock,
				taskSrvMock, wrkLogSrvMock, msgMock);
	}

	@Test
	public void taskCommentNotAllowedTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		task.setState(TaskState.CLOSED);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.addComment(TEST_1, "Comment", requestMock, raMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.WARNING,
								new Object[] {}));
	}

	@Test
	public void taskCommentMessageNotValidTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.addComment(TEST_1, "<script>Comment</script>",
				requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void taskCommentEmptyTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.addComment(TEST_1, null, requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void taskCommentTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		task.setComments(new HashSet<Comment>());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.addComment(TEST_1, "Comment", requestMock, raMock);
		verify(raMock, times(1))
				.addFlashAttribute(
						anyString(),
						new Message(anyString(), Message.Type.SUCCESS,
								new Object[] {}));
		verify(taskSrvMock, times(1)).save(task);
		verify(wrkLogSrvMock, times(1)).addActivityLog(any(Task.class),
				anyString(), any(LogType.class));
	}

	@Test
	public void taskCommentDeleteNotAllowedTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		task.setState(TaskState.CLOSED);
		task.setComments(new HashSet<Comment>());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.deleteComment(TEST_1, 1L, requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void taskCommentDeleteNotFoundAllowedTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		task.setComments(new HashSet<Comment>());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.deleteComment(TEST_1, 1L, requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}

	@Test
	public void taskCommentDeleteNotAuthorTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setAuthor(new Account("email@email.com", "", Roles.ROLE_USER));
		comment.setMessage("Comment");
		Set<Comment> comments = new HashSet<Comment>();
		comments.add(comment);
		task.setComments(comments);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		when(commentsRepoMock.findById(1L)).thenReturn(comment);
		commentsController.deleteComment(TEST_1, 1L, requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}
	
	@Test
	public void taskCommentDeleteTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setAuthor(testAccount);
		comment.setMessage("Comment");
		Set<Comment> comments = new HashSet<Comment>();
		comments.add(comment);
		task.setComments(comments);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		when(commentsRepoMock.findById(1L)).thenReturn(comment);
		commentsController.deleteComment(TEST_1, 1L, requestMock, raMock);
		verify(commentsRepoMock,times(1)).save(any(Comment.class));
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.SUCCESS, new Object[] {}));
	}
	
	@Test
	public void taskCommentEditNotAllowedTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		task.setState(TaskState.CLOSED);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.editComment(TEST_1, 1L, "comment", requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.WARNING, new Object[] {}));
	}
	
	@Test
	public void taskCommentEditInvalidTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		commentsController.editComment(TEST_1, 1L, "<script>comment</script>", requestMock, raMock);
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.DANGER, new Object[] {}));
	}
	
	@Test
	public void taskCommentEditTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setAuthor(testAccount);
		comment.setMessage("Comment");
		Set<Comment> comments = new HashSet<Comment>();
		comments.add(comment);
		task.setComments(comments);
		when(taskSrvMock.findById(TEST_1)).thenReturn(task);
		when(commentsRepoMock.findById(1L)).thenReturn(comment);
		commentsController.editComment(TEST_1, 1L, "new comment content", requestMock, raMock);
		verify(commentsRepoMock,times(1)).save(any(Comment.class));
		verify(raMock, times(1)).addFlashAttribute(anyString(),
				new Message(anyString(), Message.Type.SUCCESS, new Object[] {}));
	}
	
	@Test
	public void commentsTest(){
		Comment comment = new Comment();
		comment.setId(1L);
		comment.setAuthor(testAccount);
		comment.setMessage("Comment");
		comment.setDate(new Date());
		comment.setDate_edited(new Date());
		
		Comment comment2 = new Comment();
		comment2.setId(2L);
		comment2.setAuthor(testAccount);
		comment2.setMessage("Comment2");
		comment2.setDate(new Date());
		Assert.assertFalse(comment.equals(comment2));
		Assert.assertFalse(comment.getId() == comment2.getId());
		Assert.assertNotNull(comment.getDate_edited());
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

}
