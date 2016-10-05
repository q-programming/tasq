package com.qprogramming.tasq.account;

import com.qprogramming.tasq.projects.Project;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.test.TestUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Khobar on 05.10.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class LastVisitedServiceTest {


    private LastVisitedService visitedService;
    @Mock
    private LastVisitedRepository lastVisitedRepositoryMock;
    private Account testAccount;


    @Before
    public void setUp() {
        visitedService = new LastVisitedService(lastVisitedRepositoryMock);
        testAccount = TestUtils.createAccount();
    }


    @Test
    public void addLastVisitedUpdateTest() throws Exception {
        Task visitedTask = TestUtils.createTask(TestUtils.TASK_NAME, 3, TestUtils.createProject());
        List<LastVisited> lastVisitedTasks = TestUtils.createLastVisitedTasks(4);
        lastVisitedTasks.get(3).setTime(new DateTime().minusDays(1).toDate());
        when(lastVisitedRepositoryMock.findByAccountAndTypeNotNullOrderByTimeAsc(testAccount.getId())).thenReturn(lastVisitedTasks);
        visitedService.addLastVisited(testAccount.getId(), visitedTask);
        verify(lastVisitedRepositoryMock, times(1)).save(anyCollection());
    }

    @Test
    public void addLastVisitedMoreTest() throws Exception {
        Task visitedTask = TestUtils.createTask(TestUtils.TASK_NAME, 4, TestUtils.createProject());
        List<LastVisited> lastVisitedTasks = TestUtils.createLastVisitedTasks(4);
        lastVisitedTasks.get(3).setTime(new DateTime().minusDays(1).toDate());
        LastVisited oldest = lastVisitedTasks.get(3);
        when(lastVisitedRepositoryMock.findByAccountAndTypeNotNullOrderByTimeAsc(testAccount.getId())).thenReturn(lastVisitedTasks);
        visitedService.addLastVisited(testAccount.getId(), visitedTask);
        verify(lastVisitedRepositoryMock, times(1)).delete(oldest);
        verify(lastVisitedRepositoryMock, times(1)).save(anyCollection());
    }


    @Test
    public void getAccountLastVisited() throws Exception {
        List<LastVisited> lastVisitedTasks = TestUtils.createLastVisitedTasks(4);
        when(lastVisitedRepositoryMock.findByAccountOrderByTimeAsc(testAccount.getId())).thenReturn(lastVisitedTasks);
        Set<LastVisited> accountLastVisited = visitedService.getAccountLastVisited(testAccount.getId());
        assertTrue(accountLastVisited.size() == 4);

    }
}