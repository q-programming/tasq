package com.qprogramming.tasq.task.tags;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskPriority;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.task.TaskState;
import com.qprogramming.tasq.task.TaskType;
import com.qprogramming.tasq.task.tag.Tag;
import com.qprogramming.tasq.task.tag.TagsRepository;
import com.qprogramming.tasq.task.tag.TagsRestController;
import com.qprogramming.tasq.test.MockSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class TagsControllerTest {

	private static final String TEST_1 = "TEST";
	private static final String TEST_2 = "TEST2";
	private static final String TASQ_AUTH_MSG = "TasqAuthException was not thrown";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "user@test.com";
	private static final String NEW_EMAIL = "newuser@test.com";
	private static final String TASK_NAME = "taskName";
	private static final String PROJECT_NAME = "TestProject";
	private static final String PROJECT_ID = "TEST";
	private static final String PROJECT_DESCRIPTION = "Description";

	private Account testAccount;
	private TagsRestController tagsRestController;
	@Mock
	private TagsRepository tagsRepoMock;
	@Mock
	private TaskService taskSrvMock;
	@Mock
	private MessageSource msgMock;
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
		tagsRestController = new TagsRestController(tagsRepoMock, taskSrvMock);
	}

	@Test
	public void getTagsTest() {
		Tag tag1 = new Tag(TEST_1);
		tag1.setId(1L);
		Tag tag2 = new Tag(TEST_2);
		tag2.setId(2L);
		List<Tag> list = new LinkedList<Tag>();
		List<Tag> list2 = new LinkedList<Tag>();
		list.add(tag1);
		list.add(tag2);
		list2.add(tag1);
		when(tagsRepoMock.findAll()).thenReturn(list);
		when(tagsRepoMock.findByNameContainingIgnoreCase(TEST_2)).thenReturn(
				list2);
		List<Tag> result = tagsRestController.getTags(null, responseMock);
		Assert.assertTrue(result.size() > 1);
		result = tagsRestController.getTags(TEST_2, responseMock);
		Assert.assertTrue(result.size() == 1);
		Assert.assertNotEquals(tag1, tag2);
	}

	@Test
	public void addTagsTest() {
		Task task = createTask(TASK_NAME, 1, createProject());
		when(tagsRepoMock.findByName(TEST_1)).thenReturn(null);
		when(taskSrvMock.findById(PROJECT_ID + "-1")).thenReturn(task);
		Assert.assertEquals(ResultData.ERROR,
				tagsRestController.addTag(TEST_1, PROJECT_ID + "-2"));
		Assert.assertEquals(ResultData.OK,
				tagsRestController.addTag(TEST_1, PROJECT_ID + "-1"));
		verify(tagsRepoMock, times(1)).save(any(Tag.class));
	}

	@Test
	public void removeTagsTest() {
		Tag tag = new Tag(TEST_1);
		Task task = createTask(TASK_NAME, 1, createProject());
		Set<Tag> taskTags = new HashSet<Tag>();
		taskTags.add(tag);
		task.setTags(taskTags);
		when(tagsRepoMock.findByName(TEST_1)).thenReturn(tag);
		when(taskSrvMock.findById(PROJECT_ID + "-1")).thenReturn(task);
		Assert.assertEquals(ResultData.ERROR,
				tagsRestController.removeTag(TEST_1, PROJECT_ID + "-2"));
		Assert.assertEquals(ResultData.OK,
				tagsRestController.removeTag(TEST_1, PROJECT_ID + "-1"));
		Assert.assertTrue(task.getTags().isEmpty());
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
