package com.qprogramming.tasq.projects;

import com.qprogramming.tasq.account.*;
import com.qprogramming.tasq.projects.NewProjectForm;
import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.projects.ProjectRepository;
import com.qprogramming.tasq.projects.ProjectService;
import com.qprogramming.tasq.projects.holiday.Holiday;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.qprogramming.tasq.test.TestUtils.PROJECT_ID;
import static com.qprogramming.tasq.test.TestUtils.PROJECT_NAME;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ProjectService projSrv;
    @Mock
    private AccountRepository accRepoMomck;
    @Mock
    private UserService usrSrvMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private ProjectRepository projRepoMock;
    @Mock
    private AccountService accSrvMock;
    @Mock
    private Authentication authMock;
    private Account testAccount;
    private Project testProject;

    @Before
    public void setUp() {
        testAccount = TestUtils.createAccount();
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        projSrv = new ProjectService(projRepoMock, accSrvMock, usrSrvMock);
        testProject = TestUtils.createProject();
    }

    @Test
    public void findByNameTest() {
        when(projRepoMock.findByNameIgnoreCase(PROJECT_NAME)).thenReturn(testProject);
        Assert.assertNotNull(projSrv.findByName(PROJECT_NAME));
    }

    @Test
    public void findByIdTest() {
        when(projRepoMock.findById(1L)).thenReturn(testProject);
        when(projRepoMock.findByProjectId(PROJECT_ID)).thenReturn(testProject);
        Assert.assertNotNull(projSrv.findById(1L));
        Assert.assertNotNull(projSrv.findByProjectId(PROJECT_ID));
    }

    @Test
    public void saveTest() {
        projSrv.save(testProject);
        verify(projRepoMock, times(1)).save(testProject);
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
        testAccount.setActiveProject(PROJECT_ID);
        when(projRepoMock.findByProjectId(PROJECT_ID)).thenReturn(testProject);
        Assert.assertNotNull(projSrv.findUserActiveProject());
    }

    @Test
    public void activateTest() {
        when(projRepoMock.findByProjectId(PROJECT_ID)).thenReturn(testProject);
        when(projRepoMock.save(testProject)).thenReturn(testProject);
        Assert.assertNotNull(projSrv.activateForCurrentUser(PROJECT_ID));
        verify(accSrvMock, times(1)).update(testAccount);
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
        testAccount.setRole(Roles.ROLE_POWERUSER);
        Assert.assertTrue(projSrv.canEdit(1L));
    }

    @Test
    public void getFreeDaysTest() {
        Holiday holiday = new Holiday(new DateTime(2016, 7, 1, 11, 00).toDate());
        testProject.setWorkingWeekends(false);
        Set<Holiday> holidays = testProject.getHolidays();
        holidays.add(holiday);
        testProject.setHolidays(holidays);
        List<LocalDate> freeDays = projSrv.getFreeDays(testProject, new DateTime(2016, 6, 11, 11, 00), new DateTime(2016, 7, 10, 11, 00));
        Assert.assertEquals(11, freeDays.size());

    }


    @Test
    public void getParticipantTest() {
        Project project = createForm(PROJECT_NAME, PROJECT_ID).createProject();
        testAccount.setName("John");
        testAccount.setSurname("Doe");
        Account secondTestAccount = new Account("second@test.com", "", "second", Roles.ROLE_USER);
        secondTestAccount.setName("Second");
        secondTestAccount.setSurname("Doe");
        project.setId(1L);
        project.addParticipant(testAccount);
        project.addParticipant(secondTestAccount);
        when(projRepoMock.findByProjectId(PROJECT_ID)).thenReturn(project);
        List<Account> result = projSrv.getProjectAccounts(PROJECT_ID, null);
        Assert.assertEquals(2, result.size());
        result = projSrv.getProjectAccounts(PROJECT_ID, "Do");
        Assert.assertEquals(2, result.size());
        result = projSrv.getProjectAccounts(PROJECT_ID, "Jo");
        Assert.assertEquals(1, result.size());
        result = projSrv.getProjectAccounts(PROJECT_ID, "Xx");
        Assert.assertEquals(0, result.size());
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
