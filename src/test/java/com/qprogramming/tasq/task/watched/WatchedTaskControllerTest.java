package com.qprogramming.tasq.task.watched;

import com.qprogramming.tasq.account.Account;
import com.qprogramming.tasq.account.Roles;
import com.qprogramming.tasq.events.DisplayEvent;
import com.qprogramming.tasq.events.Event;
import com.qprogramming.tasq.support.ResultData;
import com.qprogramming.tasq.task.Task;
import com.qprogramming.tasq.task.TaskService;
import com.qprogramming.tasq.test.MockSecurityContext;
import com.qprogramming.tasq.test.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static com.qprogramming.tasq.test.TestUtils.TEST_1;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Remote on 01.02.2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WatchedTaskControllerTest {

    @Mock
    private MessageSource msgMock;
    @Mock
    private WatchedTaskRepository watchRepoMock;
    @Mock
    private TaskService taskSrvMock;
    @Mock
    private MockSecurityContext securityMock;
    @Mock
    private Authentication authMock;


    private WatchedTaskService watchSrv;
    private WatchedTaskController watchCtrl;
    private Account testAccount;


    @Before
    public void setUp() throws Exception {
        testAccount = TestUtils.createAccount();
        testAccount.setLanguage("en");
        when(msgMock.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("MESSAGE");
        when(securityMock.getAuthentication()).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(testAccount);
        SecurityContextHolder.setContext(securityMock);
        watchSrv = new WatchedTaskService(watchRepoMock);
        watchCtrl = new WatchedTaskController(watchSrv, msgMock, taskSrvMock);
    }

    @Test
    public void watchNotUserTest() throws Exception {
        testAccount.setRole(Roles.ROLE_VIEWER);
        ResultData resultData = watchCtrl.watch(TEST_1);
        assertEquals(ResultData.Code.ERROR, resultData.code);
    }

    @Test
    public void watchStartTest() throws Exception {
        Task task = TestUtils.createTask(TestUtils.TASK_NAME, 1, TestUtils.createProject());
        when(taskSrvMock.findById(TEST_1)).thenReturn(task);
        ResultData resultData = watchCtrl.watch(TEST_1);
        verify(watchRepoMock, times(1)).save(any(WatchedTask.class));
        assertEquals(ResultData.Code.OK, resultData.code);
    }

    @Test
    public void watchStopTest() throws Exception {
        WatchedTask watched = new WatchedTask();
        Set<Account> watchers = new HashSet<>();
        watchers.add(testAccount);
        watched.setWatchers(watchers);
        watched.setId(TEST_1);
        Task task = TestUtils.createTask(TestUtils.TASK_NAME, 1, TestUtils.createProject());
        when(taskSrvMock.findById(TEST_1)).thenReturn(task);
        when(watchRepoMock.findById(TEST_1)).thenReturn(watched);
        assertTrue("Test account is not watching , but is in watchers", watchSrv.isWatching(TEST_1));
        ResultData resultData = watchCtrl.watch(TEST_1);
        verify(watchRepoMock, times(1)).save(any(WatchedTask.class));
        assertEquals(ResultData.Code.OK, resultData.code);
    }

    @Test
    public void getWatchesTest() throws Exception {
        WatchedTask watched = new WatchedTask();
        Set<Account> watchers = new HashSet<>();
        watchers.add(testAccount);
        watched.setWatchers(watchers);
        watched.setId(TEST_1);
        WatchedTask watched2 = new WatchedTask();
        watched2.setId(TestUtils.TEST_2);
        List<WatchedTask> list = new ArrayList<>();
        list.add(watched);
        list.add(watched2);
        assertNotEquals(watched, watched2);
        assertNotEquals(watched.hashCode(), watched2.hashCode());
        assertEquals("Count is not correct for watched task", 1, watched.getCount());
        Page<WatchedTask> page = new PageImpl<>(list);
        Pageable pageSpecification = new PageRequest(0, 5);
        when(watchRepoMock.findByWatchersId(testAccount.getId(), pageSpecification)).thenReturn(page);
        Page<DisplayWatch> watches = watchCtrl.getWatches(null, pageSpecification);
        assertEquals(2, watches.getTotalElements());
    }

    @Test
    public void addToWatchesTest(){
        Account account = TestUtils.createAccount("John", "Doe");
        Task task = TestUtils.createTask(TestUtils.TASK_NAME, 1, TestUtils.createProject());
        when(taskSrvMock.findById(TEST_1)).thenReturn(task);
        watchSrv.addToWatchers(task, account);
        verify(watchRepoMock, times(1)).save(any(WatchedTask.class));
    }

}