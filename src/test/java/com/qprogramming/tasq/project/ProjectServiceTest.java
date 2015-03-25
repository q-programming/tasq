package com.qprogramming.tasq.project;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.qprogramming.tasq.MockSecurityContext;
import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.AccountRepository;
import com.qprogramming.tasq.account.AccountService;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.projects.NewProjectForm;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectRepository;
import com.qprogramming.tasq.projects.ProjectService;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

	private static final String EMAIL = "user@test.com";

	private static final String PROJ_NAME = "Test project";
	private static final String PROJ_ID = "TEST";

	private ProjectService projSrv;
	
	@Mock
	private AccountRepository accRepoMomck;

	@Mock
	private MockSecurityContext securityMock;
	
	@Mock
	private ProjectRepository projRepoMock;
	
	@Mock
	private AccountService accSrvMock;

	@Mock
	private Authentication authMock;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private Account testAccount;
	private Project testProject;

	@Before
	public void setUp() {
		testAccount = new Account(EMAIL, "", Roles.ROLE_ADMIN);
		when(securityMock.getAuthentication()).thenReturn(authMock);
		when(authMock.getPrincipal()).thenReturn(testAccount);
		SecurityContextHolder.setContext(securityMock);
		projSrv = new ProjectService(projRepoMock, accSrvMock);
		testProject = createForm(PROJ_NAME, PROJ_ID).createProject();
	}

	@Test
	public void findByNameTest() {
		when(projRepoMock.findByName(PROJ_NAME)).thenReturn(testProject);
		Assert.assertNotNull(projSrv.findByName(PROJ_NAME));
	}
	@Test
	public void findByIdTest() {
		when(projRepoMock.findById(1L)).thenReturn(testProject);
		when(projRepoMock.findByProjectId(PROJ_ID)).thenReturn(testProject);
		Assert.assertNotNull(projSrv.findById(1L));
		Assert.assertNotNull(projSrv.findByProjectId(PROJ_ID));
	}
	@Test
	public void saveTest() {
		projSrv.save(testProject);
		verify(projRepoMock,times(1)).save(testProject);
	}
	@Test
	public void findAllTest() {
		List<Project> list = new LinkedList<Project>();
		list.add(testProject);
		when(projRepoMock.findAll()).thenReturn(list);
		Assert.assertNotNull(projSrv.findAll());
	}
	@Test
	public void findAllByUserTest() {
		List<Project> list = new LinkedList<Project>();
		list.add(testProject);
		when(projRepoMock.findByParticipants_Id(1L)).thenReturn(list);
		Assert.assertNotNull(projSrv.findAllByUser());
		Assert.assertNotNull(projSrv.findAllByUser(1L));
	}
	@Test
	public void findUserActiveProjectTest() {
		List<Project> list = new LinkedList<Project>();
		list.add(testProject);
		testAccount.setActive_project(1L);
		when(projRepoMock.findById(1L)).thenReturn(testProject);
		Assert.assertNotNull(projSrv.findUserActiveProject());
	}
	@Test
	public void activateTest() {
		when(projRepoMock.findById(1L)).thenReturn(testProject);
		when(projRepoMock.save(testProject)).thenReturn(testProject);
		Assert.assertNotNull(projSrv.activate(1L));
		verify(accSrvMock,times(1)).update(testAccount);
	}
	
	@Test
	public void canEditEmptyTest() {
		when(projRepoMock.findById(1L)).thenReturn(null);
		Assert.assertFalse(projSrv.canEdit(1L));
	}
	
	@Test
	public void canEditTest() {
		when(projRepoMock.findById(1L)).thenReturn(testProject);
		Assert.assertTrue(projSrv.canEdit(1L));
		testAccount.setRole(Roles.ROLE_USER);
		Assert.assertTrue(projSrv.canEdit(1L));
		
	}


	
	private NewProjectForm createForm(String name, String projid) {
		NewProjectForm form = new NewProjectForm();
		form.setProject_id(projid);
		form.setName(name);
		form.setDescription("Description");
		form.setAgile("SCRUM");
		form.setId(1L);
		return form;
	}

}
